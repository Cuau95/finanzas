package com.cuau.finanzas.domain.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.repository.CreditTransactionRepository;
import com.cuau.finanzas.domain.repository.DebitTransactionRepository;
import com.cuau.finanzas.domain.repository.TransactionRepository;
import com.cuau.finanzas.domain.rules.TransactionBusinessRules;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private static final Logger LOGGER = getLogger(TransactionService.class);

	private static final String TRANSACTION_RESOURCE_NAME = "Transaction";
	private static final String ID_FIELD_NAME = "id";

	private final TransactionRepository transactionRepo;
	private final CreditTransactionRepository creditRepo;
	private final DebitTransactionRepository debitRepo;
	private final ConcurrentValueService concurrentValueService;
	private final TransactionBusinessRules rules;

	@Transactional
	public Transaction saveTransaction(Transaction transaction) {
		Transaction transactionSaved = transactionRepo.saveAndFlush(transaction);
		LOGGER.info("Transaction created with ID: {}", transactionSaved.getId());
		incrementBalance(transactionSaved);
		return transactionSaved;
	}

	@Transactional
	public List<Transaction> saveTransactions(List<Transaction> transactions) {
		List<Transaction> transactionsSaved = transactionRepo.saveAllAndFlush(transactions);
		transactionsSaved.forEach(transaction -> {
			LOGGER.info("Transaction created with ID: {}", transaction.getId());
			incrementBalance(transaction);
		});

		return transactionsSaved;
	}

	@Transactional(readOnly = true)
	public Transaction getTransaction(Long id) {
		return transactionRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(TRANSACTION_RESOURCE_NAME, ID_FIELD_NAME, id));
	}

	@Transactional(readOnly = true)
	public List<Transaction> getLatestTransactions() {
		return transactionRepo.findTop10ByOrderByDateDesc();
	}

	@Transactional(readOnly = true)
	public List<CreditTransaction> getLatestCreditTransactions() {
		return creditRepo.findTop5ByOrderByDateDesc();
	}
	
	@Transactional(readOnly = true)
	public List<DebitTransaction> getLatestDebitTransaction() {
		return debitRepo.findTop5ByOrderByDateDesc();
	}

	private void incrementBalance(Transaction transactionSaved) {
		List<BalanceUpdate> balance = rules.generateBalances(transactionSaved);
		balance.forEach(concurrentValueService::incrementAmount);
	}

}
