package org.highfive.backend.user.service;

import static org.highfive.backend.user.exception.UserErrorCode.USER_NOT_FOUND_ERROR;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLogService {

    private final UserRepository userRepository;

    @Transactional
    public Response<Void> updateUserWatchInfo(final CreateContentWatchLogRequestDto request, final User user) {

        final long previousViewCount = Optional.ofNullable(user.getViewCount()).orElse(0L);
        final long newViewCount = previousViewCount + 1;

        final long previousAverageViewTime = Optional.ofNullable(user.getAverageViewTime()).orElse(0L);
        final long totalWatchTime = previousAverageViewTime * previousViewCount + request.watchTime();
        final long newAverageViewTime = totalWatchTime / newViewCount;

        User existedUser = userRepository.findById(user.getId()).
                orElseThrow(() -> new BusinessException(USER_NOT_FOUND_ERROR));

        existedUser.updateUserWatchInfo(newAverageViewTime, newViewCount);

        return Response.ok(null);
    }

}
