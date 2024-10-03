package com.ai.dto;

import com.ai.entity.Role;

import lombok.Data;

@Data
public class LoginDto {

	private Long userId;
	
	private String username;
	
	private String emailId;
	
	private Long phone;
	
	private String password;
	
	private Role role;
}
