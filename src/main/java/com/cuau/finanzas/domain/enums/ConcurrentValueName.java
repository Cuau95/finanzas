package com.cuau.finanzas.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConcurrentValueName {

	CREDIT_BALANCE("Saldo Credito"), DEBIT_BALANCE("Saldo Debito"), TOTAL_SAVING_BUCKET("Acumulado Apartados");

	private final String name;

	ConcurrentValueName(String description) {
		this.name = description;
	}

	@JsonValue
	public String getName() {
		return this.name;
	}

}
