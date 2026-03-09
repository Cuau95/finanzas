package com.cuau.finanzas.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.domain.repository.ConcurrentValueRepository;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ConcurrentValueServiceTest {

	private ConcurrentValue value = new ConcurrentValue();
	private List<ConcurrentValue> values = List.of(value);

	private static final String NAME = "SomeFunnyName";
	private static final BigDecimal AMOUNT = BigDecimal.valueOf(1);
	private BalanceUpdate balance = new BalanceUpdate(NAME, AMOUNT);

	@Mock
	private ConcurrentValueRepository repository;

	@InjectMocks
	private ConcurrentValueService service;

	@Test
	void shouldDoSaveConcurrentValueProcess() {
		value.setId(1L);
		when(repository.save(any(ConcurrentValue.class))).then(returnsFirstArg());

		ConcurrentValue valueSaved = assertDoesNotThrow(() -> service.saveConcurrentValue(value));

		assertAll(() -> assertNotNull(valueSaved), () -> assertSame(valueSaved, value),
				() -> verify(repository, times(1)).save(eq(value)));

	}

	@Test
	void shouldPropagateExcepctionWhenSaveConcurrentValueThrows() {
		when(repository.save(any(ConcurrentValue.class)))
				.thenThrow(new IllegalArgumentException("this test had failed by a funny argument"));

		assertAll(() -> assertThrows(IllegalArgumentException.class, () -> service.saveConcurrentValue(value)),
				() -> verify(repository, times(1)).save(eq(value)));

	}

	@Test
	void shouldDoSaveConcurrentValuesProcess() {
		value.setId(2L);
		when(repository.saveAll(any())).thenReturn(values);

		List<ConcurrentValue> valuesSaved = assertDoesNotThrow(() -> service.saveConcurrentValues(values));

		assertAll(() -> assertFalse(valuesSaved.isEmpty()), () -> assertSame(value, valuesSaved.get(0)),
				() -> verify(repository, times(1)).saveAll(eq(values)));
	}

	@Test
	void shouldPropagateExceptionWhenSaveConcurrentValuesThorws() {
		when(repository.saveAll(any()))
				.thenThrow(new IllegalArgumentException("this test had failed by a funny argument"));

		assertAll(() -> assertThrows(IllegalArgumentException.class, () -> service.saveConcurrentValues(values)),
				() -> verify(repository, times(1)).saveAll(eq(values)));

	}

	@Test
	void shouldCallIncrementAmountAndValidateResultWhenIncrementWasDoneInAtLeastOneRow() {
		when(repository.incrementAmount(anyString(), any(BigDecimal.class))).thenReturn(1);

		assertAll(() -> assertDoesNotThrow(() -> service.incrementAmount(balance)),
				() -> verify(repository, times(1)).incrementAmount(eq(NAME), eq(AMOUNT)));
	}

	@Test
	void shouldCallIncrementAmountAndValidateResultWhenNoRegisterToUpdate() {
		when(repository.incrementAmount(anyString(), any(BigDecimal.class))).thenReturn(0);

		assertAll(() -> assertThrows(EntityNotFoundException.class, () -> service.incrementAmount(balance)),
				() -> verify(repository, times(1)).incrementAmount(eq(NAME), eq(AMOUNT)));
	}

	@Test
	void shouldReturnConcurrentValueWhenNameIsValidArgument() {
		when(repository.findByName(eq(NAME))).thenReturn(Optional.of(value));

		ConcurrentValue valueFetched = assertDoesNotThrow(() -> service.getConcurrentValue(NAME));

		assertAll(() -> assertNotNull(valueFetched), () -> verify(repository).findByName(eq(NAME)));
	}

	@Test
	void shouldPropagateExceptionWhenRepositoryThrows() {
		when(repository.findByName(eq(NAME))).thenReturn(Optional.empty());

		assertAll(() -> assertThrows(ResourceNotFoundException.class, () -> service.getConcurrentValue(NAME)),
				() -> verify(repository).findByName(eq(NAME)));
	}

}
