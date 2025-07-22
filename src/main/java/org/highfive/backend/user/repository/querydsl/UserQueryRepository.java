package org.highfive.backend.user.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.highfive.backend.global.dto.CursorPageResponse;
import org.highfive.backend.user.dto.mapper.UserMapper;
import org.highfive.backend.user.dto.response.GetAllUserResponseDto;
import org.highfive.backend.user.dto.response.SearchUserResponseDto;
import org.highfive.backend.user.entity.QUser;
import org.highfive.backend.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

    public CursorPageResponse<GetAllUserResponseDto> findByCursor(Long cursor, Integer size) {
        List<User> findByCursor = jpaQueryFactory.selectFrom(user)
                .where(
                        cursorFilter(cursor)
                ).orderBy(user.id.asc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = findByCursor.size() > size;
        Long nextCursor = hasNext ? findByCursor.getLast().getId() : null;
        List<GetAllUserResponseDto> result = findByCursor.stream()
                .map(UserMapper::toGetAllUserResponseDto)
                .limit(size)
                .toList();
        return new CursorPageResponse<>(result, hasNext, String.valueOf(nextCursor));
    }

    public CursorPageResponse<SearchUserResponseDto> findByNameContaining(String username, Long cursor, int size) {
        List<User> findUsers = jpaQueryFactory.selectFrom(user)
                .where(
                        user.name.toLowerCase().like("%" + username.toLowerCase() + "%"),
                        cursorFilter(cursor)
                ).orderBy(user.id.asc())
                .limit(size + 1)
                .fetch();
        boolean hasNext = findUsers.size() > size;
        Long nextCursor = hasNext ? findUsers.getLast().getId() : null;
        List<SearchUserResponseDto> result = findUsers.stream()
                .map(UserMapper::toSearchUserResponseDto)
                .limit(size)
                .toList();
        return new CursorPageResponse<>(result, hasNext, String.valueOf(nextCursor));
    }

    private BooleanExpression cursorFilter(Long cursor) {
        if (cursor == null || String.valueOf(cursor).isBlank()) {
            return null;
        }
        return user.id.goe(cursor);
    }
}
