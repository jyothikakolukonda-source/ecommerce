package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;

public record OrderResponse(
		Long id,
		Long customerId,
		String customerName,
		List<OrderItemResponse> items,
		BigDecimal total,
		OrderStatus status,
		LocalDateTime createdAt) {

	public static OrderResponse from(Order order) {
		return new OrderResponse(
				order.getId(),
				order.getCustomer().getId(),
				order.getCustomer().getName(),
				order.getItems().stream().map(OrderItemResponse::from).toList(),
				order.getTotal(),
				order.getStatus(),
				order.getCreatedAt());
	}
}
