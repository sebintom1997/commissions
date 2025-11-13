package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.RecognitionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecognitionScheduleRepository extends JpaRepository<RecognitionSchedule, Long> {

    List<RecognitionSchedule> findByCommissionPlanId(Long commissionPlanId);

    List<RecognitionSchedule> findByStatus(String status);

    @Query("SELECT rs FROM RecognitionSchedule rs WHERE rs.recognitionDate <= :date AND rs.status = 'PENDING' ORDER BY rs.recognitionDate")
    List<RecognitionSchedule> findDueForRecognition(@Param("date") LocalDate date);

    @Query("SELECT rs FROM RecognitionSchedule rs WHERE rs.commissionPlan.id = :planId AND rs.status != 'PAID'")
    List<RecognitionSchedule> findUnpaidSchedules(@Param("planId") Long planId);
}
