package org.highfive.backend.metadata.dto;

import java.util.HashMap;
import java.util.Map;

public class GenreMapper {
    public static final Map<String, String> CONVERT_DB_GENRE = new HashMap<>();

    static {
        CONVERT_DB_GENRE.put("Horror", "공포");
        CONVERT_DB_GENRE.put("Action", "액션");
        CONVERT_DB_GENRE.put("Thriller", "스릴러");
        CONVERT_DB_GENRE.put("Mystery", "미스터리");
        CONVERT_DB_GENRE.put("Family", "가족");
        CONVERT_DB_GENRE.put("Comedy", "코미디");
        CONVERT_DB_GENRE.put("Animation", "애니메이션");
        CONVERT_DB_GENRE.put("Science Fiction", "SF");
        CONVERT_DB_GENRE.put("Documentary", "다큐멘터리");
        CONVERT_DB_GENRE.put("Drama", "드라마");
        CONVERT_DB_GENRE.put("Romance", "로맨스");
        CONVERT_DB_GENRE.put("Crime", "범죄");
        CONVERT_DB_GENRE.put("History", "역사");
        CONVERT_DB_GENRE.put("Music", "음악");
        CONVERT_DB_GENRE.put("Children", "Kids");
        CONVERT_DB_GENRE.put("Adventure", "Action & Adventure");
        CONVERT_DB_GENRE.put("Fantasy", "Sci-Fi & Fantasy");
        CONVERT_DB_GENRE.put("War", "War & Politics");
        CONVERT_DB_GENRE.put("Talkshow", "Talk");
    }
}
