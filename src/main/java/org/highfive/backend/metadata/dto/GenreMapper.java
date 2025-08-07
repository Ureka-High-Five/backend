package org.highfive.backend.metadata.dto;

import java.util.HashMap;
import java.util.Map;

public class GenreMapper {
    public static final Map<String, String> CONVERT_GENRE = new HashMap<>();

    static {
        CONVERT_GENRE.put("Horror", "공포");
        CONVERT_GENRE.put("Action", "액션");
        CONVERT_GENRE.put("Thriller", "스릴러");
        CONVERT_GENRE.put("Mystery", "미스터리");
        CONVERT_GENRE.put("Family", "가족");
        CONVERT_GENRE.put("Comedy", "코미디");
        CONVERT_GENRE.put("Animation", "애니메이션");
        CONVERT_GENRE.put("Science Fiction", "SF");
        CONVERT_GENRE.put("Documentary", "다큐멘터리");
        CONVERT_GENRE.put("Fantasy", "판타지");
        CONVERT_GENRE.put("Drama", "드라마");
        CONVERT_GENRE.put("Adventure", "모험");
        CONVERT_GENRE.put("Romance", "로맨스");
        CONVERT_GENRE.put("Crime", "범죄");
        CONVERT_GENRE.put("History", "역사");
        CONVERT_GENRE.put("Music", "음악");
        CONVERT_GENRE.put("War", "전쟁");
        CONVERT_GENRE.put("Talkshow", "토크쇼");
        CONVERT_GENRE.put("Reality", "리얼리티");
    }
}
