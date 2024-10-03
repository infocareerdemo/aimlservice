package com.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long>{

}
