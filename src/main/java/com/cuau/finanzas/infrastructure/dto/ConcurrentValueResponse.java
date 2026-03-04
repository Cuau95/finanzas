package com.cuau.finanzas.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConcurrentValueResponse(Long id, String name, BigDecimal amount, LocalDateTime lastUpdateDate) {
}
