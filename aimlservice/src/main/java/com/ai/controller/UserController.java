package com.ai.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.config.JwtUtil;
import com.ai.dto.LoginDto;
import com.ai.dto.UpdatePasswordDto;
import com.ai.dto.UserDto;
import com.ai.entity.UserLogin;
import com.ai.exception.ApplicationException;
import com.ai.service.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	UserLoginService userService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@PostMapping("/reg")
	public UserDto saveUser(@RequestBody UserDto userDto)throws Exception{
		return userService.saveUser(userDto);
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception {
	  
		UserLogin user = userService.authenticateUserAndGenerateToken(loginDto);	
		
		String token =  jwtUtil.createToken(user.getEmailId());
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
      
	    String headerToken = response.getHeader("Authorization");
	    System.out.println("Token in Response Header: " + headerToken);	   
	    
	    loginDto.setUserId(user.getUserId());
	    loginDto.setEmailId(user.getEmailId());
	    loginDto.setRole(user.getRole());
	    loginDto.setPassword("");
	    
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
	}

	
	
	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
	    // Retrieve the token from the Authorization header
	    String token = request.getHeader("Authorization");

	    if (token != null && token.startsWith("Bearer ")) {
	        // Extract the actual token (remove the "Bearer " prefix)
	        String jwtToken = token.substring(7);
	        response.setHeader("Authorization", null);

	        // Send a success response
	        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("Authorization token is missing or invalid", HttpStatus.BAD_REQUEST);
	    }
	}



	@GetMapping("/otpToMail")
	public ResponseEntity<Object> generateEmailOtp(@RequestParam String emailId) throws ApplicationException {
		
		
		System.out.print("\n <<< inside the otpToMail >>> ");
		return new ResponseEntity<Object>(userService.generateEmailOtp(emailId), HttpStatus.OK);
	}
	
	
	@GetMapping("/verifyMail")
	public ResponseEntity<Object> verifyEmail(@RequestParam String emailId, @RequestParam int otp) throws ApplicationException {
		System.out.print("\n inside the verifyMail");

		return new ResponseEntity<Object>(userService.verifyEmail(emailId, otp), HttpStatus.OK);
	}
	
	
	@GetMapping("/otpToPhone")
	public ResponseEntity<Object> generatePhoneOtp(@RequestParam Long phone,@RequestParam(required = false) Long id) throws ApplicationException {
		return new ResponseEntity<Object>(userService.generatePhoneOtp(phone, id), HttpStatus.OK);
	}
	
	@GetMapping("/verifyPhone")
	public ResponseEntity<Object> verifyPhone(@RequestParam Long phone, @RequestParam int otp) throws ApplicationException {
		return new ResponseEntity<Object>(userService.verifyPhone(phone, otp), HttpStatus.OK);
	}
	
	@PostMapping("/otpForgotPwd")
	public ResponseEntity<Object> sendOtpForForgotPassword(@RequestBody Map<String, String> userData) throws ApplicationException {
		
		  String emailId = userData.get("emailId");
		return new ResponseEntity<Object>(userService.sendOtpForForgotPassword(emailId), HttpStatus.OK);
	}
	
	
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<Object> forgotPasswordForUser(@RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
		return new ResponseEntity<Object>(userService.forgotPasswordForUser(updatePasswordDto), HttpStatus.OK);
	}
	
	
	@PostMapping("/changePassword")
	public ResponseEntity<Object> changePasswordForUser(@RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
		return new ResponseEntity<Object>(userService.changePasswordForUser(updatePasswordDto), HttpStatus.OK);
	}
	
	@PostMapping("/profile")
	public ResponseEntity<Object> userProfile(@RequestBody UserDto userDto) throws Exception {
		return new ResponseEntity<Object>(userService.getUserDetails(userDto.getUserId()), HttpStatus.OK);
	}
	
	
}