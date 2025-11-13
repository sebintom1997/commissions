package com.ContractBilling.commissions.repository;

import com.ContractBilling.commissions.entity.DrawdownRequest;
import com.ContractBilling.commissions.entity.Salesperson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DrawdownRequestRepository extends JpaRepository<DrawdownRequest, Long> {

    List<DrawdownRequest> findBySalesperson(Salesperson salesperson);

    List<DrawdownRequest> findByStatus(String status);

    List<DrawdownRequest> findBySalespersonAndStatus(Salesperson salesperson, String status);

    @Query("SELECT COUNT(dr) FROM DrawdownRequest dr WHERE dr.salesperson = :salesperson " +
           "AND dr.quarterYear = :year AND dr.quarterNumber = :quarter AND dr.status != 'REJECTED'")
    int countByQuarter(@Param("salesperson") Salesperson salesperson,
                       @Param("year") Integer year,
                       @Param("quarter") Integer quarter);

    @Query("SELECT COALESCE(SUM(dr.approvedAmount), 0) FROM DrawdownRequest dr " +
           "WHERE dr.salesperson = :salesperson AND dr.status = 'APPROVED'")
    BigDecimal sumApprovedAmount(@Param("salesperson") Salesperson salesperson);

    @Query("SELECT COALESCE(SUM(dr.approvedAmount), 0) FROM DrawdownRequest dr " +
           "WHERE dr.salesperson = :salesperson AND dr.status = 'PAID'")
    BigDecimal sumPaidAmount(@Param("salesperson") Salesperson salesperson);
}
