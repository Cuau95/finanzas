package com.cuau.finanzas.domain.rules;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.cuau.finanzas.domain.enums.ConcurrentValueName;
import com.cuau.finanzas.domain.enums.CreditType;
import com.cuau.finanzas.domain.enums.DebitType;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

public class TransactionBusinessRulesTest {

	private static final BigDecimal BASE_AMOUNT = BigDecimal.valueOf(100.01);

	TransactionBusinessRules rules = new TransactionBusinessRules();

	@ParameterizedTest
	@MethodSource("debitScenarios")
	void shouldCalculateAmountAccordingToDebitType(DebitType type, List<BalanceUpdate> expectedBalances) {
		List<BalanceUpdate> balances = assertDoesNotThrow(() -> rules.generateBalances(buildDebitTransaction(type)));

		assertAll(() -> assertNotNull(balances), () -> assertEquals(expectedBalances.size(), balances.size()),
				() -> assertEquals(new HashSet<>(expectedBalances), new HashSet<>(balances)));
	}

	@ParameterizedTest
	@MethodSource("creditScenarios")
	void shouldCalculateAmountAccordingToCreditType(CreditType type, BigDecimal expectedValue) {
		List<BalanceUpdate> balances = assertDoesNotThrow(() -> rules.generateBalances(buildCreditTransaction(type)));

		assertAll(() -> assertNotNull(balances), () -> assertFalse(balances.isEmpty()),
				() -> assertEquals(1, balances.size()), () -> assertEquals(expectedValue, balances.get(0).amount()),
				() -> assertEquals(ConcurrentValueName.CREDIT_BALANCE.getName(), balances.get(0).valueName()));
	}

	@Test
	void shouldThrowExceptionToNullValue() {
		assertThrows(NullPointerException.class, () -> rules.generateBalances(null));
	}

	private DebitTransaction buildDebitTransaction(DebitType type) {
		DebitTransaction debit = new DebitTransaction();
		debit.setAmount(BASE_AMOUNT);
		debit.setDebitType(type);
		return debit;
	}

	private CreditTransaction buildCreditTransaction(CreditType type) {
		CreditTransaction credit = new CreditTransaction();
		credit.setAmount(BASE_AMOUNT);
		credit.setCreditType(type);
		if (CreditType.DEFERRED.equals(type)) {
			credit.setInterest(BASE_AMOUNT);
		}
		return credit;
	}

	private static Stream<Arguments> creditScenarios() {
		return Stream.of(Arguments.of(CreditType.DEFERRED, BASE_AMOUNT),
				Arguments.of(CreditType.PAYMENT, BASE_AMOUNT.negate()), Arguments.of(CreditType.EXPENSE, BASE_AMOUNT),
				Arguments.of(CreditType.AUTODEBIT, BASE_AMOUNT));
	}

	private static Stream<Arguments> debitScenarios() {
		return Stream.of(
				Arguments.of(DebitType.ADD_BUCKET,
						List.of(new BalanceUpdate(ConcurrentValueName.TOTAL_SAVING_BUCKET.getName(), BASE_AMOUNT),
								new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT.negate()))),
				Arguments.of(DebitType.TAKE_BUCKET,
						List.of(new BalanceUpdate(ConcurrentValueName.TOTAL_SAVING_BUCKET.getName(),
								BASE_AMOUNT.negate()),
								new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT))),
				Arguments.of(DebitType.AUTODEBIT,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT.negate()))),
				Arguments.of(DebitType.EXPENSE,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT.negate()))),
				Arguments.of(DebitType.EXPENSE_CONCURRENT,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT.negate()))),
				Arguments.of(DebitType.INCOME,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT))),
				Arguments.of(DebitType.NOMINA,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT))),
				Arguments.of(DebitType.INCOME_CONCURRENT,
						List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), BASE_AMOUNT))));
	}

}
