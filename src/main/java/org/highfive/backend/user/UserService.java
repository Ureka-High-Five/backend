package org.highfive.backend.user;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.client.fastapi.dto.FastApiOnboardingResponseDto;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.code.UserErrorCode;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.preference.PreferMetaInfoRepository;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final FastApiClient fastApiClient;
    private final UserRepository userRepository;
    private final PreferMetaInfoRepository preferMetaInfoRepository;

    public void initUser(SubmitOnboardingRequestDto request) {
        long userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND_ERROR));
        initBasic(request, user);
        initVector(request, user);
    }

    private void initBasic(SubmitOnboardingRequestDto request, User user) {
        int age = request.year();
        Gender gender = request.gender();
        String name = request.name();
        user.setAge(age);
        user.setGender(gender);
        user.setName(name);
    }

    private void initVector(SubmitOnboardingRequestDto request, User user) {
        List<Long> contentIds = request.selectedContentIds();
        FastApiOnboardingResponseDto response = fastApiClient.onboarding(contentIds);
        String vector = response.vector(); // 사용자 벡터
        // 사용자 가중치

        user.setEmbedding(vector);
        // todo 가중치 저장
        // todo 가중치 key 매퍼 만들기
    }
}
