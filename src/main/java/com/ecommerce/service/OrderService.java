package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.OrderItemRequest;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;

@Service
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final CustomerService customerService;
	private final ProductService productService;

	public OrderService(OrderRepository orderRepository, CustomerService customerService,
			ProductService productService) {
		this.orderRepository = orderRepository;
		this.customerService = customerService;
		this.productService = productService;
	}

	public OrderResponse placeOrder(OrderRequest request) {
		Customer customer = customerService.findById(request.customerId());

		Order order = new Order();
		order.setCustomer(customer);

		BigDecimal total = BigDecimal.ZERO;
		for (OrderItemRequest line : request.items()) {
			Product product = productService.findById(line.productId());
			if (product.getStock() < line.quantity()) {
				throw new BadRequestException("Not enough stock for '" + product.getName()
						+ "'. Available: " + product.getStock() + ", requested: " + line.quantity());
			}
			product.setStock(product.getStock() - line.quantity());

			OrderItem item = new OrderItem();
			item.setProduct(product);
			item.setQuantity(line.quantity());
			item.setUnitPrice(product.getPrice());
			order.addItem(item);

			total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
		}
		order.setTotal(total);

		return OrderResponse.from(orderRepository.save(order));
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrder(Long id) {
		return OrderResponse.from(find(id));
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersForCustomer(Long customerId) {
		customerService.findById(customerId);
		return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
				.stream().map(OrderResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders() {
		return orderRepository.findAll().stream().map(OrderResponse::from).toList();
	}

	public OrderResponse cancelOrder(Long id) {
		Order order = find(id);
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new BadRequestException("Order " + id + " is already cancelled");
		}
		for (OrderItem item : order.getItems()) {
			Product product = item.getProduct();
			product.setStock(product.getStock() + item.getQuantity());
		}
		order.setStatus(OrderStatus.CANCELLED);
		return OrderResponse.from(order);
	}

	private Order find(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order", id));
	}
}
