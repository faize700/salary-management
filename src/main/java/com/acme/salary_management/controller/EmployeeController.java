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
     * ============================================================
     * EMPLOYEE LIST
     * ============================================================
     *
     * Supports:
     * - Server-side pagination
     * - Server-side search
     * - Department filtering
     * - Server-side sorting
     *
     * Example:
     *
     * /api/employees?page=0&size=10&sort=salary,asc
     *
     * /api/employees?page=0&size=10&sort=name,desc
     *
     * /api/employees?page=1&size=25&sort=department,asc
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
            String sort

    ) {

        /*
         * --------------------------------------------------------
         * Pagination validation
         * --------------------------------------------------------
         */

        int safePage =
                Math.max(page, 0);

        int safeSize =
                Math.min(
                        Math.max(size, 1),
                        100
                );


        /*
         * --------------------------------------------------------
         * Parse sort parameter
         * --------------------------------------------------------
         *
         * Expected format:
         *
         * salary,asc
         * salary,desc
         * name,asc
         *
         * If only "salary" is supplied,
         * ascending order is used.
         */

        String[] sortParts =
                sort.split(",");

        String requestedSort =
                sortParts[0].trim();

        String requestedDirection =
                sortParts.length > 1
                        ? sortParts[1].trim()
                        : "asc";


        /*
         * --------------------------------------------------------
         * Whitelist sortable fields
         * --------------------------------------------------------
         *
         * This prevents arbitrary property names from being
         * passed to Spring Data.
         */

        String safeSort =
                switch (requestedSort) {

                    case "id",
                         "name",
                         "department",
                         "country",
                         "salary"
                            -> requestedSort;

                    default
                            -> "id";
                };


        /*
         * --------------------------------------------------------
         * Sort direction
         * --------------------------------------------------------
         */

        Sort.Direction sortDirection =
                "desc".equalsIgnoreCase(
                        requestedDirection
                )
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;


        /*
         * --------------------------------------------------------
         * Build Pageable
         * --------------------------------------------------------
         */

        Pageable pageable =
                PageRequest.of(
                        safePage,
                        safeSize,
                        Sort.by(
                                sortDirection,
                                safeSort
                        )
                );


        /*
         * --------------------------------------------------------
         * Delegate to service
         * --------------------------------------------------------
         */

        return service.searchEmployees(
                search,
                dept,
                pageable
        );
    }


    /*
     * ============================================================
     * GET EMPLOYEE BY ID
     * ============================================================
     */

    @GetMapping("/{id}")
    public Employee getEmployee(
            @PathVariable Long id
    ) {

        return service.findById(id);
    }


    /*
     * ============================================================
     * CREATE EMPLOYEE
     * ============================================================
     */

    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @Valid
            @RequestBody EmployeeRequest request
    ) {

        return ResponseEntity.ok(
                service.create(request)
        );
    }


    /*
     * ============================================================
     * UPDATE EMPLOYEE
     * ============================================================
     */

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,

            @Valid
            @RequestBody EmployeeRequest request
    ) {

        return ResponseEntity.ok(
                service.update(
                        id,
                        request
                )
        );
    }


    /*
     * ============================================================
     * DELETE EMPLOYEE
     * ============================================================
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }


    /*
     * ============================================================
     * SALARY DELTA ADJUSTMENT
     * ============================================================
     *
     * Example:
     *
     * Current salary = 5000
     * Adjustment = +500
     * Result = 5500
     */

    @PostMapping("/{id}/adjust")
    public ResponseEntity<Employee> adjustSalary(
            @PathVariable Long id,

            @Valid
            @RequestBody SalaryAdjustmentRequest request
    ) {

        return ResponseEntity.ok(
                service.adjustSalary(
                        id,
                        request.salary()
                )
        );
    }


    /*
     * ============================================================
     * ABSOLUTE SALARY UPDATE
     * ============================================================
     *
     * Example:
     *
     * Current salary = 5000
     * New salary = 7000
     * Result = 7000
     */

    @PostMapping("/{id}/update")
    public ResponseEntity<Employee> updateSalary(
            @PathVariable Long id,

            @Valid
            @RequestBody SalaryUpdateRequest request
    ) {

        return ResponseEntity.ok(
                service.updateSalary(
                        id,
                        request.salary()
                )
        );
    }


    /*
     * ============================================================
     * BULK SALARY ADJUSTMENT
     * ============================================================
     */

    @PostMapping("/bulk-adjust")
    public ResponseEntity<Map<String, Object>> bulkAdjustSalary(
            @Valid
            @RequestBody BulkSalaryAdjustmentRequest request
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


    /*
     * ============================================================
     * SALARY ANALYTICS
     * ============================================================
     */

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