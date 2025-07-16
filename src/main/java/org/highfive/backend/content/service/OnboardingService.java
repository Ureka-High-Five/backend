package org.highfive.backend.content.service;

import static org.highfive.backend.global.code.SuccessCode.OK;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.content.dto.request.OnboardingSelectContentRequestDto;
import org.highfive.backend.content.dto.response.GenreCountDto;
import org.highfive.backend.content.dto.response.MostPopularContentPerGenreDto;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.dto.response.OnboardingInitContentsResponseDto;
import org.highfive.backend.content.dto.response.OnboardingSelectContentResponseDto;
import org.highfive.backend.metadata.entity.MetaInfo;
import org.highfive.backend.metadata.entity.MetaType;
import org.highfive.backend.content.exception.ContentErrorCode;
import org.highfive.backend.content.repository.jpa.ContentRepository;
import org.highfive.backend.content.repository.querydsl.ContentQueryRepositoryImpl;
import org.highfive.backend.metadata.repository.jpa.MetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiOnboardingResponseDto;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.global.util.WeightManager;
import org.highfive.backend.user.code.UserErrorCode;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;
import org.highfive.backend.user.entity.preference.PreferMetaInfo;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final int INIT_CONTENT_CNT = 6;
    private static final int RESULT_CONTENT_COUNT = 3;

    private final TokenService tokenService;
    private final FastApiClient fastApiClient;
    private final WeightManager weightManager;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final PreferMetaInfoRepository preferMetaInfoRepository;
    private final ContentQueryRepositoryImpl contentQueryRepositoryImpl;
    private final MetaInfoRepository metaInfoRepository;

    public Response<List<OnboardingSelectContentResponseDto>> getContentBySelectedContent(final OnboardingSelectContentRequestDto request) {
        List<GenreCountDto> topGenresByContentIds = contentQueryRepositoryImpl.findTopGenresByContentIds(request.selectedContentIds());
        List<OnboardingSelectContentResponseDto> contents = getOnboardingSelectContentResponseDtos(
                topGenresByContentIds.stream().map((GenreCountDto::genre)).toList(), request);

        if (contents.isEmpty()) {
            return new Response<>(SuccessCode.NO_CONTENT.getCode(), contents, null);
        }
        return new Response<>(SuccessCode.OK.getCode(), contents, null);
    }

    @Transactional
    public Response<TokenResponseDto> initUser(final SubmitOnboardingRequestDto request) {
        long userId = request.userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND_ERROR));
        initBasic(request, user);
        initVector(request, user);

        final String kakaoUserId = user.getKakaoUserId();
        return tokenResponse(kakaoUserId, List.of(user.getUserRole()));
    }

    public Response<List<OnboardingInitContentsResponseDto>> getDistinctGenreTopContents() {
        List<MostPopularContentPerGenreDto> topContents = contentRepository.findTopContentPerGenre(INIT_CONTENT_CNT);

        if (topContents.isEmpty()) {
            throw new BusinessException(ContentErrorCode.CONTENT_NOT_FOUND);
        }
        List<OnboardingInitContentsResponseDto> initContents = topContents.stream()
                .map(dto -> new OnboardingInitContentsResponseDto(dto.id(), dto.thumbnailUrl(), dto.title(), dto.openYear()))
                .toList();

        return new Response<>(SuccessCode.OK.getCode(), initContents, null);
    }

    private Response<TokenResponseDto> tokenResponse(final String kakaoUserId, final List<UserRole> userRoles) {
        final TokenResponseDto tokens = createToken(kakaoUserId, userRoles.stream().map(String::valueOf).toList());
        return new Response<>(OK.getCode(), tokens, OK.getMessage());
    }

    private TokenResponseDto createToken(final String kakaoUserId, final List<String> roles) {
        final String accessToken = tokenService.generateAccessToken(kakaoUserId, roles);
        final String refreshToken = tokenService.generateRefreshToken(kakaoUserId, roles);
        return new TokenResponseDto(accessToken, refreshToken, true);
    }

    private List<OnboardingSelectContentResponseDto> getOnboardingSelectContentResponseDtos(List<String> topGenres, OnboardingSelectContentRequestDto request) {
        List<OnboardingContentDto> result = contentQueryRepositoryImpl.findContentsByGenresOrderByMatchCountDesc(topGenres);
        result = duplicateFilter(result, request);
        return result.stream().map(
                c -> new OnboardingSelectContentResponseDto(c.id(), c.postUrl(), c.title(), c.openDate().getYear()))
                .toList();
    }

    private List<OnboardingContentDto> duplicateFilter(final List<OnboardingContentDto> contents, final OnboardingSelectContentRequestDto request) {
        final List<OnboardingContentDto> result = new ArrayList<>();
        for (OnboardingContentDto content : contents) {
            if (request.selectedContentIds().contains(content.id())) {
                continue;
            }
            result.add(content);
            if (result.size() == RESULT_CONTENT_COUNT) {
                break;
            }
        }
        return result;
    }

    private void initBasic(final SubmitOnboardingRequestDto request, final User user) {
        int age = Year.now().getValue() - request.birthYear();
        Gender gender = request.gender();
        String name = request.name();
        user.updateBasicInfo(name, age, gender, UserRole.USER);
    }

    private void initVector(final SubmitOnboardingRequestDto request, final User user) {
        List<Long> contentIds = request.selectedContentIds();
        Map<String, Integer> genreCount = countGenre(contentIds);
        FastApiOnboardingResponseDto response = fastApiClient.onboardingSubmit(genreCount);

        String vector = response.userVector();
        user.updateEmbedding(vector);

        Map<String, Double> genreWeights = weightManager.calcWeight(genreCount);
        updateUserWeight(user, genreWeights);
    }

    private Map<String, Integer> countGenre(final List<Long> contentIds) {
        final Map<String, Integer> genreCount = new HashMap<>();

        List<Map<String, Object>> results = contentQueryRepositoryImpl.findContentGenresByContentIds(contentIds);

        for (Map<String, Object> row : results) {
            String genreName = (String) row.get("genreName");
            genreCount.put(genreName, genreCount.getOrDefault(genreName, 0) + 1);
        }
        return genreCount;
    }

    private void updateUserWeight(final User user, final Map<String, Double> genreWeights) {
        for (Map.Entry<String, Double> entry : genreWeights.entrySet()) {
            final String genreName = entry.getKey();
            final double weight = entry.getValue();
            
            if (weight == 0) {
                continue;
            }

            final MetaInfo metaInfo = metaInfoRepository.findByNameAndType(genreName, MetaType.GENRE);
            Long metaInfoId = metaInfo.getId();
            PreferMetaInfo preferMetaInfo = preferMetaInfoRepository.findByMetaInfoAndUser(metaInfoId, user.getId()).orElse(null);

            if (preferMetaInfo != null) {
                preferMetaInfo.updateWeight(weight);
                return;
            }

            saveUserWeight(user, weight, metaInfo);
        }
    }

    private void saveUserWeight(User user, double weight, MetaInfo metaInfo) {
        final PreferMetaInfo prefer = PreferMetaInfo.builder()
                .user(user)
                .weight(weight)
                .metaInfo(metaInfo)
                .build();
        preferMetaInfoRepository.save(prefer);
    }
}
