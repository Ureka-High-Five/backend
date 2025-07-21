package org.highfive.backend.content.repository.querydsl;

import java.util.List;
import java.util.Optional;
import org.highfive.backend.content.dto.response.OnboardingContentDto;
import org.highfive.backend.content.entity.Content;

public interface ContentQueryRepository {

    public List<OnboardingContentDto> findContentsByGenres(List<String> genres, long genreCount);

    Optional<Content> findWithMetaInfoById(Long contentId);
}
