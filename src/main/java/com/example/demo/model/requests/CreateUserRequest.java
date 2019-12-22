package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserRequest {

	@JsonProperty
	private String username;

	@JsonProperty
	@NotBlank(message = "Password cannot be empty or simply spaces")
	@Size(min = 8, max = 20, message = "Password must be between 8 to 20 characters")
	private String password;


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
