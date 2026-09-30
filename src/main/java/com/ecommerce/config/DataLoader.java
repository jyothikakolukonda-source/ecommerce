package com.ecommerce.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;

/** Inserts a few sample rows on first start so the API has something to show. */
@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner seedData(ProductRepository products, CustomerRepository customers) {
		return args -> {
			if (products.count() == 0) {
				products.saveAll(List.of(
						new Product("Wireless Mouse", "2.4 GHz ergonomic mouse", new BigDecimal("799.00"), 50, "Electronics", "/images/mouse.svg"),
						new Product("Mechanical Keyboard", "Blue switch, backlit", new BigDecimal("2499.00"), 25, "Electronics", "/images/keyboard.svg"),
						new Product("Running Shoes", "Lightweight, size 9", new BigDecimal("3299.00"), 40, "Fashion", "/images/shoes.svg"),
						new Product("Water Bottle", "1 litre steel bottle", new BigDecimal("399.00"), 100, "Home", "/images/bottle.svg")));
			}
			if (customers.count() == 0) {
				customers.save(new Customer("Demo User", "demo@example.com"));
			}
		};
	}
}
