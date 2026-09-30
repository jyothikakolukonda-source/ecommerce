package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional(readOnly = true)
	public List<Product> findAll(String category) {
		if (category == null || category.isBlank()) {
			return productRepository.findAll();
		}
		return productRepository.findByCategoryIgnoreCase(category);
	}

	@Transactional(readOnly = true)
	public Product findById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product", id));
	}

	public Product create(ProductRequest request) {
		Product product = new Product();
		apply(product, request);
		return productRepository.save(product);
	}

	public Product update(Long id, ProductRequest request) {
		Product product = findById(id);
		apply(product, request);
		return productRepository.save(product);
	}

	public void delete(Long id) {
		productRepository.delete(findById(id));
		productRepository.flush();
	}

	private void apply(Product product, ProductRequest request) {
		product.setName(request.name());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setStock(request.stock());
		product.setCategory(request.category());
		product.setImageUrl(request.imageUrl());
	}
}
