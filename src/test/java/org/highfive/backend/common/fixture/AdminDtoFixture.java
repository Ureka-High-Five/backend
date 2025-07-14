package org.highfive.backend.common.fixture;

import java.util.List;
import org.highfive.backend.content.dto.request.AdminAddContentRequestDto;
import org.highfive.backend.content.dto.request.AdminUpdateContentRequestDto;

public class AdminDtoFixture {

    public static AdminAddContentRequestDto getAdminAddContentRequestDto() {
        return AdminAddContentRequestDto.builder()
                .title("test-title")
                .description("test-desc")
                .videoUrl("test-videourl")
                .postUrl("test-posturl")
                .countryName("KR")
                .openDate("2025-01-01")
                .runningTime(100)
                .totalRound(10)
                .type("MOVIE")
                .genres(List.of("thriller"))
                .actors(List.of("톰 크루즈"))
                .director("딘 데블로이스")
                .build();
    }

    public static AdminUpdateContentRequestDto getValidAdminUpdateContentRequestDto() {
        return AdminUpdateContentRequestDto.builder()
                .contentId(1L)
                .title("test-title")
                .description("test-desc")
                .videoUrl("test-videourl")
                .postUrl("test-posturl")
                .countryName("KR")
                .openDate("2025-01-01")
                .runningTime(100)
                .totalRound(10)
                .type("MOVIE")
                .genres(List.of("액션"))
                .actors(List.of("톰 크루즈"))
                .director("딘 데블로이스")
                .grade(15)
                .build();
    }

    public static AdminUpdateContentRequestDto getInvalidAdminUpdateContentRequestDto() {
        return AdminUpdateContentRequestDto.builder()
                .title("test-title")
                .description("test-desc")
                .videoUrl("test-videourl")
                .postUrl("test-posturl")
                .countryName("KR")
                .openDate("2025-01-01")
                .runningTime(100)
                .totalRound(10)
                .type("MOVIE")
                .genres(List.of("액션"))
                .actors(List.of("톰 크루즈"))
                .director("딘 데블로이스")
                .grade(15)
                .build();
    }
}
