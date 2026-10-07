package com.maharram.uberclone.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be 8+ characters, with at least one uppercase, one lowercase, and one number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
