package com.ecommerceApp.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerceApp.model.persistence.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
