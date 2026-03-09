package com.cuau.finanzas.infrastructure.controller;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail handleIllegatArgumentException(Exception ex, WebRequest request) {

		LOGGER.error("ERROR en API - IllegalArgumentException", ex);
		MDC.clear();

		ProblemDetail problem = ProblemDetail.forStatus(BAD_REQUEST);

		problem.setTitle("Error in request");
		problem.setDetail(ex.getMessage());
		problem.setProperty("path", request.getDescription(false));

		return problem;
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	public ProblemDetail handleUnsupportedOperationException(Exception ex, WebRequest request) {

		LOGGER.error("ERROR en API - UnsupportedOperationException", ex);
		MDC.clear();

		ProblemDetail problem = ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);

		problem.setTitle("Error in API");
		problem.setDetail(ex.getMessage());
		problem.setProperty("path", request.getDescription(false));

		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

		LOGGER.error("ERROR en API - MethodArgumentNotValidException", ex);
		MDC.clear();

		ProblemDetail problem = ProblemDetail.forStatus(BAD_REQUEST);

		problem.setTitle("Validation error");

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();

		problem.setProperty("errors", errors);
		problem.setProperty("path", request.getDescription(false));

		return problem;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {

		LOGGER.error("ERROR en API - ConstraintViolationException", ex);
		MDC.clear();

		ProblemDetail problem = ProblemDetail.forStatus(BAD_REQUEST);
		problem.setTitle("Validation error");

		List<String> errors = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()).toList();

		problem.setProperty("errors", errors);
		problem.setProperty("path", request.getDescription(false));

		return problem;
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
	    problem.setTitle("Resource Not Found");
	    problem.setDetail(ex.getMessage());
	    problem.setProperty("resource", ex.getResourceName());
	    problem.setProperty("field", ex.getFieldName());
	    problem.setProperty("value", ex.getFieldValue());
	    return problem;
	}

}
