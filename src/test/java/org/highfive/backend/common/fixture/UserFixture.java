package org.highfive.backend.common.fixture;

import org.highfive.backend.user.entity.User;

public class UserFixture {

    public static User createUser (Long id){
        return User.builder()
                .id(id)
                .name("황지연")
                .profileUrl("s3://profileUrl")
                .build();
    }
}
