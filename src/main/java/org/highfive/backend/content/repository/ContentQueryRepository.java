package org.highfive.backend.content.repository;

import java.util.List;
import org.highfive.backend.content.dto.response.OnboardingContentDto;

public interface ContentQueryRepository {

    public List<OnboardingContentDto> findContentsByGenres(List<String> genres, long genreCount);
}
