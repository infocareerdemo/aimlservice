package com.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.entity.Location;
import com.ai.repository.LocationRepository;



@Service
public class LocationService {
	
	@Autowired
	private LocationRepository locationRepository;
	
	public List<Location> getAllLocation() {
		return locationRepository.findAll();
	}
	
	
	public Location getLocationById(Long locationId) {
		Optional<Location> location = locationRepository.findById(locationId);
		if (location.isPresent()) {
			return location.get();
		} else {
			return null;
		}
	}

}
