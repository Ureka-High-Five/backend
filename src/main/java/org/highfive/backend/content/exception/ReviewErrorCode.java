package org.highfive.backend.content.exception;

import org.highfive.backend.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {


	REVIEW_001(42200,"리뷰에 부적절한 단어가 포함되어있습니다.", HttpStatus.UNPROCESSABLE_ENTITY);

	private final int code;
	private final String message;
	private final HttpStatus httpStatus;



	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}
}
