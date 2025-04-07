//package com.bank.transaction.service.exception;
//
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//
//@ControllerAdvice
//public class HandlerException {
//	
//	@ExceptionHandler(value = {RequestException.class})
//	public ResponseEntity<Object> NotFoundException(RequestException c)
//	{
//		Exception exception = new Exception(
//				c.getMessage(),
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z"))	
//				);
//
//	return new ResponseEntity<> (exception, HttpStatus.BAD_REQUEST);
//	}
//	
//	
//	
//
//}
