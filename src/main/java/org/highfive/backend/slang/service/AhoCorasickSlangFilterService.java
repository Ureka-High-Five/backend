package org.highfive.backend.slang.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AhoCorasickSlangFilterService implements SlangFilterService {

    private final SlangHolder slangHolder;

    @Override
    public boolean filteringSlang(String text) {
        return slangHolder.countSlang(text) > 0;
    }
}
