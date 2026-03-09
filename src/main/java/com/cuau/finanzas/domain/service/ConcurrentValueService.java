package com.cuau.finanzas.domain.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.domain.repository.ConcurrentValueRepository;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcurrentValueService {

	private static final Logger LOGGER = getLogger(ConcurrentValueService.class);
	private static final String CONCURRENT_VALUE_RESOURCE_NAME = "ConcurrentValue";
	private static final String NAME_FIELD_NAME = "name";

	private final ConcurrentValueRepository repository;

	@Transactional
	public ConcurrentValue saveConcurrentValue(ConcurrentValue value) {
		return repository.save(value);
	}

	@Transactional
	public List<ConcurrentValue> saveConcurrentValues(List<ConcurrentValue> values) {
		return repository.saveAll(values);
	}

	@Transactional
	public void incrementAmount(BalanceUpdate balance) {
		LOGGER.info("init process to increment {} balance", balance.valueName());
		int rowsModified = repository.incrementAmount(balance.valueName(), balance.amount());
		if (rowsModified == 0) {
			throw new EntityNotFoundException("No value found with name: " + balance.valueName());
		}
		LOGGER.info("rows modified: {} for value: {} and this amount: {}", String.valueOf(rowsModified),
				balance.valueName(), String.valueOf(balance.amount()));
	}

	@Transactional(readOnly = true)
	public ConcurrentValue getConcurrentValue(String name) {
		return repository.findByName(name).orElseThrow(
				() -> new ResourceNotFoundException(CONCURRENT_VALUE_RESOURCE_NAME, NAME_FIELD_NAME, name));
	}

}
