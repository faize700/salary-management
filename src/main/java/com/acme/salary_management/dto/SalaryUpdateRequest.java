package com.acme.salary_management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SalaryUpdateRequest(

        @NotNull(message = "New salary is required")
        @DecimalMin(
                value = "0.01",
                message = "Salary must be greater than zero"
        )
        BigDecimal salary

) {
}