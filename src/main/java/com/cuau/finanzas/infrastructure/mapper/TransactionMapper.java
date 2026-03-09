package com.cuau.finanzas.infrastructure.mapper;

import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.infrastructure.dto.CreditTransactionDto;
import com.cuau.finanzas.infrastructure.dto.DebitTransactionDto;
import com.cuau.finanzas.infrastructure.dto.TransactionDto;

@Component
public class TransactionMapper {

	// Method to generate response from transaction model in list
	public List<TransactionDto> dtoFrom(List<? extends Transaction> transactions) {
		return transactions.stream().map(this::dtoFrom).toList();
	}

	// Method to generate model from dto in list
	public List<Transaction> modelFrom(List<? extends TransactionDto> dtos) {
		return dtos.stream().map(this::modelFrom).toList();
	}

	// Method to generate response from transaction model
	public TransactionDto dtoFrom(Transaction transaction) {
		if (isNull(transaction)) {
			throw new IllegalArgumentException("transaction can't be null");
		}
		if (transaction instanceof CreditTransaction credit) {
			return creditMapper(credit);
		} else if (transaction instanceof DebitTransaction debit) {
			return debitMapper(debit);
		} else {
			throw new UnsupportedOperationException(
					"No mapper defined for transaction type" + transaction.getClass().getName());
		}
	}

	// Method to generate model from dto
	public Transaction modelFrom(TransactionDto dto) {
		if (isNull(dto)) {
			throw new IllegalArgumentException("dto can't be null");
		}
		if (dto instanceof CreditTransactionDto creditDto) {
			return creditMapper(creditDto);
		} else if (dto instanceof DebitTransactionDto deditDto) {
			return debitMapper(deditDto);
		} else {
			throw new UnsupportedOperationException(
					"No mapper defined for transaction type" + dto.getClass().getName());
		}
	}

	private CreditTransaction creditMapper(CreditTransactionDto creditDto) {
		CreditTransaction transaction = new CreditTransaction();

		mapCommonFieldsToModel(transaction, creditDto);

		transaction.setCreditType(creditDto.getCreditType());
		transaction.setInterest(creditDto.getInterest());
		transaction.setNumberActualPayment(creditDto.getNumberActualPayment());
		transaction.setTotalPayments(creditDto.getTotalPayments());
		return transaction;
	}

	private DebitTransaction debitMapper(DebitTransactionDto creditDto) {
		DebitTransaction transaction = new DebitTransaction();

		mapCommonFieldsToModel(transaction, creditDto);

		transaction.setDebitType(creditDto.getDebitType());
		return transaction;
	}

	private CreditTransactionDto creditMapper(CreditTransaction credit) {
		CreditTransactionDto dto = new CreditTransactionDto();

		mapCommonFieldsToDto(credit, dto);

		dto.setCreditType(credit.getCreditType());
		dto.setInterest(credit.getInterest());
		dto.setNumberActualPayment(credit.getNumberActualPayment());
		dto.setTotalPayments(credit.getTotalPayments());
		return dto;
	}

	private DebitTransactionDto debitMapper(DebitTransaction debit) {
		DebitTransactionDto dto = new DebitTransactionDto();

		mapCommonFieldsToDto(debit, dto);

		dto.setDebitType(debit.getDebitType());
		return dto;
	}

	private void mapCommonFieldsToDto(Transaction source, TransactionDto target) {
		target.setId(source.getId());
		target.setAmount(source.getAmount());
		target.setCronologyType(source.getCronologyType());
		target.setDate(source.getDate());
		target.setName(source.getName());
	}

	private void mapCommonFieldsToModel(Transaction source, TransactionDto target) {
		source.setAmount(target.getAmount());
		source.setCronologyType(target.getCronologyType());
		source.setDate(target.getDate());
		source.setName(target.getName());
	}

}
