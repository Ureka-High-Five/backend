package org.highfive.backend.slang.service;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.highfive.backend.slang.Slang;
import org.highfive.backend.slang.SlangRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SlangHolder {

    private final SlangRepository slangRepository;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private volatile Trie trie;
    private Trie.TrieBuilder trieBuilder;

    public int countSlang(String text) {
        rwLock.readLock().lock();
        try {
            // 여기서 writeLock을 얻으려고 하면 데드락 발생(writeLock은 모든 readLock이 해제되길 기다리기 때문에)
            return trie.parseText(text).size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int rebuild() {
        List<Slang> all = slangRepository.findAll();
        List<String> slangList = all.stream().map(Slang::getWord).toList();
        trieBuilder.addKeywords(slangList);
        Trie newTrie = trieBuilder.build();
        log.info("{}개의 비속어가 rebuild 되었습니다.", slangList.size());

        rwLock.writeLock().lock();
        try{
            this.trie = newTrie;
        } finally {
            rwLock.writeLock().unlock();
        }

        return slangList.size();
    }
}
