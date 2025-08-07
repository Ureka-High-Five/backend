package org.highfive.backend.slang;

import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.slang.dto.request.SlangFilterRequestDto;
import org.highfive.backend.slang.dto.response.SlangFilterResponseDto;
import org.highfive.backend.slang.service.AhoCorasickSlangFilterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SlangController {

    private final AhoCorasickSlangFilterService slangFilterService;

    @PostMapping("/slang/filter")
    public Response<SlangFilterResponseDto> slangFilter(
            @RequestBody SlangFilterRequestDto request
    ) {
        return Response.ok(new SlangFilterResponseDto(slangFilterService.filteringSlang(request.text())));
    }
}
