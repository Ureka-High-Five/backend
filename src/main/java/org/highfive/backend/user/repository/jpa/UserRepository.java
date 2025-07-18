package org.highfive.backend.user.repository.jpa;

import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoUserId(String kakaoUserId);
}
