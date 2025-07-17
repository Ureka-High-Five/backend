package org.highfive.backend.user.repository;

import org.highfive.backend.user.entity.log.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog,Long> {
}
