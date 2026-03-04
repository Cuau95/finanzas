package com.cuau.finanzas.infrastructure.controller.validator;

import com.cuau.finanzas.domain.enums.CronologyType;
import com.cuau.finanzas.infrastructure.dto.TransactionDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionsValidator implements ConstraintValidator<ValidTransactions, TransactionDto> {

	@Override
	public boolean isValid(TransactionDto value, ConstraintValidatorContext context) {
		// Rule: Endpoint only MUST create Transactions with CronologyType.ACTUAL
		boolean isActualCronologyType = CronologyType.ACTUAL.equals(value.getCronologyType());
		if (!isActualCronologyType) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					"INVALID CRONOLOGY TYPE: Transactions created by this endpoint must be cronologyType: ACTUAL")
					.addPropertyNode("cronologyType").addConstraintViolation();
		}
		return isActualCronologyType;
	}

}
