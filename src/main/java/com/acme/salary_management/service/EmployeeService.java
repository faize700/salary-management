package com.acme.salary_management.service;

import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.exception.EmployeeNotFoundException;
import com.acme.salary_management.repository.EmployeeRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;
    private final AuditLogService auditLogService;

    public EmployeeService(
            EmployeeRepository repo,
            AuditLogService auditLogService
    ) {
        this.repo = repo;
        this.auditLogService = auditLogService;
    }

    /**
     * Searches employees with optional:
     * - search text
     * - department filter
     * - pagination
     * - sorting
     */
    public Page<Employee> searchEmployees(
            String search,
            String dept,
            Pageable pageable
    ) {
        return repo.searchEmployees(
                normalize(search),
                normalize(dept),
                pageable
        );
    }

    /**
     * Retrieves a single employee.
     */
    public Employee findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee with ID " + id + " not found"
                        )
                );
    }

    /**
     * Creates a new employee.
     */
    @Transactional
    public Employee create(EmployeeRequest request) {

        Employee employee = new Employee();

        employee.setName(request.name().trim());
        employee.setDepartment(request.department().trim());
        employee.setCountry(request.country().trim());
        employee.setSalary(request.salary());

        Employee saved = repo.save(employee);

        auditLogService.log(
                saved.getId(),
                "CREATE",
                null,
                saved.getSalary(),
                null
        );

        return saved;
    }

    /**
     * Updates employee details and salary.
     */
    @Transactional
    public Employee update(
            Long id,
            EmployeeRequest request
    ) {

        Employee employee = findById(id);

        BigDecimal oldSalary = employee.getSalary();

        employee.setName(request.name().trim());
        employee.setDepartment(request.department().trim());
        employee.setCountry(request.country().trim());
        employee.setSalary(request.salary());

        Employee saved = repo.save(employee);

        auditLogService.log(
                id,
                "UPDATE",
                oldSalary,
                saved.getSalary(),
                null
        );

        return saved;
    }

    /**
     * Deletes an employee.
     */
    @Transactional
    public void delete(Long id) {

        Employee employee = findById(id);

        BigDecimal oldSalary = employee.getSalary();

        repo.delete(employee);

        auditLogService.log(
                id,
                "DELETE",
                oldSalary,
                null,
                null
        );
    }

    /**
     * Adjusts salary by a delta value.
     *
     * Example:
     * Current salary = 5000
     * Adjustment = +500
     * New salary = 5500
     */
    @Transactional
    public Employee adjustSalary(
            Long id,
            BigDecimal adjustment
    ) {

        Employee employee = findById(id);

        if (adjustment == null) {
            throw new IllegalArgumentException(
                    "Salary adjustment is required"
            );
        }

        BigDecimal oldSalary = employee.getSalary();

        BigDecimal newSalary =
                oldSalary.add(adjustment);

        if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Resulting salary must be greater than zero"
            );
        }

        employee.setSalary(newSalary);

        Employee saved = repo.save(employee);

        auditLogService.log(
                id,
                "SALARY_ADJUSTMENT",
                oldSalary,
                newSalary,
                adjustment
        );

        return saved;
    }

    /**
     * Sets salary to an absolute value.
     *
     * Example:
     * Current salary = 5000
     * New salary = 6500
     */
    @Transactional
    public Employee updateSalary(
            Long id,
            BigDecimal newSalary
    ) {

        Employee employee = findById(id);

        if (newSalary == null ||
                newSalary.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Salary must be greater than zero"
            );
        }

        BigDecimal oldSalary = employee.getSalary();

        employee.setSalary(newSalary);

        Employee saved = repo.save(employee);

        auditLogService.log(
                id,
                "SALARY_UPDATE",
                oldSalary,
                newSalary,
                null
        );

        return saved;
    }

    /**
     * Applies the same salary adjustment to multiple employees.
     *
     * The complete operation is transactional:
     * if validation fails for any employee,
     * the transaction is rolled back.
     */
    @Transactional
    public int bulkAdjustSalary(
            List<Long> employeeIds,
            BigDecimal adjustment
    ) {

        if (employeeIds == null || employeeIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one employee must be selected"
            );
        }

        if (adjustment == null) {
            throw new IllegalArgumentException(
                    "Salary adjustment is required"
            );
        }

        List<Employee> employees =
                repo.findAllById(employeeIds);

        /*
         * findAllById() silently ignores IDs that don't exist,
         * so explicitly verify that every requested ID was found.
         */
        if (employees.size() != employeeIds.size()) {

            List<Long> foundIds = employees.stream()
                    .map(Employee::getId)
                    .toList();

            List<Long> missingIds = employeeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .distinct()
                    .toList();

            throw new EmployeeNotFoundException(
                    "Employee(s) not found: " + missingIds
            );
        }

        /*
         * Validate the complete operation before changing
         * any employee salary.
         */
        for (Employee employee : employees) {

            BigDecimal newSalary =
                    employee.getSalary()
                            .add(adjustment);

            if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Bulk adjustment would create a non-positive salary " +
                        "for employee ID " + employee.getId()
                );
            }
        }

        /*
         * Apply the changes and create audit records.
         */
        for (Employee employee : employees) {

            BigDecimal oldSalary =
                    employee.getSalary();

            BigDecimal newSalary =
                    oldSalary.add(adjustment);

            employee.setSalary(newSalary);

            auditLogService.log(
                    employee.getId(),
                    "BULK_SALARY_ADJUSTMENT",
                    oldSalary,
                    newSalary,
                    adjustment
            );
        }

        repo.saveAll(employees);

        return employees.size();
    }

    /**
     * Salary analytics.
     *
     * Calculations are executed at database level
     * rather than loading all 10,000 employees into memory.
     */
    public BigDecimal getAverageSalary(String dept) {
        return repo.findAverageSalary(
                normalize(dept)
        );
    }

    public BigDecimal getMinSalary(String dept) {
        return repo.findMinSalary(
                normalize(dept)
        );
    }

    public BigDecimal getMaxSalary(String dept) {
        return repo.findMaxSalary(
                normalize(dept)
        );
    }

    /**
     * Converts blank request parameters to null.
     */
    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}