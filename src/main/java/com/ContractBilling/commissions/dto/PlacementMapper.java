package com.ContractBilling.commissions.dto;

import com.ContractBilling.commissions.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Placement entities and DTOs
 */
@Component
@RequiredArgsConstructor
public class PlacementMapper {

    private final SalespersonMapper salespersonMapper;
    private final ClientMapper clientMapper;
    private final ContractorMapper contractorMapper;

    /**
     * Convert Placement entity to PlacementResponse DTO
     */
    public PlacementResponse toResponse(Placement entity) {
        if (entity == null) {
            return null;
        }

        PlacementResponse response = new PlacementResponse();
        response.setId(entity.getId());

        // Convert nested entities
        response.setSalesperson(salespersonMapper.toResponse(entity.getSalesperson()));
        response.setClient(clientMapper.toResponse(entity.getClient()));
        response.setContractor(contractorMapper.toResponse(entity.getContractor()));

        // Basic fields
        response.setPlacementType(entity.getPlacementType());
        response.setStatus(entity.getStatus());

        // Dates
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());

        // Contractor fields
        response.setHoursPerWeek(entity.getHoursPerWeek());
        response.setWeeksPerYear(entity.getWeeksPerYear());

        // Pay fields
        response.setPayType(entity.getPayType());
        response.setAnnualSalary(entity.getAnnualSalary());
        response.setHourlyPayRate(entity.getHourlyPayRate());

        // Bill fields
        response.setBillRate(entity.getBillRate());
        response.setMarginPercentage(entity.getMarginPercentage());

        // Overhead fields
        response.setAdminPercentage(entity.getAdminPercentage());
        response.setInsurancePercentage(entity.getInsurancePercentage());
        response.setFixedCosts(entity.getFixedCosts());

        // Calculated fields
        response.setHourlyPayCost(entity.getHourlyPayCost());
        response.setMarginPerHour(entity.getMarginPerHour());
        response.setWeeklyMargin(entity.getWeeklyMargin());
        response.setGrossAnnualMargin(entity.getGrossAnnualMargin());
        response.setNetAnnualMargin(entity.getNetAnnualMargin());

        // Commission fields
        response.setSequenceNumber(entity.getSequenceNumber());
        response.setCommissionPercentage(entity.getCommissionPercentage());
        response.setCommissionTotal(entity.getCommissionTotal());

        // Permanent fields
        response.setPlacementFee(entity.getPlacementFee());
        response.setFeeType(entity.getFeeType());
        response.setCandidateSalary(entity.getCandidateSalary());
        response.setRecognitionPeriodMonths(entity.getRecognitionPeriodMonths());

        // Audit fields
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    /**
     * Convert CreatePlacementRequest DTO to Placement entity (partial)
     * Relationships will be set by service layer
     */
    public Placement toEntity(CreatePlacementRequest request) {
        if (request == null) {
            return null;
        }

        Placement entity = new Placement();

        // Basic fields
        entity.setPlacementType(request.getPlacementType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());

        // Contractor fields
        entity.setHoursPerWeek(request.getHoursPerWeek());
        entity.setWeeksPerYear(request.getWeeksPerYear());
        entity.setPayType(request.getPayType());
        entity.setAnnualSalary(request.getAnnualSalary());
        entity.setHourlyPayRate(request.getHourlyPayRate());
        entity.setBillRate(request.getBillRate());

        // Permanent fields
        entity.setPlacementFee(request.getPlacementFee());
        entity.setFeeType(request.getFeeType());
        entity.setCandidateSalary(request.getCandidateSalary());
        entity.setRecognitionPeriodMonths(request.getRecognitionPeriodMonths());

        return entity;
    }

    /**
     * Update existing Placement entity with data from UpdatePlacementRequest DTO
     */
    public void updateEntity(Placement entity, UpdatePlacementRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getStartDate() != null) {
            entity.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            entity.setEndDate(request.getEndDate());
        }
        if (request.getHoursPerWeek() != null) {
            entity.setHoursPerWeek(request.getHoursPerWeek());
        }
        if (request.getWeeksPerYear() != null) {
            entity.setWeeksPerYear(request.getWeeksPerYear());
        }
        if (request.getPayType() != null) {
            entity.setPayType(request.getPayType());
        }
        if (request.getAnnualSalary() != null) {
            entity.setAnnualSalary(request.getAnnualSalary());
        }
        if (request.getHourlyPayRate() != null) {
            entity.setHourlyPayRate(request.getHourlyPayRate());
        }
        if (request.getBillRate() != null) {
            entity.setBillRate(request.getBillRate());
        }
        if (request.getPlacementFee() != null) {
            entity.setPlacementFee(request.getPlacementFee());
        }
        if (request.getFeeType() != null) {
            entity.setFeeType(request.getFeeType());
        }
        if (request.getCandidateSalary() != null) {
            entity.setCandidateSalary(request.getCandidateSalary());
        }
        if (request.getRecognitionPeriodMonths() != null) {
            entity.setRecognitionPeriodMonths(request.getRecognitionPeriodMonths());
        }
    }
}
