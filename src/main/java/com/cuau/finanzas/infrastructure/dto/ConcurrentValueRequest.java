package com.cuau.finanzas.infrastructure.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConcurrentValueRequest(@NotBlank String name, @NotNull BigDecimal amount) {
}
