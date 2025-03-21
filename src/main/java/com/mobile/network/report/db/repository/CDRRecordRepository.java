package com.mobile.network.report.db.repository;

import com.mobile.network.report.db.entity.CDRRecord;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CDRRecordRepository extends JpaRepository<CDRRecord, UUID> {
    List<CDRRecord> findAllByCallerPhoneNumberAndCallStartTimeBetween(String phoneNumber, Instant start, Instant end);
    List<CDRRecord> findAllByReceiverPhoneNumberAndCallStartTimeBetween(String phoneNumber, Instant start, Instant end);
    List<CDRRecord> findAllByCallStartTimeBetween(Instant start, Instant end);
    @Query("SELECT r FROM CDRRecord r WHERE r.callerPhoneNumber = :phoneNumber OR r.receiverPhoneNumber = :phoneNumber")
    List<CDRRecord> findAllByCallerPhoneNumberOrReceiverPhoneNumber(String phoneNumber);
}
