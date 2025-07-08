package org.highfive.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequestDto(
	@NotNull
	Long contentId,
	@NotNull
	Integer rating,
	@Size(max = 255)
	String review
) {
}
