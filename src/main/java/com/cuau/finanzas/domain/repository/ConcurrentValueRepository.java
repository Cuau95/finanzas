package com.cuau.finanzas.domain.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cuau.finanzas.domain.model.ConcurrentValue;

@Repository
public interface ConcurrentValueRepository extends JpaRepository<ConcurrentValue, Long> {

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ConcurrentValue c SET c.amount = c.amount + :amount, c.lastUpdateDate = CURRENT_TIMESTAMP WHERE c.name = :name")
	int incrementAmount(@Param("name") String name, @Param("amount") BigDecimal amount);

	Optional<ConcurrentValue> findByName(String name);

}
