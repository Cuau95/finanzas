package com.cuau.finanzas.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.repository.CreditTransactionRepository;
import com.cuau.finanzas.domain.repository.DebitTransactionRepository;
import com.cuau.finanzas.domain.repository.TransactionRepository;
import com.cuau.finanzas.domain.rules.TransactionBusinessRules;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	private static final String NAME = "SomeFunnyName";
	private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.01);

	@Mock
	private TransactionRepository repository;
	@Mock
	private CreditTransactionRepository creditRepo;
	@Mock
	private DebitTransactionRepository debitRepo;
	@Mock
	private ConcurrentValueService concurrentValueService;
	@Mock
	private TransactionBusinessRules rules;

	@InjectMocks
	private TransactionService service;

	@ParameterizedTest
	@MethodSource("transactionInstances")
	void shouldSaveTransactionAndDoIncrementConcurrentValueProcess(Transaction transaction) {
		when(repository.saveAndFlush(any(Transaction.class))).then(returnsFirstArg());
		when(rules.generateBalances(any(Transaction.class))).thenReturn(List.of(new BalanceUpdate(NAME, AMOUNT)));

		Transaction transactionSaved = assertDoesNotThrow(() -> service.saveTransaction(transaction));

		InOrder inOrder = inOrder(repository, rules, concurrentValueService);
		ArgumentCaptor<BalanceUpdate> balanceCaptor = ArgumentCaptor.forClass(BalanceUpdate.class);
		assertAll(() -> assertNotNull(transactionSaved), () -> assertSame(transaction, transactionSaved),
				() -> inOrder.verify(repository).saveAndFlush(eq(transaction)),
				() -> inOrder.verify(rules).generateBalances(eq(transactionSaved)),
				() -> inOrder.verify(concurrentValueService).incrementAmount(balanceCaptor.capture()),
				() -> assertEquals(NAME, balanceCaptor.getValue().valueName()),
				() -> assertEquals(AMOUNT, balanceCaptor.getValue().amount()));
	}

	@ParameterizedTest
	@MethodSource("transactionInstances")
	void shouldPropagateExceptionWhenSaveTransactionThrows(Transaction transaction) {
		when(repository.saveAndFlush(any(Transaction.class)))
				.thenThrow(new IllegalArgumentException("this test had failed by a funny argument"));

		assertAll(() -> assertThrows(IllegalArgumentException.class, () -> service.saveTransaction(transaction)),
				() -> verify(rules, never()).generateBalances(any()),
				() -> verify(concurrentValueService, never()).incrementAmount(any()));
	}

	@ParameterizedTest
	@MethodSource("transactionInstances")
	void shouldPropagateExceptionWhenIncrementConcurrentValueThrows(Transaction transaction) {
		when(repository.saveAndFlush(any(Transaction.class))).then(returnsFirstArg());
		when(rules.generateBalances(any(Transaction.class))).thenReturn(List.of(new BalanceUpdate(NAME, AMOUNT)));
		doThrow(new EntityNotFoundException("LOL a Funny Exception, it wasn't found XD")).when(concurrentValueService)
				.incrementAmount(any(BalanceUpdate.class));

		assertAll(() -> assertThrows(EntityNotFoundException.class, () -> service.saveTransaction(transaction)),
				() -> verify(rules).generateBalances(any(Transaction.class)),
				() -> verify(concurrentValueService).incrementAmount(any(BalanceUpdate.class)));
	}

	@Test
	void shouldSaveListTransactionAndDoIncrementConcurrentValueProcessForEachOne() {
		List<Transaction> transactions = List.of(new CreditTransaction(), new DebitTransaction());
		when(repository.saveAllAndFlush(any())).thenReturn(transactions);
		when(rules.generateBalances(any(Transaction.class))).thenReturn(List.of(new BalanceUpdate(NAME, AMOUNT)));

		List<Transaction> transactionsSaved = assertDoesNotThrow(() -> service.saveTransactions(transactions));

		assertAll(() -> assertNotNull(transactionsSaved), () -> assertFalse(transactionsSaved.isEmpty()),
				() -> assertEquals(2, transactionsSaved.size()),
				() -> verify(rules, times(2)).generateBalances(any(Transaction.class)),
				() -> verify(concurrentValueService, times(2)).incrementAmount(any(BalanceUpdate.class)));
	}

	@ParameterizedTest
	@MethodSource("transactionInstances")
	void shouldReturnTransactionWhenIdIsAValidParam(Transaction transaction) {
		when(repository.findById(anyLong())).thenReturn(Optional.of(transaction));

		Transaction transactionFetched = assertDoesNotThrow(() -> service.getTransaction(1L));

		assertNotNull(transactionFetched);
	}

	@Test
	void shouldPropagateExceptionWhenIsNotFoundSource() {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.getTransaction(1L));
	}

	@Test
	void shouldReturnLatestTop10Transactions() {
		when(repository.findTop10ByOrderByDateDesc())
				.thenReturn(List.of(new CreditTransaction(), new DebitTransaction()));

		List<Transaction> latestTransactions = assertDoesNotThrow(() -> service.getLatestTransactions());

		assertAll(() -> assertNotNull(latestTransactions), () -> assertEquals(2, latestTransactions.size()),
				() -> verify(repository).findTop10ByOrderByDateDesc());
	}

	@Test
	void shouldPropagateExceptionWhenFindTop10TransactionsThrows() {
		when(repository.findTop10ByOrderByDateDesc())
				.thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		assertAll(() -> assertThrows(DataAccessResourceFailureException.class, () -> service.getLatestTransactions()),
				() -> verify(repository).findTop10ByOrderByDateDesc());
	}

	@Test
	void shouldReturnLatestTop5CreditTransactions() {
		when(creditRepo.findTop5ByOrderByDateDesc()).thenReturn(List.of(new CreditTransaction()));

		List<CreditTransaction> latestCreditTransactions = assertDoesNotThrow(
				() -> service.getLatestCreditTransactions());

		assertAll(() -> assertNotNull(latestCreditTransactions), () -> assertEquals(1, latestCreditTransactions.size()),
				() -> verify(creditRepo).findTop5ByOrderByDateDesc());
	}

	@Test
	void shouldPropagateExceptionWhenFindTop5CreditTransactionsThrows() {
		when(creditRepo.findTop5ByOrderByDateDesc())
				.thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		assertAll(
				() -> assertThrows(DataAccessResourceFailureException.class,
						() -> service.getLatestCreditTransactions()),
				() -> verify(creditRepo).findTop5ByOrderByDateDesc());
	}

	@Test
	void shouldReturnLatestTop5DebitTransactions() {
		when(debitRepo.findTop5ByOrderByDateDesc()).thenReturn(List.of(new DebitTransaction()));

		List<DebitTransaction> latestDebitTransactions = assertDoesNotThrow(() -> service.getLatestDebitTransactions());

		assertAll(() -> assertNotNull(latestDebitTransactions), () -> assertEquals(1, latestDebitTransactions.size()),
				() -> verify(debitRepo).findTop5ByOrderByDateDesc());
	}

	@Test
	void shouldPropagateExceptionWhenFindTop5DebitTransactionsThrows() {
		when(debitRepo.findTop5ByOrderByDateDesc()).thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		assertAll(
				() -> assertThrows(DataAccessResourceFailureException.class, () -> service.getLatestDebitTransactions()),
				() -> verify(debitRepo).findTop5ByOrderByDateDesc());
	}

	private static Stream<Transaction> transactionInstances() {
		return Stream.of(new CreditTransaction(), new DebitTransaction());
	}

}
