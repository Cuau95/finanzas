package com.cuau.finanzas.domain.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cuau.finanzas.domain.enums.CronologyType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "transaction_type")
public abstract class Transaction {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private LocalDate date;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CronologyType cronologyType;
	@Column(precision = 8, scale = 2, nullable=false)
	private BigDecimal amount;
	
}
