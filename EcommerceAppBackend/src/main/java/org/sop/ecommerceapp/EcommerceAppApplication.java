package org.sop.ecommerceapp;

import org.sop.ecommerceapp.models.*;
import org.sop.ecommerceapp.services.CartService;
import org.sop.ecommerceapp.services.ProductsToDeleteService;
import org.sop.ecommerceapp.services.StatisticService;
import org.sop.ecommerceapp.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
public class EcommerceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserService userService, CartService cartService,
											   StatisticService statisticService, ProductsToDeleteService productsToDeleteService) {
		return args -> {

			if (statisticService.findById(1L) == null) {
				Statistic statistic = new Statistic();
				statistic.setDeliveryTime(2);
				statistic.setRefundPeriod(5);
				statisticService.save(statistic);
			}
			if (productsToDeleteService.findById(1L) == null) {
				ProductsToDelete productsToDelete = new ProductsToDelete();
				productsToDelete.setProducts(new ArrayList<>());
				productsToDeleteService.save(productsToDelete);
			}
			if (userService.findByUsername("admin") == null) {
				User user = new User();
				user.setUsername("admin");
				user.setEmail("admin");
				user.setPassword("123");
				user.setFirstName("admin");
				user.setLastName("admin");
				user.setRole("ADMIN");
				user.setCreationDate(new Date());
				user.setBirthDate(new Date());
				user.setCart(new Cart());
				user = userService.register(user);
				user.getCart().setUser(user);
				cartService.save(user.getCart());
			}
		};
	}
}

