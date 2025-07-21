package org.highfive.backend.user.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoUserId(String kakaoUserId);

    List<User> findByNameContaining(String username);
}
