package com.ecommerce.dto;

import java.math.BigDecimal;

import com.ecommerce.model.OrderItem;

public record OrderItemResponse(
		Long productId,
		String productName,
		Integer quantity,
		BigDecimal unitPrice,
		BigDecimal subtotal) {

	public static OrderItemResponse from(OrderItem item) {
		return new OrderItemResponse(
				item.getProduct().getId(),
				item.getProduct().getName(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
	}
}
