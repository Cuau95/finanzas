package com.cuau.finanzas.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.cuau.finanzas.domain.enums.CreditType;
import com.cuau.finanzas.domain.enums.CronologyType;
import com.cuau.finanzas.domain.enums.DebitType;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.infrastructure.dto.CreditTransactionDto;
import com.cuau.finanzas.infrastructure.dto.DebitTransactionDto;

public class TransactionMapperTest {

	private TransactionMapper mapper = new TransactionMapper();

	@Test
	void shouldCreateADebitDtoFromAmodel() {
		DebitTransaction model = buildDebit();

		DebitTransactionDto dto = assertDoesNotThrow(() -> (DebitTransactionDto) mapper.dtoFrom(model));

		assertAll(() -> assertEquals(model.getId(), dto.getId()),
				() -> assertEquals(model.getAmount(), dto.getAmount()),
				() -> assertEquals(model.getCronologyType(), dto.getCronologyType()),
				() -> assertEquals(model.getDate(), dto.getDate()), () -> assertEquals(model.getName(), dto.getName()),
				() -> assertEquals(model.getDebitType(), dto.getDebitType()));
	}

	@Test
	void shouldCreateACreditDtoFromAModel() {
		CreditTransaction model = buildCredit();

		CreditTransactionDto dto = assertDoesNotThrow(() -> (CreditTransactionDto) mapper.dtoFrom(model));

		assertAll(() -> assertEquals(model.getId(), dto.getId()),
				() -> assertEquals(model.getAmount(), dto.getAmount()),
				() -> assertEquals(model.getCronologyType(), dto.getCronologyType()),
				() -> assertEquals(model.getDate(), dto.getDate()), () -> assertEquals(model.getName(), dto.getName()),
				() -> assertEquals(model.getCreditType(), dto.getCreditType()),
				() -> assertEquals(model.getInterest(), dto.getInterest()),
				() -> assertEquals(model.getNumberActualPayment(), dto.getNumberActualPayment()),
				() -> assertEquals(model.getTotalPayments(), dto.getTotalPayments()));
	}

	@Test
	void shouldCreateADebitFromADto() {
		DebitTransactionDto dto = buildDebitDto();

		DebitTransaction modelFetched = assertDoesNotThrow(() -> (DebitTransaction) mapper.modelFrom(dto));

		assertAll(() -> assertEquals(dto.getAmount(), modelFetched.getAmount()),
				() -> assertEquals(dto.getCronologyType(), modelFetched.getCronologyType()),
				() -> assertEquals(dto.getDate(), modelFetched.getDate()),
				() -> assertEquals(dto.getName(), modelFetched.getName()),
				() -> assertEquals(dto.getDebitType(), modelFetched.getDebitType()));
	}

	@Test
	void shouldCreateACreditFromADto() {
		CreditTransactionDto dto = buildCreditDto();

		CreditTransaction modelFetched = assertDoesNotThrow(() -> (CreditTransaction) mapper.modelFrom(dto));

		assertAll(() -> assertEquals(dto.getAmount(), modelFetched.getAmount()),
				() -> assertEquals(dto.getCronologyType(), modelFetched.getCronologyType()),
				() -> assertEquals(dto.getDate(), modelFetched.getDate()),
				() -> assertEquals(dto.getName(), modelFetched.getName()),
				() -> assertEquals(dto.getCreditType(), modelFetched.getCreditType()),
				() -> assertEquals(dto.getInterest(), modelFetched.getInterest()),
				() -> assertEquals(dto.getNumberActualPayment(), modelFetched.getNumberActualPayment()),
				() -> assertEquals(dto.getTotalPayments(), modelFetched.getTotalPayments()));
	}

	private static DebitTransaction buildDebit() {
		DebitTransaction debit = new DebitTransaction();
		debit.setId(2L);
		debit.setAmount(BigDecimal.valueOf(100.01));
		debit.setCronologyType(CronologyType.CALCULATED);
		debit.setDate(LocalDate.of(2026, 2, 26));
		debit.setDebitType(DebitType.ADD_BUCKET);
		debit.setName("Some Funny Debit Name");
		return debit;
	}

	private static CreditTransaction buildCredit() {
		CreditTransaction credit = new CreditTransaction();
		credit.setId(1L);
		credit.setAmount(BigDecimal.valueOf(100.01));
		credit.setCreditType(CreditType.AUTODEBIT);
		credit.setCronologyType(CronologyType.ACTUAL);
		credit.setDate(LocalDate.of(2026, 2, 26));
		credit.setInterest(null);
		credit.setName("Some Funny Credit Name");
		credit.setNumberActualPayment(null);
		credit.setTotalPayments(null);
		return credit;
	}

	private static DebitTransactionDto buildDebitDto() {
		DebitTransactionDto debit = new DebitTransactionDto();
		debit.setId(2L);
		debit.setAmount(BigDecimal.valueOf(100.01));
		debit.setCronologyType(CronologyType.CALCULATED);
		debit.setDate(LocalDate.of(2026, 2, 26));
		debit.setDebitType(DebitType.EXPENSE);
		debit.setName("Some Funny Debit Name");
		return debit;
	}

	private static CreditTransactionDto buildCreditDto() {
		CreditTransactionDto credit = new CreditTransactionDto();
		credit.setId(1L);
		credit.setAmount(BigDecimal.valueOf(100.01));
		credit.setCreditType(CreditType.DEFERRED);
		credit.setCronologyType(CronologyType.ACTUAL);
		credit.setDate(LocalDate.of(2026, 2, 26));
		credit.setInterest(BigDecimal.valueOf(1.01));
		credit.setName("Some Funny Credit Name");
		credit.setNumberActualPayment(1);
		credit.setTotalPayments(2);
		return credit;
	}

}
