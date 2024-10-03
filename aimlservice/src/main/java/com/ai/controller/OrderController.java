package com.ai.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.dto.OrderRequest;
import com.ai.exception.ApplicationException;
import com.ai.service.OrderService;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

	@Autowired
	OrderService orderService;

	@PostMapping("/save")
	public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody List<OrderRequest> orderRequests,
			@RequestParam Long userId, @RequestParam Long locationId) throws ApplicationException {
		return new ResponseEntity<Object>(orderService.saveOrderWithOrderDetails(orderRequests, userId, locationId),
				HttpStatus.OK);
	}

	@GetMapping("/byUser")
	public ResponseEntity<Object> getOrdersByUserId(@RequestParam Long id) throws ApplicationException {
		return new ResponseEntity<Object>(orderService.getOrdersByUserId(id), HttpStatus.OK);
	}

	@GetMapping("/id")
	public ResponseEntity<Object> getOrderWithOrderDetailsById(@RequestParam Long id) throws ApplicationException {
		return new ResponseEntity<Object>(orderService.getOrderWithOrderDetailsById(id), HttpStatus.OK);
	}

//	@GetMapping(value = "/generateQr", produces = MediaType.IMAGE_PNG_VALUE)
//	public ResponseEntity<Object> generateQrCodeForOrderId(@RequestParam Long id) throws WriterException {
//		return new ResponseEntity<Object>(orderService.generateQrCodeForOrderId(id), HttpStatus.OK);
//	}
//
//	@GetMapping("/getItemQty")
//	public ResponseEntity<Object> getOrderedItemsWithQuantityForToday(@RequestParam Long locationId) {
//		return new ResponseEntity<Object>(orderService.getOrderedItemsWithQuantityForToday(locationId), HttpStatus.OK);
//	}
//
//	@GetMapping("/today")
//	public ResponseEntity<Object> getTodayOrders(@RequestParam Long locationId) {
//		return new ResponseEntity<Object>(orderService.getTodayOrders(locationId), HttpStatus.OK);
//	}
//
//	@GetMapping("/dashboard")
//	public ResponseEntity<Object> getOrderItemCount(@RequestParam Long locationId) {
//		return new ResponseEntity<Object>(orderService.getOrderItemCount(locationId), HttpStatus.OK);
//	}
//
//	@PostMapping("/report")
//	public ResponseEntity<Object> generateOrderDetailsExcelReport(
//			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//			@RequestParam(required = false) Long locationId) throws IOException {
//		byte[] in = orderService.generateOrderDetailsExcelReport(date, locationId);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//		String filename = "Order_Report.xlsx";
//		headers.setContentDispositionFormData(filename, filename);
//		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//		return new ResponseEntity<>(in, headers, HttpStatus.OK);
//	}
//
//	 
//		@PostMapping("/getTotalQuantityOrderDetailsExcel")
//		public ResponseEntity<Object> getTotalQuantityOrderDetailsExcel(
//				@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//				@RequestParam(required = false) Long locationId) throws IOException {
//			byte[] in = orderService.getTotalQuantityOrderDetailsExcel(date, locationId);
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//			String filename = "Total_Order_Report.xlsx";
//			headers.setContentDispositionFormData(filename, filename);
//			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//			return new ResponseEntity<>(in, headers, HttpStatus.OK);
//		}
//		
//		 
//		 @PostMapping("/updateShippedStatus")
//		    public ResponseEntity<Object> updateShippedStatus(@RequestBody OrderStatusUpdateRequest request) {
//		        String result = orderService.updateShippedStatus(request);
//		        return new ResponseEntity<>(result, HttpStatus.OK);
//		    }
//		 
//		 
//		 @PostMapping("/updateDeliveredStatus")
//		 public String updateDeliveredStatus(@RequestBody OrderDeliveredRequest request){
//			String result =  orderService.updateDeliveredStatus(request);
//			return result;
//			 
//		 }
}
