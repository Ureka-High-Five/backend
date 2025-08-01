package org.highfive.backend.content.repository.jpa;

import org.highfive.backend.content.entity.ContentVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ContentVectorRepository extends JpaRepository<ContentVector, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO contents_vector (content_id, embedding, created_at, updated_at)
        VALUES (:contentId, CAST(:embedding AS vector), now(), now())
        ON CONFLICT (content_id) DO UPDATE 
        SET embedding = CAST(:embedding AS vector), updated_at = now()
    """, nativeQuery = true)
    void upsertContentVector(Long contentId, String embedding);
}
