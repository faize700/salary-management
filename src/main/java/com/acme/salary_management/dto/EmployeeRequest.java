package com.acme.salary_management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EmployeeRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Department is required")
        String department,

        @NotBlank(message = "Country is required")
        String country,

        @NotNull(message = "Salary is required")
        @DecimalMin(
                value = "0.01",
                message = "Salary must be greater than zero"
        )
        BigDecimal salary

) {
}