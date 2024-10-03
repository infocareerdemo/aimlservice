package com.ai.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ai.cmdlinerunner.FoodInitializer;
import com.ai.config.GeneralConstant;
import com.ai.dto.OrderRequest;
import com.ai.dto.OrderSummary;
import com.ai.entity.Location;
import com.ai.entity.OrderDetails;
import com.ai.entity.Orders;
import com.ai.entity.Products;
import com.ai.entity.UserLogin;
import com.ai.exception.ApplicationException;
import com.ai.message.SmsNotificationService;
import com.ai.repository.LocationRepository;
import com.ai.repository.OrderDetailsRepository;
import com.ai.repository.OrderRepository;
import com.ai.repository.ProductsRepository;
import com.ai.repository.UserLoginRepository;


@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	ProductsRepository productsRepository;

	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	SmsNotificationService smsNotificationService;
	
	//@Autowired
	//OrderTimeRepository orderTimeRepository;
	
	public OrderSummary saveOrderWithOrderDetails(List<OrderRequest> orderRequests, Long userId, Long locationId)
			throws ApplicationException {
				    
		OrderSummary orderSummary = new OrderSummary();	
		
//		 // Check the order creation window
//	    OrderTime orderTime = orderTimeRepository.findById(1L)
//	            .orElseThrow(() -> new ApplicationException(
//	                    HttpStatus.INTERNAL_SERVER_ERROR,
//	                    1002,
//	                    LocalDateTime.now(),
//	                    "Order creation window not set."
//	            ));
//
//	    LocalTime currentTime = LocalTime.now();

	    // Set OrderTime in OrderSummary
//	    orderSummary.setOrderTime(orderTime);

//    if (currentTime.isBefore(orderTime.getStartTime())) {
//	        // The current time is before the order start time
//	        orderSummary.setErrorMessage("The order window has not yet started.");
//	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1003, LocalDateTime.now(), orderSummary.getErrorMessage());
//	    } else if (currentTime.isAfter(orderTime.getEndTime())) {
//	        // The current time is after the order end time
//	        orderSummary.setErrorMessage("The order window has closed.");
//	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1004, LocalDateTime.now(), orderSummary.getErrorMessage());
//	    }
	  
	    
		if (!CollectionUtils.isEmpty(orderRequests)) {
			Orders orders = new Orders();
			
			Optional<UserLogin> userLogin = userLoginRepository.findById(userId);
			
			if (userLogin.isPresent()) {
				orders.setUserLogin(userLogin.get());
				orders.setAddress(userLogin.get().getAddress());
				Optional<Location> location = locationRepository.findById(locationId);
				orders.setLocation(location.get());
				//orders.setGst(5.0);
				orders.setPaymentStatus(GeneralConstant.PAY_PENDING.toString());
				orders.setOrderedDateTime(LocalDateTime.now());
				orderRepository.save(orders);
				
				double totAmt = 0.0;
				List<OrderDetails> orderDetailsRes = new ArrayList<>();
				for (OrderRequest orderRequest : orderRequests) {
					OrderDetails orderDetails = new OrderDetails();
					Optional<Products> products = productsRepository.findById(orderRequest.getProductId());
					if (products.isPresent()) {
						orderDetails.setProducts(products.get());
						orderDetails.setOrders(orders);
						orderDetails.setQuantity(orderRequest.getQuantity());
						orderDetails.setUnitPrice(products.get().getProductPrice());

						double total = orderRequest.getQuantity() * products.get().getProductPrice();

						orderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", total)));
						orderDetails.setOrderDateTime(LocalDateTime.now());

						orderDetailsRepository.save(orderDetails);
						
						totAmt += total;
						orderDetailsRes.add(orderDetails);
					}
				}

				orders.setOrderAmount(Double.parseDouble(String.format("%.2f", totAmt)));
                Double gstPercentage = FoodInitializer.GST_VALUE;
	            double gst = (gstPercentage / 100.0) * totAmt;
				orders.setGstAmount(Double.parseDouble(String.format("%.2f", gst)));
				double totalAmountIncludingGst = totAmt + gst;
				orders.setTotalAmount(Double.parseDouble(String.format("%.2f", totalAmountIncludingGst)));
				
				orderRepository.save(orders);
				
				orderSummary.setOrderDetails(orderDetailsRes);
				orderSummary.setOrders(orders);
				return orderSummary;
			} else {
				throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
			}
		} else {
			throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "No data present");
		}
	}

	public String generateOrderId(LocalDateTime orderDateTime) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Orders> orders = orderRepository.findByOrderedDateTimeBetween(startOfDay, endOfDay);
		String orderId = null;
		if (!CollectionUtils.isEmpty(orders)) {
			orders = orders.stream().filter(o -> (o.getOrderId() != null && !o.getOrderId().isEmpty()))
					.sorted(Comparator.comparing(Orders::getOrderId)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(orders)) {
				orderId = orders.get(orders.size() - 1).getOrderId();
			}
		}
		String oid = null;
		if (orderId != null && !orderId.isEmpty()) {
			String[] id = orderId.split("-");
			int n = Integer.parseInt(id[1]) + 1;
			oid = "ORDER#" + orderDateTime.format(format) + "-" + String.format("%04d", n);
		} else {
			oid = "ORDER#" + orderDateTime.format(format) + "-" + String.format("%04d", 1);
		}
		return oid;
	}

	public Orders updatePaymentStatus(Orders orders, boolean status) {
		if (status == true) {
			// Generate order id
			String oid = generateOrderId(orders.getOrderedDateTime());
			orders.setOrderId(oid);
			orders.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
			orderRepository.save(orders);

			return orders;
		} else {
			// Generate order id
			String oid = generateOrderId(orders.getOrderedDateTime());
			orders.setOrderId(oid);
			orders.setPaymentStatus(GeneralConstant.PAY_FAILED.toString());
			orderRepository.save(orders);

			return orders;
		}
	}

	public List<Orders> getOrdersByUserId(Long id) throws ApplicationException {
		Optional<UserLogin> user = userLoginRepository.findById(id);
		if (user.isPresent()) {
			List<Orders> orders = orderRepository.findByUserLoginUserId(id);
			if (!CollectionUtils.isEmpty(orders)) {
				return orders;
			}
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No user found");
		}
		return null;
	}

	public OrderSummary getOrderWithOrderDetailsById(Long id) throws ApplicationException {
		OrderSummary orderSummary = new OrderSummary();
		Optional<Orders> orders = orderRepository.findById(id);
		if (orders.isPresent()) {
			List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(id);
			if (!CollectionUtils.isEmpty(orderDetails)) {
				orderSummary.setOrders(orders.get());
				orderSummary.setOrderDetails(orderDetails);
			}
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No order found");
		}
		return orderSummary;
	}

//	public BufferedImage generateQrCodeForOrderId(Long id) throws WriterException {
//		QRCodeWriter barcodeWriter = new QRCodeWriter();
//		BitMatrix bitMatrix = barcodeWriter.encode(String.valueOf(id), BarcodeFormat.QR_CODE, 200, 200);
//
//		return MatrixToImageWriter.toBufferedImage(bitMatrix);
//	}

//	public Map<String, Object> getOrderedItemsWithQuantityForToday(Long locationId) {
//		Map<String, Object> itemWithQty = new HashMap<>();
//		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
//		LocalDateTime endOfDay = startOfDay.plusDays(1);
//		List<Orders> orders = orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startOfDay, endOfDay,
//				locationId);
//		if (!CollectionUtils.isEmpty(orders)) {
//			orders = orders.stream().filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
//					.collect(Collectors.toList());
//			if (!CollectionUtils.isEmpty(orders)) {
//				for (Orders order : orders) {
//					List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
//					if (!CollectionUtils.isEmpty(orderDetails)) {
//						for (OrderDetails orderDetail : orderDetails) {
//							if (itemWithQty.get(orderDetail.getProducts().getProductName()) != null) {
//								Long qty = (Long) itemWithQty.get(orderDetail.getProducts().getProductName());
//								qty += orderDetail.getQuantity();
//
//								itemWithQty.put(orderDetail.getProducts().getProductName(), qty);
//							} else {
//								itemWithQty.put(orderDetail.getProducts().getProductName(), orderDetail.getQuantity());
//							}
//						}
//					}
//				}
//			}
//		}
//		return itemWithQty;
//	}

//	public List<Orders> getTodayOrders(Long locationId) {
//		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
//		LocalDateTime endOfDay = startOfDay.plusDays(1);
//		List<Orders> orders = orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startOfDay, endOfDay,
//				locationId);
//		if (!CollectionUtils.isEmpty(orders)) {
//			orders = orders.stream().filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
//					.collect(Collectors.toList());
//			if (!CollectionUtils.isEmpty(orders)) {
//				return orders;
//			}
//		}
//		return null;
//	}
//
//	public OrderDashboard getOrderItemCount(Long locationId) {
//		OrderDashboard orderDashboard = new OrderDashboard();
//		List<Orders> locationOrders = null;
//		if (locationId != null) {
//			locationOrders = getTodayOrders(locationId);
//		} else {
//			locationOrders = getTodayOrders(locationRepository.findAll().get(0).getLocationId());
//		}
//
//		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
//		LocalDateTime endOfDay = startOfDay.plusDays(1);
//		List<Orders> todayOrders = orderRepository.findByOrderedDateTimeBetween(startOfDay, endOfDay);
//		if (!CollectionUtils.isEmpty(todayOrders)) {
//			todayOrders = todayOrders.stream()
//					.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
//					.collect(Collectors.toList());
//		}
//		orderDashboard.setTotalOrdersCount((long) todayOrders.size());
//
//		List<ItemCount> itemCounts = new ArrayList<>();
//		List<Products> products = productsRepository.findAll();
//		if (!CollectionUtils.isEmpty(products)) {
//			if (!CollectionUtils.isEmpty(todayOrders)) {
//				todayOrders = todayOrders.stream()
//						.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
//						.collect(Collectors.toList());
//				if (!CollectionUtils.isEmpty(todayOrders)) {
//					for (Products product : products) {
//						ItemCount itemCount = new ItemCount();
//						long count = 0;
//						for (Orders order : todayOrders) {
//							List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
//							for (OrderDetails orderDetail : orderDetails) {
//								if (orderDetail.getProducts().getProductId() == product.getProductId()) {
//									count++;
//								}
//							}
//						}
//						if (count > 0) {
//							itemCount.setProducts(product);
//							itemCount.setCount(count);
//							itemCounts.add(itemCount);
//						}
//					}
//				}
//			}
//		}
//
//		orderDashboard.setTotalOrderDetails(itemCounts);
//
//		Map<String, Long> itemWithQty = new HashMap<>();
//		if (!CollectionUtils.isEmpty(locationOrders)) {
//			locationOrders = locationOrders.stream()
//					.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
//					.collect(Collectors.toList());
//			if (!CollectionUtils.isEmpty(locationOrders)) {
//				for (Orders order : locationOrders) {
//					List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
//					if (!CollectionUtils.isEmpty(orderDetails)) {
//						for (OrderDetails orderDetail : orderDetails) {
//							if (itemWithQty.get(orderDetail.getProducts().getProductName()) != null) {
//								Long qty = (Long) itemWithQty.get(orderDetail.getProducts().getProductName());
//								qty += 1;
//
//								itemWithQty.put(orderDetail.getProducts().getProductName(), qty);
//							} else {
//								itemWithQty.put(orderDetail.getProducts().getProductName(), (long) 1);
//							}
//						}
//					}
//				}
//			}
//		}
//
//		orderDashboard.setLocationOrderDetails(itemWithQty);
//
//		return orderDashboard;
//	}
//
//	 
//	   public String updateShippedStatus(OrderStatusUpdateRequest request) {
//		  List<String> orderIds  = request.getOrderIds();
//	        for(String orderId : orderIds) {
//	            Orders order = orderRepository.findByOrderId(orderId);
//                 if(order == null) {
// 	                throw new RuntimeException("Order not found with orderId: " + orderId);
//                 }
//                 order.setShipped(request.isShipped());
// 	             orderRepository.save(order);	            
//	        }
//	        return "Order status updated successfully";
//	    }
//	
//	
//	public String updateDeliveredStatus(OrderDeliveredRequest request) {
//		int otp = 1234;
//	    List<String> orderIds = request.getOrderIds();
//	    boolean deliveredStatus = request.isDelivered();
//
//	    for(String orderId : orderIds) {
//	        Orders order = orderRepository.findByOrderId(orderId);
//	        if(order == null) {
//	            throw new RuntimeException("Order not found with orderId: " + orderId);
//	        }
//	        
//	        order.setDelivered(deliveredStatus);
//	        orderRepository.save(order); 
//	        
//	        Long userId = order.getUserLogin().getUserId();
//	        Optional<UserLogin> userOptional = userLoginRepository.findById(userId);
//	        if(!userOptional.isPresent()) {
//	            throw new RuntimeException("User not found: " + userId);
//	        }
//
//	        UserLogin user = userOptional.get();
//	        Long phone = user.getPhone();
//	        
//	        if (deliveredStatus) {
//	            String orderID = order.getOrderId();
//	            smsNotificationService.sendOtpToMobile(phone,(long) otp);
//	        }
//	    }
//	    
//	    return "Success";
//	}


}
