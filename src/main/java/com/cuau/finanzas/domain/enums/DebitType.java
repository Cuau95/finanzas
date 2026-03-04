package com.cuau.finanzas.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DebitType {

	INCOME(DebitCategory.INCOME),
	TAKE_BUCKET(DebitCategory.INCOME),
	NOMINA(DebitCategory.INCOME),
	INCOME_CONCURRENT(DebitCategory.INCOME),
	EXPENSE(DebitCategory.EXPENSE),
	AUTODEBIT(DebitCategory.EXPENSE),
	EXPENSE_CONCURRENT(DebitCategory.EXPENSE),
	ADD_BUCKET(DebitCategory.EXPENSE);
	

	private final DebitCategory category;

	public boolean isExpense() {
		return category == DebitCategory.EXPENSE;
	}

	public boolean isIncome() {
		return category == DebitCategory.INCOME;
	}

}
