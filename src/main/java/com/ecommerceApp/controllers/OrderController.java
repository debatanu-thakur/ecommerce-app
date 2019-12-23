package com.ecommerceApp.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerceApp.model.persistence.Cart;
import com.ecommerceApp.model.persistence.User;
import com.ecommerceApp.model.persistence.UserOrder;
import com.ecommerceApp.model.persistence.repositories.CartRepository;
import com.ecommerceApp.model.persistence.repositories.OrderRepository;
import com.ecommerceApp.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		UserOrder order = null;
		try {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				return ResponseEntity.notFound().build();
			}
			order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);
			log.trace("orderSubmitSuccess", 1);
		} catch (Exception ex) {
			log.error("Error while submitting order");
			log.warn(ex.getMessage());
			log.trace("orderSubmitFailure", 1);
		}
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
