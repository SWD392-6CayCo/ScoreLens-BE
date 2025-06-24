package com.scorelens.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = { PhoneValidator.class }
)
public @interface PhoneConstraint {
    String message() default "Phone number must start with 0 and contain 9 or 10 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
