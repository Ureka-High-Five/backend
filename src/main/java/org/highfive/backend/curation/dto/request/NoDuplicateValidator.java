package org.highfive.backend.curation.dto.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;

public class NoDuplicateValidator implements ConstraintValidator<NoDuplicate, List<Long>> {
    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext constraintValidatorContext) {
        return new HashSet<>(value).size() == value.size();
    }
}
