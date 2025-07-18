package org.highfive.backend.user.dto.response;

import org.highfive.backend.user.entity.UserRole;

public record SearchUserResponseDto(
        String profileUrl, // 사용자의 프로필 사진
        String email,      // 사용자의 이메일 주소
        UserRole type, // 사용자의 권한
        int visitedCnt, // 사용자의 방문 횟수
        int likeCnt, // 사용자의 좋아요 수
        int reviewCnt,// 사용자의 리뷰 수
        int commentCnt // 사용자의 댓글 수
) {
}
