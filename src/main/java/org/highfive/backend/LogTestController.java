package org.highfive.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {

    @GetMapping("/log")
    public String logTest() {
        log.info("CloudWatch 연동 테스트 - 로그 출력됨!");
        return "로그 테스트 완료";
    }
}
