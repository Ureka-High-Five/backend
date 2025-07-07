package org.highfive.backend.user.repository;

import org.highfive.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByKakaoUserId(String kakaoUserId);
}
