package com.cuau.finanzas.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cuau.finanzas.domain.enums.CronologyType;
import com.cuau.finanzas.infrastructure.controller.validator.ValidTransactions;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = CreditTransactionDto.class, name = "CREDIT"),
		@JsonSubTypes.Type(value = DebitTransactionDto.class, name = "DEBIT") })
@ValidTransactions
public abstract class TransactionDto {

	private Long id;
	@NotBlank
	private String name;
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	@NotNull
	private CronologyType cronologyType;
	@NotNull
	@Positive
	private BigDecimal amount;

}