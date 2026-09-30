package com.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse place(@Valid @RequestBody OrderRequest request) {
		return orderService.placeOrder(request);
	}

	@GetMapping
	public List<OrderResponse> list(@RequestParam(required = false) Long customerId) {
		if (customerId != null) {
			return orderService.getOrdersForCustomer(customerId);
		}
		return orderService.getAllOrders();
	}

	@GetMapping("/{id}")
	public OrderResponse get(@PathVariable Long id) {
		return orderService.getOrder(id);
	}

	@PostMapping("/{id}/cancel")
	public OrderResponse cancel(@PathVariable Long id) {
		return orderService.cancelOrder(id);
	}
}
