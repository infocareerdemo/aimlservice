package com.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.entity.RazorpayPayment;
import com.ai.exception.ApplicationException;
import com.ai.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

	@Autowired
	PaymentService paymentService;

	@PostMapping("/createOrder")
	public ResponseEntity<Object> createOrder(@RequestParam Long id, @RequestParam Long oid) throws ApplicationException {
		return new ResponseEntity<Object>(paymentService.createOrder(id, oid), HttpStatus.OK);
	}
	
	@PostMapping("/checkStatus")
	public ResponseEntity<Object> checkPaymentStatus(@RequestBody RazorpayPayment razorpayPayment) {
		return new ResponseEntity<Object>(paymentService.isPaymentSuccess(razorpayPayment), HttpStatus.OK);
	}
}
