package com.ai.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ai.entity.Location;
import com.ai.entity.Products;
import com.ai.exception.ApplicationException;
import com.ai.repository.LocationRepository;
import com.ai.repository.ProductsRepository;
import com.ai.service.ProductService;



public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
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
        System.out.print("\n Testing the setUp \t ");
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

        // Mock the service to return a success message (String)
        when(productService.saveOrUpdateFoodItem(any(Products.class), any(MultipartFile.class)))
           .thenReturn("Food item saved");
        
        // Act
        ResponseEntity<Object> result = productController.saveOrUpdateFoodItem(productsRequest, productImg);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Food item saved", result.getBody());
        
        // Verify interactions
        verify(productService, times(1)).saveOrUpdateFoodItem(any(Products.class), any(MultipartFile.class));
        
        System.out.println("ProductController : Product save is Passed");
    }
    

    
    @Test
    public void testGetAllFoodItems_Success() throws Exception {
        // Arrange
        // Create mock list of products
    	
    	Location location = new Location();
    	
        Products product1 = new Products();
        product1.setProductName("Pizza");
        product1.setProductPrice(10.50);
        product1.setProductDescription("Pizza with chicken");
        product1.setLocation(location);
        product1.setProductActive(true);
        product1.setProductGST(0);
        product1.setProductUpdatedBy(1L);
        product1.setUpdatedDate(LocalDateTime.now());
       
        
        Products product2 = new Products();
        product2.setProductName("Burger");
        product2.setProductPrice(5.50);
        product2.setProductDescription("Burger with chicken");
        product2.setLocation(location);
        product2.setProductActive(true);
        product2.setProductGST(0);
        product2.setProductUpdatedBy(1L);
        product2.setUpdatedDate(LocalDateTime.now());

        List<Products> mockProductsList = Arrays.asList(product1, product2);

        // Mock the service to return the mock list of products
        when(productService.getAllFoodItems()).thenReturn(mockProductsList);

        
        // Act: Call the controller method
        ResponseEntity<Object> responseEntity = productController.getAllFoodItems();
    
        
        // Assert: Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());  
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody() instanceof List<?>);  
        List<Products> productsList = (List<Products>) responseEntity.getBody();
        assertEquals(2, productsList.size());
        assertEquals("Pizza", productsList.get(0).getProductName());
        assertEquals("Burger", productsList.get(1).getProductName());

        // Verify that the service method was called once
        verify(productService, times(1)).getAllFoodItems();

        System.out.println("ProductController : Get All Products Passed");
    }
    
    
    
}
