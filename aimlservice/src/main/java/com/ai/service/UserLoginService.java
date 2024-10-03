package com.ai.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.ai.config.JwtUtil;
import com.ai.dto.LoginDto;
import com.ai.dto.UpdatePasswordDto;
import com.ai.dto.UserDto;
import com.ai.entity.Role;
import com.ai.entity.UserLogin;
import com.ai.exception.ApplicationException;
import com.ai.message.EmailNotificationService;
import com.ai.message.SmsNotificationService;
import com.ai.repository.RoleRepository;
import com.ai.repository.UserLoginRepository;
import com.ai.util.CryptoUtils;
import com.ai.util.FunUtils;

@Service
public class UserLoginService {
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	RoleRepository roleRepository;


	@Autowired
	CryptoUtils cryptoUtils;
	

	@Autowired
	EmailNotificationService emailNotificationService;
	@Autowired
	SmsNotificationService smsNotificationService;
	
	
	public UserDto saveUser(UserDto userDto) throws Exception {
		
		
		System.out.print("\n <<>>>\t"+userDto.toString());
		
		UserLogin userLogin = userLoginRepository.findByEmailId(userDto.getEmailId());
		
		if(userLogin!=null) 
		{

		    if (userDto.getPassWord() != null)
		    {
		    	System.out.print("\n <<>>>\t"+userDto.getEmailId());
		    	System.out.print("\n <<>>>\t"+userDto.getPassWord());
	
				String key = cryptoUtils.generateSecretKey(userDto.getEmailId());
				
				String encryptedPassword = cryptoUtils.encrypt(key, userDto.getPassWord());
					
				userLogin.setKey(key);
		    	System.out.print("\n <<>>>\t"+encryptedPassword);

		        userLogin.setPassWord(encryptedPassword);
		    }
	
		    userLogin.setUserName(userDto.getUserName());
		    
		    userLogin.setEmailId(userDto.getEmailId());
		    userLogin.setEmailVerified(userDto.getEmailVerified());
		    
		    userLogin.setPhone(userDto.getPhoneNo());
		    userLogin.setPhoneVerified(userDto.getPhoneVerified());
	
		    userLogin.setAddress(userDto.getAddress());
		    
			Optional<Role> role = roleRepository.findById((long)1);
		    userLogin.setRole(role.get());
		  	
		    userLogin.setUpdatedDateTime(LocalDateTime.now());
		    
		    UserLogin userlogin1 = userLoginRepository.save(userLogin);
		    
		    System.out.print("\n<<<<->>>>>>"+userlogin1.toString());
		}
		else
		{
			System.out.print("\n<<<<NOT FOUND ->>>>>>"+userDto.toString());
		}
		
	    return userDto;
	}


	
	  public UserLogin authenticateUserAndGenerateToken(LoginDto loginDto) throws Exception
	  {
		  
          System.out.print("\n<<<---------->>> \t "+loginDto.getUsername());
          UserLogin user = userLoginRepository.findByEmailId(loginDto.getEmailId());
	        
	        if (user != null) {
	        	
	        	 System.out.print(user.toString());
	        	
	        	String pwd = cryptoUtils.decrypt(user.getKey(), user.getPassWord());
	            
	            System.out.print("\n<<<---------->>> \t "+pwd);
	            System.out.print("\n<<<---------->>> \t "+loginDto.getPassword());

	            
	            if (pwd != null && pwd.equals(loginDto.getPassword())) {
	            	
		            System.out.print("\n inside the if ....");

	                return user;
	            } else 
	            {
	                throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid credentials");
	            }
	            
	            
	        } 
	        else
	        {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid credentials");
	        }
	        
	        
	    }


	  // send OTP to email ID
	  public String generateEmailOtp(String emailId) throws ApplicationException {
		  
		  FunUtils funUtils = new FunUtils();
			int otp = funUtils.generateOtp();
			
				 UserLogin userLogin = userLoginRepository.findByEmailId(emailId);
				 
				  if (userLogin != null) 
				  {
								userLogin.setEmailId(emailId);
								userLogin.setEmailOtp(otp);
								userLogin.setUpdatedDateTime(LocalDateTime.now());
				
								try {
									emailNotificationService.sendOtpToMail(emailId, (long) otp);
								} catch (Exception e) {
									throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
											"Error occured while sending mail");
								}
				
								userLoginRepository.save(userLogin);
								return "OTP sent";
				
				  }
				  else
				  {
					  	UserLogin NewUserLogin = new UserLogin();
					  	NewUserLogin.setEmailId(emailId);
					  	NewUserLogin.setEmailOtp(otp);
					  	NewUserLogin.setUpdatedDateTime(LocalDateTime.now());
		
						try {
							emailNotificationService.sendOtpToMail(emailId, (long) otp);
						} catch (Exception e) {
							throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
									"Error occured while sending mail");
						}
		
						userLoginRepository.save(NewUserLogin);
						return "OTP sent";
					  
					  
				  }
				
		
		}
	  
	  
		public UserLogin verifyEmail(String emailId, int otp) throws ApplicationException {
			
			UserLogin userLogin = userLoginRepository.findByEmailId(emailId);
			if (userLogin != null) {
				if (userLogin.getEmailOtp() == otp) {
					userLogin.setUpdatedDateTime(LocalDateTime.now());
					//userLogin.setEmailVerified(true);;
					userLoginRepository.save(userLogin);
					return userLogin;
				} else {
					throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Email otp");
				}
			}
			return null;
		}
		
		
		
		
		
		public String generatePhoneOtp(Long phone, Long id) throws ApplicationException {
			
		  		FunUtils funUtils = new FunUtils();
				int otp = funUtils.generateOtp();
				
				Optional<UserLogin> userLogin = userLoginRepository.findById(id);
				
				System.out.print(userLogin.get());
				
				if(userLogin!=null)
				{
					userLogin.get().setPhone(phone);
					userLogin.get().setPhoneOtp(otp);
					userLogin.get().setUpdatedDateTime(LocalDateTime.now());
					smsNotificationService.sendOtpToMobile(phone, (long) otp);
					userLoginRepository.save(userLogin.get());
					return "OTP sent";
										
				}
				
				return "Try Again"; 
		}
		
		

		public boolean verifyPhone(Long phone, int otp) throws ApplicationException {
			UserLogin userLogin = userLoginRepository.findByPhone(phone);
			if (userLogin != null) {
				if (userLogin.getPhoneOtp() == otp) {
					userLogin.setUpdatedDateTime(LocalDateTime.now());
					return true;
				} else {
					throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Phone otp");
				}
			}
			return false;
		}



		
		public String sendOtpForForgotPassword(String emailId) throws ApplicationException {
			
			UserLogin userLogin = userLoginRepository.findByEmailId(emailId);
			
			if (userLogin != null) 
			{
				
				FunUtils funUtils = new FunUtils();
				int otp = funUtils.generateOtp();
				
				try {
					emailNotificationService.sendOtpToMail(emailId, (long) otp);
				} catch (Exception e) {
					throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
							"Error occured while sending mail");
				}
				
				userLogin.setEmailOtp(otp);
				userLoginRepository.save(userLogin);
				
				return "OTP sent";
				
				
			} else {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No user found");
			}
		}
		
		
		
		
		
		
		public String forgotPasswordForUser(UpdatePasswordDto updatePasswordDto) throws Exception {
			
			UserLogin userLogin = userLoginRepository.findByEmailId(updatePasswordDto.getEmailId());
			
			if (userLogin!=null) {
				
				if (updatePasswordDto.getNewPassword()!="") {
					
					String pwd = cryptoUtils.encrypt(userLogin.getKey(), updatePasswordDto.getNewPassword());

					// String pwd = cryptoUtils.encrypt(updatePasswordDto.getNewPassword());
					userLogin.setPassWord(pwd);
					userLogin.setUpdatedDateTime(LocalDateTime.now());
					
					userLoginRepository.save(userLogin);
					
					return "Password updated";
				} else {
					throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), "New Password updated");
				}
			} else {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No user found");
			}
		}
		
	
		
		public String changePasswordForUser(UpdatePasswordDto updatePasswordDto) throws Exception {
			Optional<UserLogin> userLogin = userLoginRepository.findById(updatePasswordDto.getUserId());
			if (userLogin.isPresent()) {
				
				String pwd = cryptoUtils.decrypt(userLogin.get().getKey(), userLogin.get().getPassWord());

				
				if (updatePasswordDto.getOldPassword().equals(pwd)) {
					if (updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())) {
						
						
						String newPwd = cryptoUtils.encrypt(userLogin.get().getKey(), updatePasswordDto.getNewPassword());

						//String newPwd = cryptoUtils.encrypt(updatePasswordDto.getConfirmPassword());

						
						userLogin.get().setPassWord(newPwd);
						userLogin.get().setUpdatedDateTime(LocalDateTime.now());
						
						userLoginRepository.save(userLogin.get());
						
						return "PIN changed";
					} else {
						throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), "New pin and confirm pin mismatch");
					}
				} else {
					throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), "Incorrect old pin");
				}
			} else {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No user found");
			}
		}
		
		

	
	    public UserDto getUserDetails(Long userId) 
	    {
	        
	    	Optional<UserLogin> userDetails = userLoginRepository.findById(userId);
	        
	    	UserLogin user = userDetails.get();
	    	
	        UserDto usersData = new UserDto();
	        
	        usersData.setUserId(user.getUserId());
	        usersData.setUserName(user.getUserName());
	        usersData.setEmailId(user.getEmailId());
	        usersData.setPhoneNo(user.getPhone());
	        usersData.setAddress(user.getAddress());
	        
	        
	        return usersData;
	    }

	  
}
