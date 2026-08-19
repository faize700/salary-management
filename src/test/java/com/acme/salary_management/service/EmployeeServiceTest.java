package com.acme.salary_management.service;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
    @Test
    void adjustSalary_updatesSalary() {
        EmployeeRepository repo = mock(EmployeeRepository.class);
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setSalary(BigDecimal.valueOf(5000));

        when(repo.findById(1L)).thenReturn(Optional.of(emp));

        EmployeeService service = new EmployeeService(repo);
        service.adjustSalary(1L, BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(5500), emp.getSalary());
        verify(repo).save(emp);
    }
}
