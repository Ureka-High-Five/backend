package org.highfive.backend.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.CreateContentWatchLogRequestDto;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLogService {

    @Transactional
    public Response<Void> updateUserWatchInfo(final CreateContentWatchLogRequestDto request, final User user) {

        final long previousViewCount = Optional.ofNullable(user.getViewCount()).orElse(0L);
        final long newViewCount = previousViewCount + 1;

        final long previousAverage = Optional.ofNullable(user.getAverageViewTime()).orElse(0L);
        final long totalWatchTime = previousAverage * previousViewCount + request.watchTime();
        final long newAverage = totalWatchTime / newViewCount;

        user.updateUserWatchInfo(newAverage, newViewCount);

        return Response.ok(null);
    }

}
