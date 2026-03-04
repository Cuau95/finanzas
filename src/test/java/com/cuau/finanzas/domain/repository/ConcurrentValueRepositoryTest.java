package com.cuau.finanzas.domain.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cuau.finanzas.domain.model.ConcurrentValue;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = ConcurrentValueRepository.class)
@EntityScan(basePackageClasses = ConcurrentValue.class)
public class ConcurrentValueRepositoryTest {

	private static final String TEST_VALUE_NAME = "TestValue";
	private static final String OTHER_NAME = "name";

	@Autowired
	private ConcurrentValueRepository repository;

	@Test
	void shouldIncrementAConcurrentValueAndReturnHowManyRowsWasModified() {
		ConcurrentValue value = new ConcurrentValue();
		value.setId(null);
		value.setName(TEST_VALUE_NAME);
		value.setAmount(BigDecimal.valueOf(1));
		value.setLastUpdateDate(null);
		repository.save(value);

		int rowsModified = assertDoesNotThrow(() -> repository.incrementAmount(TEST_VALUE_NAME, BigDecimal.valueOf(1)));

		//Add find by name and asserts when that method being implemented
		assertEquals(1, rowsModified);
	}

	@Test
	void shouldTryIncrementAConcurrentValueNotFoundAndReturnZeroRowsModified() {
		ConcurrentValue value = new ConcurrentValue();
		value.setId(null);
		value.setName(TEST_VALUE_NAME);
		value.setAmount(BigDecimal.valueOf(1));
		value.setLastUpdateDate(null);
		repository.save(value);

		int rowsModified = assertDoesNotThrow(() -> repository.incrementAmount(OTHER_NAME, BigDecimal.valueOf(1)));

		assertEquals(0, rowsModified);
	}

}
