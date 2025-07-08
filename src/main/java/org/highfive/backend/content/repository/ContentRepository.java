package org.highfive.backend.content.repository;

import org.highfive.backend.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content,Long> {
}
