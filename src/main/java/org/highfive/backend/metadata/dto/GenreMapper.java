package org.highfive.backend.metadata.dto;

import java.util.HashMap;
import java.util.Map;

public class GenreMapper {
    public static final Map<String, String> CONVERT_DB_GENRE = new HashMap<>();
    public static final Map<String,String> CONVERT_HOME_GENRE = new HashMap<>();

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

    static {
        CONVERT_HOME_GENRE.put("Horror", "공포");
        CONVERT_HOME_GENRE.put("Action", "액션");
        CONVERT_HOME_GENRE.put("Thriller", "스릴러");
        CONVERT_HOME_GENRE.put("Mystery", "미스터리");
        CONVERT_HOME_GENRE.put("Family", "가족");
        CONVERT_HOME_GENRE.put("Comedy", "코미디");
        CONVERT_HOME_GENRE.put("Animation", "애니메이션");
        CONVERT_HOME_GENRE.put("Science Fiction", "SF");
        CONVERT_HOME_GENRE.put("Documentary", "다큐멘터리");
        CONVERT_HOME_GENRE.put("Drama", "드라마");
        CONVERT_HOME_GENRE.put("Romance", "로맨스");
        CONVERT_HOME_GENRE.put("Crime", "범죄");
        CONVERT_HOME_GENRE.put("History", "역사");
        CONVERT_HOME_GENRE.put("Music", "음악");
        CONVERT_HOME_GENRE.put("Children", "키즈");
        CONVERT_HOME_GENRE.put("Adventure", "어드벤처");
        CONVERT_HOME_GENRE.put("Fantasy", "판타지");
        CONVERT_HOME_GENRE.put("War", "전쟁");
        CONVERT_HOME_GENRE.put("Talkshow", "토크쇼");
    }
}
