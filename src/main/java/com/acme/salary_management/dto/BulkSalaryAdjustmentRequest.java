package com.acme.salary_management.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record BulkSalaryAdjustmentRequest(

        @NotEmpty(message = "At least one employee must be selected")
        List<Long> employeeIds,

        @NotNull(message = "Salary adjustment is required")
        BigDecimal adjustment

) {
}