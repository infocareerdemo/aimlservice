package com.ai.controller;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ai.dto.OrderRequest;
import com.ai.dto.OrderSummary;
import com.ai.entity.OrderDetails;
import com.ai.entity.Orders;
import com.ai.exception.ApplicationException;
import com.ai.repository.LocationRepository;
import com.ai.repository.ProductsRepository;
import com.ai.repository.UserLoginRepository;
import com.ai.service.OrderService;




public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;  
    @Mock
    private OrderService orderService;  

    @Mock
    private UserLoginRepository userLoginRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProductsRepository productsRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  
    }

    @Test
    public void testSaveOrderWithOrderDetails_Success() throws ApplicationException {
        // Arrange Order request 
        List<OrderRequest> orderRequests = new ArrayList<>();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(2);
        orderRequests.add(orderRequest);

        Long userId = 1L;
        Long locationId = 1L;

        
        // Create a mock OrderSummary to be returned by the service
        OrderSummary mockOrderSummary = new OrderSummary();
        Orders mockOrders = new Orders();
        mockOrders.setPaymentStatus("Pay Pending");
        mockOrders.setOrderAmount(200.0);
        mockOrders.setTotalAmount(210.0);
        mockOrderSummary.setOrders(mockOrders);
        mockOrderSummary.setOrderDetails(Collections.singletonList(new OrderDetails()));

        // Mock the service method call
        when(orderService.saveOrderWithOrderDetails(anyList(), anyLong(), anyLong())).thenReturn(mockOrderSummary);

        // Act
        ResponseEntity<Object> responseEntity = orderController.saveOrderAndOrderDetails(orderRequests, userId, locationId);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        // Cast the response body
        assertTrue(responseEntity.getBody() instanceof OrderSummary);

        OrderSummary orderSummary = (OrderSummary) responseEntity.getBody();
        assertNotNull(orderSummary);
        assertNotNull(orderSummary.getOrders());
        assertNotNull(orderSummary.getOrderDetails());
        assertEquals(1, orderSummary.getOrderDetails().size());

        Orders savedOrder = orderSummary.getOrders();
        assertEquals("Pay Pending", savedOrder.getPaymentStatus());
        assertEquals(200.0, savedOrder.getOrderAmount());
        assertEquals(210.0, savedOrder.getTotalAmount());

        // Verify service interaction
        verify(orderService, times(1)).saveOrderWithOrderDetails(orderRequests, userId, locationId);

        System.out.println("OrderController : Order Saved is Passed");
    }
}

