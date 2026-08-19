package com.acme.salary_management.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; 

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{id}/adjust")
    public void adjustSalary(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal adjustment = payload.get("salary");
        service.adjustSalary(id, adjustment);
    }

}
