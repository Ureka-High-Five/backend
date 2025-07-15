package org.highfive.backend.curation.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.request.CurationUpdateRequestDto;
import org.highfive.backend.auth.aop.EditorOnly;
import org.highfive.backend.curation.dto.response.CurationDetailResponseDto;
import org.highfive.backend.curation.dto.response.MyCurationResponseDto;
import org.highfive.backend.curation.service.CurationService;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/curation")
public class CurationController {

    private final CurationService curationService;

    @EditorOnly
    @PostMapping
    public Response<Void> createCuration(final @AuthenticationPrincipal User user, @Valid @RequestBody final CreateCurationRequestDto createCurationRequestDto) {
        return curationService.create(user, createCurationRequestDto);
    }

    @GetMapping("/{curationId}")
    public Response<CurationDetailResponseDto> getCuration(@PathVariable final Long curationId) {
        return curationService.getCurationDetail(curationId);
    }

    @GetMapping("/me")
    public Response<CursorPageResponse<MyCurationResponseDto>> getMyCurations(
            @AuthenticationPrincipal final User user,
            @RequestParam @Nullable String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return curationService.getMyCurations(user, cursor, size);
    }

    @EditorOnly
    @PutMapping("/{curationId}")
    public Response<Void> updateCuration(@PathVariable final Long curationId,
                                         @RequestBody @Valid final CurationUpdateRequestDto curationUpdateRequestDto,
                                         @AuthenticationPrincipal final User user) {
        return curationService.updateCuration(user, curationId, curationUpdateRequestDto);
    }

    @EditorOnly
    @DeleteMapping("/{curationId}")
    public Response<Void> deleteCuration(@PathVariable final Long curationId, @AuthenticationPrincipal final User user) {
        return curationService.deleteCuration(curationId, user);
    }
}
