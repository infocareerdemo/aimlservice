package com.ai.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ai.config.JwtUtil;
import com.ai.dto.LoginDto;
import com.ai.entity.Role;
import com.ai.entity.UserLogin;
import com.ai.service.UserLoginService;

import jakarta.servlet.http.HttpServletResponse;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;  

    
    @Mock
    private UserLoginService userLoginService;
    
  //  @InjectMocks
   // private UserLoginRepository userLoginRepository;

    @Mock
    private JwtUtil jwtUtil; 
    
    @Mock
    private HttpServletResponse response;  

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  
    }

    @Test
    public void testLogin_Success_WithTokenInHeader() throws Exception {
    	
    	
    	
    	  Role r = new Role();
          r.setRoleId(1L);
          r.setRoleName("ADMIN");
          
          
        // Arrange: Create a sample LoginDto request
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("");
        loginDto.setEmailId("arulkumar.v@infocareerindia.com");
        loginDto.setPhone(null);
        loginDto.setPassword("test");
        loginDto.setRole(r);
     
      
     
        // Create a sample UserLogin object as expected from the service
        UserLogin userLogin = new UserLogin();
        userLogin.setUserId(28L);
        userLogin.setEmailId("arulkumar.v@infocareerindia.com");
        userLogin.setEmailOtp(1234);
        userLogin.setPhone(7010660814L);
        userLogin.setPhoneOtp(1234);
        userLogin.setPassWord("testEncrypted");  // Example encrypted password
        userLogin.setRole(r);
     
        
//     
//        LoginDto loginDto1 = new LoginDto();
//        loginDto1.setUserId(28L);;
//        loginDto1.setUsername(null);
//        loginDto1.setEmailId("arulkumar.v@infocareerindia.com");
//        loginDto1.setPassword("");
//        loginDto1.setPhone(null);
//        loginDto1.setRole(r);

        
        // Mock JWT token creation
  //    String token = "mock-jwt-token";  // Mock token value
  //     when(JwtUtil.createToken(any(UserLogin.class)).thenReturn(token);
//     
        // Mock the service to return the expected UserLogin object
        when(userLoginService.authenticateUserAndGenerateToken(any(LoginDto.class))).thenReturn(userLogin);
        

        //when(userLoginRepository.findByEmailId(loginDto.getEmailId())).thenReturn(userLogin);
        
        
      //  when(userLoginService.authenticateUserAndGenerateToken(eq(loginDto))).thenReturn(userLogin);
        
     
        // Act: Call the login method in the controller
        ResponseEntity<Object> result = userController.login(loginDto, response);
     
        // Assert: Verify the response
        assertNotNull(result);
        System.out.println("\n" + result.getBody());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(loginDto, result.getBody());  // Verify the returned user
        
     
        // Verify that the service's login method was called
        verify(userLoginService, times(2)).authenticateUserAndGenerateToken(any(LoginDto.class));
     
        // Verify the Authorization header is set correctly
   //  verify(response).addHeader(HttpHeaders.AUTHORIZATION, "Bearer "  );
       
        System.out.println("UserController: User Login is Passed");
    }


}
