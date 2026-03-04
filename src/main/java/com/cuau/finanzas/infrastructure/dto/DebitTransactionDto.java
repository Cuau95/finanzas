package com.cuau.finanzas.infrastructure.dto;

import com.cuau.finanzas.domain.enums.DebitType;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("DEBIT")
public class DebitTransactionDto extends TransactionDto{
	
	@NotNull
	private DebitType debitType;

}
