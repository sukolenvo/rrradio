package com.dakare.radiorecord.web.repository;

import com.dakare.radiorecord.web.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
