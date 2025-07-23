package org.highfive.backend.slang;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.slang.service.AhoCorasickSlangFilterService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlangValidator {

    private static AhoCorasickSlangFilterService staticSlangFilter;

    private final AhoCorasickSlangFilterService ahoCorasickSlangFilterService;

    @PostConstruct
    public void init() {
        staticSlangFilter = this.ahoCorasickSlangFilterService;
    }

    public static void validate(String input) {
        boolean isUnsafe = staticSlangFilter.filteringSlang(input);
        if (isUnsafe) {
            throw new BusinessException(SlangErrorCode.SLANG_INPUT);
        }
    }
}
