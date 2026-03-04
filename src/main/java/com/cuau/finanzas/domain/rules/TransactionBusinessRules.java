package com.cuau.finanzas.domain.rules;

import static java.util.Objects.isNull;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cuau.finanzas.domain.enums.ConcurrentValueName;
import com.cuau.finanzas.domain.enums.CreditType;
import com.cuau.finanzas.domain.enums.DebitType;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.rules.pojo.BalanceUpdate;

@Component
public class TransactionBusinessRules {

	public List<BalanceUpdate> generateBalances(Transaction transaction) {
		if (transaction instanceof CreditTransaction credit) {
			return List.of(
					new BalanceUpdate(ConcurrentValueName.CREDIT_BALANCE.getName(), processCreditTransaction(credit)));
		} else if (transaction instanceof DebitTransaction debit) {
			return generateBalances(debit);
		} else {
			throw new UnsupportedOperationException(
					"No mapper defined for transaction type" + transaction.getClass().getName());
		}
	}

	private List<BalanceUpdate> generateBalances(DebitTransaction debit) {
		if (DebitType.ADD_BUCKET.equals(debit.getDebitType())) {
			return List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), debit.getAmount().negate()),
					new BalanceUpdate(ConcurrentValueName.TOTAL_SAVING_BUCKET.getName(), debit.getAmount()));
		} else if (DebitType.TAKE_BUCKET.equals(debit.getDebitType())) {
			return List.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), debit.getAmount()),
					new BalanceUpdate(ConcurrentValueName.TOTAL_SAVING_BUCKET.getName(), debit.getAmount().negate()));
		} else {
			return List
					.of(new BalanceUpdate(ConcurrentValueName.DEBIT_BALANCE.getName(), processDebitTransaction(debit)));
		}
	}

	private BigDecimal processCreditTransaction(CreditTransaction credit) {
		if (CreditType.PAYMENT.equals(credit.getCreditType())) {
			return credit.getAmount().negate();
		} else if (CreditType.DEFERRED.equals(credit.getCreditType())) {
			//Review this rule: interest add in DEFERRED transactions to total credit balance
			//amount of DEFERRED transaction add in monthly payment
			//In that case we need add CreditType.IMMEDIATE_CASH
			return isNull(credit.getInterest()) ? BigDecimal.valueOf(0) : credit.getInterest();
		} else {
			return credit.getAmount();
		}
	}

	private BigDecimal processDebitTransaction(DebitTransaction debit) {
		return debit.getDebitType().isExpense() ? debit.getAmount().negate() : debit.getAmount();
	}

}
