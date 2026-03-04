package com.cuau.finanzas.infrastructure.controller.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditPaymentsValidator.class)
@Documented
public @interface ValidCreditPayments {

	String message() default "totalPayments must be greater or equal than numberActualPayment";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
