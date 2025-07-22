package org.highfive.backend.user.repository.jpa;

import java.util.List;
import org.highfive.backend.user.dto.response.SearchUserResponseDto;
import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoUserId(String kakaoUserId);

    @Modifying
    @Query(value =
            """
                INSERT INTO users_vector (user_id, embedding, created_at, updated_at)
                VALUES (:userId, :embedding, now(), now())  
                ON CONFLICT (user_id) DO UPDATE SET embedding = :embedding, updated_at = now()
            """, nativeQuery = true)
    void upsertUserVector(Long userId, String embedding);
}
