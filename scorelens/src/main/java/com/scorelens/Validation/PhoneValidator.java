package com.scorelens.Validation;

import com.scorelens.Constants.RegexConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Cho phép null
        if (Objects.isNull(value)) {
            return true;
        }
        
        // Nếu không null thì validate theo regex
        return value.matches(RegexConstants.VIETNAMESE_PHONE);
    }
}
