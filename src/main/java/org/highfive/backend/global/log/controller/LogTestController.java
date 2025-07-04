package org.highfive.backend.global.log.controller;

import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {
    @GetMapping("/log")
    public String logTest(@Validated @Max(3) @RequestParam String message) {
        log.info("CloudWatch 연동 테스트 - 로그 출력됨!");
        return "로그 테스트 완료";
    }
}
