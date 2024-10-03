package com.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.service.LocationService;


@RestController
@RequestMapping("/api/v1/location")
public class LocationController {
	
	@Autowired
	LocationService locationService;
	
	@GetMapping("/allLocation")
	public ResponseEntity<Object> getAllLocation() {
		return new ResponseEntity<Object>(locationService.getAllLocation(), HttpStatus.OK);
	}

	
	  @GetMapping("/id")
	    public ResponseEntity<Object> getLocationById(Long locationId) {
	    	return new ResponseEntity<Object>(locationService.getLocationById(locationId), HttpStatus.OK);
	    }
	    
	  
}
