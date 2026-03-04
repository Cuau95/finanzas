package com.cuau.finanzas.domain.model;

import java.math.BigDecimal;

import com.cuau.finanzas.domain.enums.CreditType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CREDIT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditTransaction extends Transaction {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CreditType creditType;
	private Integer totalPayments;
	private Integer numberActualPayment;
	@Column(precision = 8, scale = 2)
	private BigDecimal interest;
	
}
