package com.cuau.finanzas.infrastructure.controller.validator;

import static java.util.Objects.isNull;

import com.cuau.finanzas.infrastructure.dto.CreditTransactionDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditPaymentsValidator implements ConstraintValidator<ValidCreditPayments, CreditTransactionDto> {

	private static final String PROPERTY_NODE_TOTAL_PAYMENTS = "totalPayments";

	@Override
	public boolean isValid(CreditTransactionDto value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // Bean Validation spec
		}
		// Rule: They can be null but If one of them is not null, the other one can't be
		// null
		if (isNull(value.getNumberActualPayment()) && isNull(value.getTotalPayments())) {
			return true;
		} else if (!isNull(value.getNumberActualPayment()) && !isNull(value.getTotalPayments())) {
			// Rule: always totalPayments >= numberActualPayment
			setConstraintViolation(context, "totalPayments must be greater than numberActualPayment",
					PROPERTY_NODE_TOTAL_PAYMENTS);
			return value.getTotalPayments() >= value.getNumberActualPayment();
		}
		setConstraintViolation(context, "Both totalPayments and numberActualPayment must be provided together",
				PROPERTY_NODE_TOTAL_PAYMENTS);
		return false;
	}

	private void setConstraintViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode).addConstraintViolation();
	}

}
