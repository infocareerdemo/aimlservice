package com.ai.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {

	private Long  userId;
	
	private String  emailId;
	
	private String oldPassword;
	
	private String newPassword;
	
	private String confirmPassword;
}
