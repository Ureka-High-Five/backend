package org.highfive.backend.common.fixture;

import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;

public class UserFixture {

    public static User createEmbeddingUser() {
        return User.builder()
                .embedding("test-embedding")
                .age(25)
                .averageRating(10)
                .build();
    }

    public static User createDefaultUser() {
        return User.builder()
                .name("test-name")
                .profileUrl("test-profile")
                .userRole(UserRole.USER)
                .embedding("[1, 1, 1]")
                .age(25)
                .averageRating(10)
                .kakaoUserId("test-kakao-id")
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
