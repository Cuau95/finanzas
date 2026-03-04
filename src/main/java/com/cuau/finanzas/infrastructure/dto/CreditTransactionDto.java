package com.cuau.finanzas.infrastructure.dto;

import java.math.BigDecimal;

import com.cuau.finanzas.domain.enums.CreditType;
import com.cuau.finanzas.infrastructure.controller.validator.ValidCreditPayments;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ValidCreditPayments
@JsonTypeName("CREDIT")
public class CreditTransactionDto extends TransactionDto {

	@NotNull
	private CreditType creditType;
	@Positive
	private Integer totalPayments;
	@Positive
	private Integer numberActualPayment;
	private BigDecimal interest;

}
