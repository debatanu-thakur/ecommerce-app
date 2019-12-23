package com.ecommerceApp.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerceApp.model.persistence.Cart;
import com.ecommerceApp.model.persistence.User;
import com.ecommerceApp.model.persistence.repositories.CartRepository;
import com.ecommerceApp.model.persistence.repositories.UserRepository;
import com.ecommerceApp.model.requests.CreateUserRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = null;
		try {
			user = userRepository.findByUsername(username);
			if (user == null) {
				log.warn("No user found");
			}
		} catch (Exception ex) {
			log.error("Exception while fetching user with " + username);
			log.warn(ex.getMessage());
			log.trace("getUserByUserNameFailure", 1);
		}
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {

		User user = new User();
		try {
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			log.info("User created successfully");
			log.trace("CreateUserSuccess", 1);
		} catch (Exception ex) {
			log.error("Error while creating user");
			log.warn(ex.getMessage());
			log.trace("CreateUserFailure", 1);

		}
		return ResponseEntity.ok(user);
	}
	
}
