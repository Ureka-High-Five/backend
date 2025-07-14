package org.highfive.backend.curation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.curation.dto.request.CreateCurationRequestDto;
import org.highfive.backend.curation.dto.response.CurationDetailResponseDto;
import org.highfive.backend.curation.service.CurationService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/curation")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    @PostMapping
    public Response<?> createCuration(final @AuthenticationPrincipal User user, @Valid @RequestBody final CreateCurationRequestDto createCurationRequestDto) {
        return curationService.create(user, createCurationRequestDto);
    }

    @GetMapping("/{curationId}")
    public Response<CurationDetailResponseDto> getCuration(@PathVariable final Long curationId) {
        return curationService.getCurationDetail(curationId);
    }
}
