package org.highfive.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.auth.dto.response.TokenResponseDto;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.highfive.backend.content.entity.metadata.MetaType;
import org.highfive.backend.content.repository.QueryDslContentRepository;
import org.highfive.backend.content.repository.querydsl.QueryDslMetaInfoRepository;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.response.FastApiOnboardingResponseDto;
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

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.highfive.backend.global.code.SuccessCode.OK;

@Service
@RequiredArgsConstructor
public class UserService {

    private final FastApiClient fastApiClient;
    private final WeightManager weightManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final QueryDslContentRepository queryDslContentRepository;
    private final QueryDslMetaInfoRepository queryDslMetaInfoRepository;
    private final PreferMetaInfoRepository preferMetaInfoRepository;

    @Transactional
    public Response<TokenResponseDto> initUser(final SubmitOnboardingRequestDto request) {
        long userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND_ERROR));
        initBasic(request, user);
        initVector(request, user);

        final String kakaoUserId = user.getKakaoUserId();
        return tokenResponse(kakaoUserId, List.of(user.getUserRole()));
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

    private void initBasic(final SubmitOnboardingRequestDto request, final User user) {
        int age = Year.now().getValue() - request.year();
        Gender gender = request.gender();
        String name = request.name();
        user.updateBasicInfo(name, age, gender, UserRole.USER);
    }

    private void initVector(final SubmitOnboardingRequestDto request, final User user) {
        List<Long> contentIds = request.selectedContentIds();
        Map<String, Integer> genreCount = countGenre(contentIds);
        FastApiOnboardingResponseDto response = fastApiClient.onboardingSubmit(genreCount);

        // 벡터 저장
        String vector = response.userVector();
        user.updateEmbedding(vector);

        // 가중치 저장
        Map<String, Double> genreWeights = weightManager.calcWeight(genreCount);
        saveUserWeight(user, genreWeights);
    }

    private void saveUserWeight(final User user, final Map<String, Double> genreWeights) {
        for (Map.Entry<String, Double> entry : genreWeights.entrySet()) {
            final String genreName = entry.getKey();
            final double weight = entry.getValue();

            final MetaInfo metaInfo = queryDslMetaInfoRepository.findByNameAndType(genreName, MetaType.GENRE);

            final PreferMetaInfo prefer = PreferMetaInfo.builder()
                    .user(user)
                    .weight(weight)
                    .metaInfo(metaInfo)
                    .build();

            preferMetaInfoRepository.save(prefer);
        }
    }

    private Map<String, Integer> countGenre(final List<Long> contentIds) {
        final Map<String, Integer> genreCount = new HashMap<>();

        List<Map<String, Object>> results = queryDslContentRepository.findContentGenresByContentIds(contentIds);

        for (Map<String, Object> row : results) {
            String genreName = (String) row.get("genreName");
            genreCount.put(genreName, genreCount.getOrDefault(genreName, 0) + 1);
        }
        return genreCount;
    }
}
