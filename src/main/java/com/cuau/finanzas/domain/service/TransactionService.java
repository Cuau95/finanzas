package com.cuau.finanzas.domain.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.repository.TransactionRepository;
import com.cuau.finanzas.domain.rules.TransactionBusinessRules;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

	private static final Logger LOGGER = getLogger(TransactionService.class);

	private final TransactionRepository repository;
	private final ConcurrentValueService concurrentValueService;
	private final TransactionBusinessRules rules;

	public Transaction saveTransaction(Transaction transaction) {
		Transaction transactionSaved = repository.saveAndFlush(transaction);
		LOGGER.info("Transaction created with ID: {}", transactionSaved.getId());
		incrementBalance(transactionSaved);
		return transactionSaved;
	}

	public List<Transaction> saveTransactions(List<Transaction> transactions) {
		List<Transaction> transactionsSaved = repository.saveAllAndFlush(transactions);
		transactionsSaved.forEach(transaction -> {
			LOGGER.info("Transaction created with ID: {}", transaction.getId());
			incrementBalance(transaction);
		});

		return transactionsSaved;
	}

	private void incrementBalance(Transaction transactionSaved) {
		List<BalanceUpdate> balance = rules.generateBalances(transactionSaved);
		balance.forEach(concurrentValueService::incrementAmount);
	}

}
