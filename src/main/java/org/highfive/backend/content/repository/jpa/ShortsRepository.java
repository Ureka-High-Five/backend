package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.shorts.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsRepository extends JpaRepository<Shorts,Long> {
}
