package org.highfive.backend.slang.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.highfive.backend.slang.Slang;
import org.highfive.backend.slang.SlangRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Slf4j
@Configuration
public class TrieConfig {

    private final SlangRepository slangRepository;

    public TrieConfig(SlangRepository slangRepository) {
        this.slangRepository = slangRepository;
    }

    @Bean
    public Trie trie() {
        return trieBuilder().build();
    }

    @Bean
    public Trie.TrieBuilder trieBuilder() {
        List<Slang> all = slangRepository.findAll();
        Trie.TrieBuilder trieBuilder = Trie.builder().ignoreCase().onlyWholeWords(); // ignoreCase: 대소문자 무시, onlyWholeWords: 금칙어 양쪽에 특수문자만 허용

        List<String> wordList = all.stream().map(Slang::getWord).toList();
        trieBuilder.addKeywords(wordList);

        log.info("총 {}개의 비속어가 추가되었습니다.", all.size());
        return trieBuilder;
    }
}
