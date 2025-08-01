package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.ContentVector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentVectorRepository extends JpaRepository<ContentVector, Long> {
}
