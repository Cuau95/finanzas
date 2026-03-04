package com.cuau.finanzas.domain.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcurrentValue {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String name;
	@Column(precision = 8, scale = 2, nullable=false)
	private BigDecimal amount;
	@Column(name = "last_update_date")
	private LocalDateTime lastUpdateDate;

}
