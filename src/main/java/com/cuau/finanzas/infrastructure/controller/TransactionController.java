package com.cuau.finanzas.infrastructure.controller;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.service.TransactionService;
import com.cuau.finanzas.infrastructure.dto.TransactionDto;
import com.cuau.finanzas.infrastructure.mapper.TransactionMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

	private static final Logger LOGGER = getLogger(TransactionController.class);

	private final TransactionService service;
	private final TransactionMapper mapper;

	@PostMapping
	public ResponseEntity<TransactionDto> createDto(@RequestBody @Valid TransactionDto body) {
		LOGGER.info("Create transaction request recieved");
		Transaction transactionSaved = service.saveTransaction(mapper.modelFrom(body));
		return ResponseEntity.status(CREATED).body(mapper.dtoFrom(transactionSaved));
	}

	@PostMapping("/batch")
	public ResponseEntity<List<TransactionDto>> createDtos(@RequestBody @Valid List<@Valid TransactionDto> body) {
		LOGGER.info("Create transactions (BATCH) request recieved");
		List<Transaction> transactionsSaved = service.saveTransactions(mapper.modelFrom(body));
		return ResponseEntity.status(CREATED).body(mapper.dtoFrom(transactionsSaved));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
		LOGGER.info("Request received to get Transaction with this ID: {}", id);
		return ResponseEntity.ok(mapper.dtoFrom(service.getTransaction(id)));
	}

}
