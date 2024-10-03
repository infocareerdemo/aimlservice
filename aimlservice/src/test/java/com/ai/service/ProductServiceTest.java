package com.ai.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.ai.entity.Location;
import com.ai.entity.Products;
import com.ai.exception.ApplicationException;
import com.ai.repository.LocationRepository;
import com.ai.repository.ProductsRepository;



public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private MultipartFile productImg;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveFoodItem_SuccessfulSave() throws IOException, ApplicationException {
        // Arrange
        Products productsRequest = new Products();
        productsRequest.setProductName("Pizza");
        productsRequest.setProductPrice(10.50);
        
        Location location = new Location();
        location.setLocationId(1L);
        productsRequest.setLocation(location);

        // Mock the location repository to return the location
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Mock the products repository to return null for no existing product
        when(productsRepository.findByProductNameAndLocationLocationId("Pizza", 1L))
            .thenReturn(null);

        // Mocking product image file
        when(productImg.isEmpty()).thenReturn(false);
        when(productImg.getBytes()).thenReturn("test-image".getBytes());

        // Mock the save method to return the saved product
        Products savedProduct = new Products();
        savedProduct.setProductId(1L); // Assuming the product ID will be generated
        savedProduct.setProductName("Pizza");
        savedProduct.setProductPrice(10.50);
        savedProduct.setLocation(location);
       // when(productsRepository.save(any(Products.class))).thenReturn(savedProduct);

        // Act
        String result = productService.saveOrUpdateFoodItem(productsRequest, productImg);

        // Assert
        assertEquals("Food item saved", result);
        verify(productsRepository, times(1)).save(any(Products.class));

        // Print success message
       System.out.println("Test Case for Successful Save Passed");
    }
}