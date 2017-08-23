package com.dakare.radiorecord.web.repository;

import com.dakare.radiorecord.web.domain.HistoryRecordItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRecordItemRepository extends JpaRepository<HistoryRecordItem, Long> {
}
