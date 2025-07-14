package org.highfive.backend.common.fixture;

import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;

public class UserFixture {

    public static User createEmbeddingUser() {
        return User.builder()
                .id(1L)
                .embedding("test-embedding")
                .build();
  }
  
    public static User createUser (Long id){
        return User.builder()
                .id(id)
                .name("황지연")
                .profileUrl("s3://profileUrl")
                .kakaoUserId("test-kakaoUserId")
                .build();
    }

    public static User createAdmin(Long id) {
        return User.builder()
                .id(id)
                .name("어드민")
                .userRole(UserRole.ADMIN)
                .age(25)
                .averageRating(10)
                .build();
    }
}
