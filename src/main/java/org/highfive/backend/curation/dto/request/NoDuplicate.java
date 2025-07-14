package org.highfive.backend.curation.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicateValidator.class)
@Documented
public @interface NoDuplicate {
    String message() default "중복된 값이 존재합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
