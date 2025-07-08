package org.highfive.backend.user.repository;

import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByKakaoUserId(String kakaoUserId);

    Optional<User> findByKakaoUserId(String kakaoUserId);
}
