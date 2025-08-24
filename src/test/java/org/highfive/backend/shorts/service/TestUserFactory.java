package org.highfive.backend.shorts.service;

import org.highfive.backend.user.entity.Gender;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.entity.UserRole;

public class TestUserFactory {

    public static User.UserBuilder createSimpleUserBuilder() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .age(25)
                .gender(Gender.MALE)
                .userRole(UserRole.USER)
                .profileUrl("http://example.com/profile.jpg")
                .averageViewTime(100L)
                .embedding(null)
                .email("test@example.com")
                .averageRating(4.5f)
                .viewCount(10L)
                .kakaoUserId("kakao-12345");
    }

    public static User createSimpleUserWithId(Long id) {
        return createSimpleUserBuilder()
                .id(id)
                .build();
    }

    public static UserCustomBuilder builder() {
        return new UserCustomBuilder();
    }

    public static class UserCustomBuilder {
        private Long id = 1L;
        private String email = "test@example.com";
        private UserRole userRole = UserRole.USER;

        public UserCustomBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserCustomBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserCustomBuilder userRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        public User build() {
            return User.builder()
                    .id(this.id)
                    .name("Test User")
                    .age(25)
                    .gender(Gender.MALE)
                    .userRole(this.userRole)
                    .profileUrl("http://example.com/profile.jpg")
                    .averageViewTime(100L)
                    .embedding(null)
                    .email(this.email)
                    .averageRating(4.5f)
                    .viewCount(10L)
                    .kakaoUserId("kakao-" + this.id)
                    .build();
        }
    }
}
