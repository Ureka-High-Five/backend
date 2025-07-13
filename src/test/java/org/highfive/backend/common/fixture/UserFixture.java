package org.highfive.backend.common.fixture;

import org.highfive.backend.user.entity.User;

public class UserFixture {

    public static User createUser() {
        return User.builder()
                .id(1L)
                .embedding("test-embedding")
                .build();
    }
}
