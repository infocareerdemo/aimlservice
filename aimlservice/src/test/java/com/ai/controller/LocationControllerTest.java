package com.ai.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ai.config.JwtUtil;
import com.ai.entity.Location;
import com.ai.service.LocationService;

@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc(addFilters = false)  
public class LocationControllerTest {

	
	   @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private LocationService locationService;

	    @MockBean
	    private JwtUtil jwtUtil; 

	    @Test
	    void testGetAllLocation() throws Exception {
	        when(locationService.getAllLocation()).thenReturn(Collections.emptyList());

	        mockMvc.perform(get("http://localhost:7001/api/v1/location/allLocation"))
	               .andExpect(status().isOk()) 
	               .andExpect(jsonPath("$.length()").value(0));  
	        System.out.println("Get All Locattion Test Case is Pass");
	    }
	    
	    
	    @Test
	    void testGetLocationByIdSuccess() throws Exception {
	        // Arrange
	        Long locationId = 1L;
	        String locationName = "DLF";
	        Location mockLocation = new Location();
	        mockLocation.setLocationId(locationId);
	        mockLocation.setLocationName(locationName);

	        when(locationService.getLocationById(locationId)).thenReturn(mockLocation);

	        try {
	            // Act & Assert
	            mockMvc.perform(get("http://localhost:7001/api/v1/location/id")
	                    .param("locationId", String.valueOf(locationId)))
	                   .andExpect(status().isOk())
	                   .andExpect(jsonPath("$.locationId").value(locationId))
	                   .andExpect(jsonPath("$.locationName").value(locationName));
	            
	            System.out.println("Get By Location Test Case is Pass");
	        } catch (AssertionError e) {
	            System.out.println("Get By Location Test Case Failed: " + e.getMessage());
	            throw e; 
	        }
	    }

}
