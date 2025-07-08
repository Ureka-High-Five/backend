package org.highfive.backend.content.dto.request;

public record CreateReviewRequestDto(
	Long contentId,
	Integer rating,
	String review
) {
}
