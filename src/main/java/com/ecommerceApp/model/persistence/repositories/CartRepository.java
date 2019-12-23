package com.ecommerceApp.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerceApp.model.persistence.Cart;
import com.ecommerceApp.model.persistence.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
