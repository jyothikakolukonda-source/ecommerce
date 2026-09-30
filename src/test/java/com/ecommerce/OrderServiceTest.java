package com.ecommerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.CustomerRequest;
import com.ecommerce.dto.OrderItemRequest;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.ProductRequest;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;

@SpringBootTest
@Transactional
class OrderServiceTest {

	@Autowired
	OrderService orderService;
	@Autowired
	ProductService productService;
	@Autowired
	CustomerService customerService;

	@Test
	void placingOrderReducesStockAndComputesTotal() {
		Product p = productService.create(new ProductRequest("Test Item", "d", new BigDecimal("10.50"), 5, "Test", null));
		Customer c = customerService.create(new CustomerRequest("Tester", "tester@example.com"));

		OrderResponse order = orderService.placeOrder(
				new OrderRequest(c.getId(), List.of(new OrderItemRequest(p.getId(), 2))));

		assertEquals(new BigDecimal("21.00"), order.total());
		assertEquals(3, productService.findById(p.getId()).getStock());
	}

	@Test
	void orderFailsWhenStockIsInsufficient() {
		Product p = productService.create(new ProductRequest("Scarce", "d", new BigDecimal("5.00"), 1, "Test", null));
		Customer c = customerService.create(new CustomerRequest("Tester2", "tester2@example.com"));

		assertThrows(BadRequestException.class, () -> orderService.placeOrder(
				new OrderRequest(c.getId(), List.of(new OrderItemRequest(p.getId(), 2)))));
	}

	@Test
	void cancellingOrderRestoresStock() {
		Product p = productService.create(new ProductRequest("Cancelable", "d", new BigDecimal("4.00"), 10, "Test", null));
		Customer c = customerService.create(new CustomerRequest("Tester3", "tester3@example.com"));
		OrderResponse order = orderService.placeOrder(
				new OrderRequest(c.getId(), List.of(new OrderItemRequest(p.getId(), 4))));

		OrderResponse cancelled = orderService.cancelOrder(order.id());

		assertEquals(OrderStatus.CANCELLED, cancelled.status());
		assertEquals(10, productService.findById(p.getId()).getStock());
	}
}
