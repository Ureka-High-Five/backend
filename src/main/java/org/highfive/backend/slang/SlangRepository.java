package org.highfive.backend.slang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlangRepository extends JpaRepository<Slang, Long> {
    void deleteByWord(String word);        // 단어로 삭제
    Slang findByWord(String word);           // 단어로 찾기
}
