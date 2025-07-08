package org.highfive.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.code.SuccessCode;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.user.dto.request.SubmitOnboardingRequestDto;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/userInfo")
    public Response<Void> submitOnboarding(@Valid @RequestBody final SubmitOnboardingRequestDto request) {
        userService.initUser(request);
        return new Response<>(SuccessCode.OK.getCode(), null, null);
    }
}
