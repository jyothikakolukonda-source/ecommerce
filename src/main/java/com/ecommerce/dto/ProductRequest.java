package com.ecommerce.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
		@NotBlank String name,
		String description,
		@NotNull @DecimalMin("0.01") BigDecimal price,
		@NotNull @Min(0) Integer stock,
		String category,
		String imageUrl) {
}
