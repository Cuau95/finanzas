package com.cuau.finanzas.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cuau.finanzas.domain.model.CreditTransaction;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long>{
	
	List<CreditTransaction> findTop5ByOrderByDateDesc();

}
