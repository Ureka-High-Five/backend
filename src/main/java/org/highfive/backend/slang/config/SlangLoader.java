package org.highfive.backend.slang.config;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.slang.Slang;
import org.highfive.backend.slang.SlangRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlangLoader {

    private static final String BASE_FILE_NAME = "slang.csv";
    private static final String EXTERNAL_FILE_NAME = "slang-ex.csv";

    private final SlangRepository slangRepository;

    @PostConstruct
    public void load() {
//        loadSlangFile(BASE_FILE_NAME); // 기존 비속어 추가
        loadSlangFile(EXTERNAL_FILE_NAME); // 추가한 비속어 추가
    }

    private void loadSlangFile(String fileName) {
        InputStream is = getInputStream(fileName);

        if (is == null) {
            return;
        }

        saveWord(is);
    }

    private static InputStream getInputStream(String fileName) {
        InputStream is = SlangLoader.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (is == null) {
            log.info("resources 폴더에서 {} 파일을 찾을 수 없습니다", EXTERNAL_FILE_NAME);
        }

        return is;
    }

    private void saveWord(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            String header = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] field = line.split(",");
                String word = field[0].replace("\"", "");
                if (word.isEmpty()) continue;

                // DB에 없으면 저장
                if (!slangRepository.existsByWord(word)) {
                    slangRepository.save(new Slang(word));
                    log.info("새 금칙어 저장: {}", word);
                }
            }
        } catch (IOException e) {
            log.error("{} 파일 읽기 중 오류 발생", EXTERNAL_FILE_NAME, e);
        }
    }
}
