package com.cuau.finanzas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cuau.finanzas.domain.model.DebitTransaction;

@Repository
public interface DebitTransactionRepository extends JpaRepository<DebitTransaction, Long>{

}
