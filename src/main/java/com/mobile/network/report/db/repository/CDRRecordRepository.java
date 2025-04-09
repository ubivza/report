package com.mobile.network.report.db.repository;

import com.mobile.network.report.db.entity.CDRRecord;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CDRRecordRepository extends JpaRepository<CDRRecord, UUID> {
}
