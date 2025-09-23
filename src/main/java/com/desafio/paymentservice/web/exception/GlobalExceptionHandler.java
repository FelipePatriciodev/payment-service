package com.desafio.paymentservice.web.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {

		// loga a exceção completa com stack trace
		log.error("Erro inesperado no endpoint {}: {}", request.getDescription(false), ex.getMessage(), ex);

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.put("error", "Internal Server Error");
		response.put("message", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
		response.put("path", request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Validações @Valid
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
		Map<String, Object> body = getDefaultBody(HttpStatus.BAD_REQUEST, request);

		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		body.put("errors", errors);

		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	//JSON inválido ou malformado
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex, WebRequest request) {
		Map<String, Object> body = getDefaultBody(HttpStatus.BAD_REQUEST, request);
		body.put("message", "Requisição inválida. Verifique os tipos de dados do JSON.");
		body.put("details", ex.getMostSpecificCause().getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	// Entidade não encontrada
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
		Map<String, Object> body = getDefaultBody(HttpStatus.NOT_FOUND, request);
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	// Método auxiliar para montar corpo base
	private Map<String, Object> getDefaultBody(HttpStatus status, WebRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("path", request.getDescription(false).replace("uri=", ""));
		return body;
	}
}
