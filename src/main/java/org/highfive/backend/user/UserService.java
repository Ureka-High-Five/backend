package org.highfive.backend.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.client.fastapi.FastApiClient;
import org.highfive.backend.global.code.ErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FastApiClient fastApiClient;

    public void initUser(SubmitOnboardingRequestDto request) {
        long userId = request.userId();
        initBasic(request, userId);
        initVector(request);
    }

    private void initBasic(SubmitOnboardingRequestDto request, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_ERROR));
        int age = request.age();
        String gender = request.gender();
        String name = request.name();
        user.setAge(age);
        user.setGender(Gender.valueOf(gender));
        user.setName(name);
    }

    private void initVector(SubmitOnboardingRequestDto request) {
        List<Integer> contentIds = request.selectedContentIds();
        fastApiClient.onboarding(contentIds);
    }
}
