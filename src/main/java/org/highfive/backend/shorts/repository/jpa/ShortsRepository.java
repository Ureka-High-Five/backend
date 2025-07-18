package org.highfive.backend.shorts.repository.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.highfive.backend.shorts.entity.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {

    @Modifying
    @Query("update Shorts s set s.likeCount = s.likeCount + 1 where s.id = :id")
    void increaseLike(Long id);

    @Modifying
    @Query("update Shorts s set s.likeCount = s.likeCount - 1 where s.id = :id and s.likeCount > 0")
    void decreaseLike(Long id);

    @Query("SELECT s FROM Shorts s WHERE s.content.id = :contentId ORDER BY function('RAND')")
    Optional<Shorts> findRandomByContentId(@Param("contentId") Long contentId);


}
