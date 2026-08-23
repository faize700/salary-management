package com.acme.salary_management.controller;

import com.acme.salary_management.dto.BulkSalaryAdjustmentRequest;
import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.dto.SalaryAdjustmentRequest;
import com.acme.salary_management.dto.SalaryUpdateRequest;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /*
     * Employee listing with:
     * - pagination
     * - search
     * - department filtering
     * - sorting
     */
    @GetMapping
    public Page<Employee> list(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            String dept,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction

    ) {

        int safePage = Math.max(page, 0);

        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        String safeSort = switch (sortBy) {
            case "id", "name", "department", "country", "salary"
                    -> sortBy;
            default -> "id";
        };

        Sort.Direction sortDirection =
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable =
                PageRequest.of(
                        safePage,
                        safeSize,
                        Sort.by(sortDirection, safeSort)
                );

        return service.searchEmployees(
                search,
                dept,
                pageable
        );
    }


    @GetMapping("/{id}")
    public Employee getEmployee(
            @PathVariable Long id
    ) {
        return service.findById(id);
    }


    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @Valid @RequestBody EmployeeRequest request
    ) {

        return ResponseEntity.ok(
                service.create(request)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request
    ) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/adjust")
    public ResponseEntity<Employee> adjustSalary(
            @PathVariable Long id,
            @Valid @RequestBody SalaryAdjustmentRequest request
    ) {

        return ResponseEntity.ok(
                service.adjustSalary(
                        id,
                        request.salary()
                )
        );
    }


    @PostMapping("/{id}/update")
    public ResponseEntity<Employee> updateSalary(
            @PathVariable Long id,
            @Valid @RequestBody SalaryUpdateRequest request
    ) {

        return ResponseEntity.ok(
                service.updateSalary(
                        id,
                        request.salary()
                )
        );
    }


    @PostMapping("/bulk-adjust")
    public ResponseEntity<Map<String, Object>> bulkAdjustSalary(
            @Valid @RequestBody BulkSalaryAdjustmentRequest request
    ) {

        int updated =
                service.bulkAdjustSalary(
                        request.employeeIds(),
                        request.adjustment()
                );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Salary adjustment completed successfully",

                        "updatedEmployees",
                        updated
                )
        );
    }


    @GetMapping("/report/average-salary")
    public BigDecimal averageSalary(
            @RequestParam(required = false)
            String dept
    ) {

        return service.getAverageSalary(dept);
    }


    @GetMapping("/report/min-salary")
    public BigDecimal minSalary(
            @RequestParam(required = false)
            String dept
    ) {

        return service.getMinSalary(dept);
    }


    @GetMapping("/report/max-salary")
    public BigDecimal maxSalary(
            @RequestParam(required = false)
            String dept
    ) {

        return service.getMaxSalary(dept);
    }
}