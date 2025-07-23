package org.highfive.backend.slang.service;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.slang.Slang;
import org.highfive.backend.slang.SlangRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SlangService {

    private final SlangHolder slangHolder;
    private final SlangRepository slangRepository;

    public void resetSlang() {
        slangHolder.rebuild();
    }

    @Transactional
    public String addSlang(String word) {
        Slang findWord = slangRepository.findByWord(word);
        if (findWord != null) {
            log.info("{}는 이미 등록된 금칙어입니다.", word);
            return word;
        }

        slangRepository.save(new Slang(word));
        return null;
    }

    public List<String> addSlang(List<String> words) {
        List<String> failedList = new ArrayList<>();
        for (String word : words) {
            String failedWord = addSlang(word);
            if (failedWord == null) { // 등록이 정상적으로 된 경우
                continue;
            }
            failedList.add(failedWord);
        }

        return failedList;
    }

    @Transactional
    public String deleteSlang(String word) {
        Slang findWord = slangRepository.findByWord(word);
        if (findWord == null) {
            log.info("{}는 등록되지 않은 금칙어입니다.", word);
            return word;
        }
        slangRepository.deleteByWord(word);
        return null;
    }

    @Transactional
    public List<String> deleteSlang(List<String> words) {
        List<String> failedList = new ArrayList<>();
        for (String word : words) {
            String failedWord = deleteSlang(word);
            if (failedWord == null) { // 삭제가 정상적으로 된 경우
                continue;
            }
            failedList.add(failedWord);
        }

        return failedList;
    }
}
