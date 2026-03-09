package com.cuau.finanzas.infrastructure.controller;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.domain.service.ConcurrentValueService;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueRequest;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueResponse;
import com.cuau.finanzas.infrastructure.mapper.ConcurrentValueMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/concurrentvalue")
@RequiredArgsConstructor
public class ConcurrentValueController {

	private static final Logger LOGGER = getLogger(ConcurrentValueController.class);

	private final ConcurrentValueService service;
	private final ConcurrentValueMapper mapper;

	@PostMapping
	public ResponseEntity<ConcurrentValueResponse> saveConcurrentValue(
			@RequestBody @Valid ConcurrentValueRequest body) {
		LOGGER.info("Concurrent Value to save request received");
		ConcurrentValue valueSaved = service.saveConcurrentValue(mapper.toModel(body));
		return ResponseEntity.created(URI.create("/concurrentvalue/" + valueSaved.getId()))
				.body(mapper.toResponse(valueSaved));
	}

	@PostMapping("/batch")
	public ResponseEntity<List<ConcurrentValueResponse>> saveConcurrentValues(
			@RequestBody @Valid List<ConcurrentValueRequest> body) {
		LOGGER.info("List of Concurrent Values to save request received");
		List<ConcurrentValue> valuesSaved = service.saveConcurrentValues(mapper.toModel(body));
		return ResponseEntity.status(CREATED).body(mapper.toResponse(valuesSaved));
	}

	@GetMapping("/{name}")
	public ResponseEntity<ConcurrentValueResponse> getConcurrentValue(@PathVariable String name) {
		LOGGER.info("Request to get Concurrent Value with this name: {}", name);
		return ResponseEntity.ok(mapper.toResponse(service.getConcurrentValue(name)));
	}

}
