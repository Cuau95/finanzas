package com.cuau.finanzas.domain.model;

import com.cuau.finanzas.domain.enums.DebitType;

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
@DiscriminatorValue("DEBIT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebitTransaction extends Transaction {
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DebitType debitType;

}
