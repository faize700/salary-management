package com.acme.salary_management.service;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<Employee> findByDepartment(String dept) {
        return dept == null ? repo.findAll() : repo.findByDepartment(dept);
    }

    // Delta adjustment
    public void adjustSalary(Long id, BigDecimal adjustment) {
        Employee emp = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setSalary(emp.getSalary().add(adjustment));
        repo.save(emp);
    }

    // Absolute update
    public void updateSalary(Long id, BigDecimal newSalary) {
        Employee emp = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setSalary(newSalary);
        repo.save(emp);
    }
        public BigDecimal getAverageSalary(String dept) {
        List<Employee> employees = findByDepartment(dept);
        return employees.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(employees.size()), RoundingMode.HALF_UP);
    }

    public BigDecimal getMinSalary(String dept) {
        return findByDepartment(dept).stream()
                .map(Employee::getSalary)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMaxSalary(String dept) {
        return findByDepartment(dept).stream()
                .map(Employee::getSalary)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
