package org.highfive.backend.shorts.service;

import java.util.ArrayList;
import java.util.List;
import org.highfive.backend.shorts.dto.ShortsDto;

public class TestShortsDtoFactory {

    public static ShortsDto createShorts(Long id, Long contentId) {
        return new ShortsDto(
                id,
                "http://example.com/shorts/" + id,
                "http://example.com/shorts/" + id + ".jpg",
                contentId,
                "Test Shorts " + id
        );
    }

    public static List<ShortsDto> createManyShorts(long cnt) {
        List<ShortsDto> result = new ArrayList<>();
        for (long idx = 1L; idx <= cnt; idx++) {
            result.add(createShorts(idx, idx));
        }

        return result;
    }
}
