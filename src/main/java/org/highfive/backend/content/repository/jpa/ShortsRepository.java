package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.shorts.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {

    @Modifying
    @Query("update Shorts s set s.likeCount = s.likeCount + 1 where s.id = :id")
    void increaseLike(Long id);
}
