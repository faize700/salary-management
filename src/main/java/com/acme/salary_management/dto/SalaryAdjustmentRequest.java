package com.acme.salary_management.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SalaryAdjustmentRequest(

        @NotNull(message = "Salary adjustment is required")
        BigDecimal salary

) {
}