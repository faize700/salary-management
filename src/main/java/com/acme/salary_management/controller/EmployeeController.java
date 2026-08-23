package com.acme.salary_management.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> list(@RequestParam(required = false) String dept) {
        return service.findByDepartment(dept);
    }

    // Delta adjustment
    @PostMapping("/{id}/adjust")
    public ResponseEntity<Void> adjustSalary(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal adjustment = payload.get("salary");
        service.adjustSalary(id, adjustment);
        return ResponseEntity.ok().build();
    }

    // Absolute update
    @PostMapping("/{id}/update")
    public ResponseEntity<Void> updateSalary(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal newSalary = payload.get("salary");
        service.updateSalary(id, newSalary);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report/average-salary")
    public BigDecimal averageSalary(@RequestParam(required = false) String dept) {
        return service.getAverageSalary(dept);
    }

    @GetMapping("/report/min-salary")
    public BigDecimal minSalary(@RequestParam(required = false) String dept) {
        return service.getMinSalary(dept);
    }

    @GetMapping("/report/max-salary")
    public BigDecimal maxSalary(@RequestParam(required = false) String dept) {
        return service.getMaxSalary(dept);
    }

}
