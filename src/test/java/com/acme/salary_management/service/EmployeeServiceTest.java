package com.acme.salary_management.service;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.exception.EmployeeNotFoundException;
import com.acme.salary_management.repository.EmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private EmployeeRepository repo;
    private AuditLogService auditLogService;
    private EmployeeService service;

    @BeforeEach
    void setUp() {

        repo = mock(EmployeeRepository.class);
        auditLogService = mock(AuditLogService.class);

        service = new EmployeeService(
                repo,
                auditLogService
        );
    }


    @Test
    void adjustSalary_updatesSalary() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        when(repo.save(emp))
                .thenReturn(emp);

        service.adjustSalary(
                1L,
                BigDecimal.valueOf(500)
        );

        assertEquals(
                BigDecimal.valueOf(5500),
                emp.getSalary()
        );

        verify(repo).save(emp);

        verify(auditLogService).log(
                1L,
                "SALARY_ADJUSTMENT",
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(5500),
                BigDecimal.valueOf(500)
        );
    }


    @Test
    void adjustSalary_allowsNegativeAdjustmentWhenResultIsPositive() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        when(repo.save(emp))
                .thenReturn(emp);

        service.adjustSalary(
                1L,
                BigDecimal.valueOf(-500)
        );

        assertEquals(
                BigDecimal.valueOf(4500),
                emp.getSalary()
        );

        verify(repo).save(emp);
    }


    @Test
    void adjustSalary_rejectsResultingNonPositiveSalary() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(500)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.adjustSalary(
                        1L,
                        BigDecimal.valueOf(-500)
                )
        );

        assertEquals(
                BigDecimal.valueOf(500),
                emp.getSalary()
        );

        verify(repo, never()).save(any());
    }


    @Test
    void adjustSalary_rejectsNullAdjustment() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.adjustSalary(
                        1L,
                        null
                )
        );

        verify(repo, never()).save(any());
    }


    @Test
    void updateSalary_setsAbsoluteSalary() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        when(repo.save(emp))
                .thenReturn(emp);

        service.updateSalary(
                1L,
                BigDecimal.valueOf(7500)
        );

        assertEquals(
                BigDecimal.valueOf(7500),
                emp.getSalary()
        );

        verify(repo).save(emp);

        verify(auditLogService).log(
                1L,
                "SALARY_UPDATE",
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(7500),
                null
        );
    }


    @Test
    void updateSalary_rejectsZeroSalary() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSalary(
                        1L,
                        BigDecimal.ZERO
                )
        );

        assertEquals(
                BigDecimal.valueOf(5000),
                emp.getSalary()
        );

        verify(repo, never()).save(any());
    }


    @Test
    void updateSalary_rejectsNegativeSalary() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSalary(
                        1L,
                        BigDecimal.valueOf(-100)
                )
        );

        verify(repo, never()).save(any());
    }


    @Test
    void findById_returnsEmployee() {

        Employee emp = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findById(1L))
                .thenReturn(Optional.of(emp));

        Employee result =
                service.findById(1L);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                BigDecimal.valueOf(5000),
                result.getSalary()
        );
    }


    @Test
    void findById_throwsWhenEmployeeDoesNotExist() {

        when(repo.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> service.findById(999L)
        );
    }


    @Test
    void bulkAdjustSalary_updatesAllEmployees() {

        Employee employee1 = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        Employee employee2 = createEmployee(
                2L,
                BigDecimal.valueOf(6000)
        );

        when(repo.findAllById(List.of(1L, 2L)))
                .thenReturn(
                        List.of(employee1, employee2)
                );

        int updated =
                service.bulkAdjustSalary(
                        List.of(1L, 2L),
                        BigDecimal.valueOf(500)
                );

        assertEquals(2, updated);

        assertEquals(
                BigDecimal.valueOf(5500),
                employee1.getSalary()
        );

        assertEquals(
                BigDecimal.valueOf(6500),
                employee2.getSalary()
        );

        verify(repo).saveAll(
                List.of(employee1, employee2)
        );

        verify(auditLogService).log(
                1L,
                "BULK_SALARY_ADJUSTMENT",
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(5500),
                BigDecimal.valueOf(500)
        );

        verify(auditLogService).log(
                2L,
                "BULK_SALARY_ADJUSTMENT",
                BigDecimal.valueOf(6000),
                BigDecimal.valueOf(6500),
                BigDecimal.valueOf(500)
        );
    }


    @Test
    void bulkAdjustSalary_rejectsEmptyEmployeeList() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.bulkAdjustSalary(
                        List.of(),
                        BigDecimal.valueOf(500)
                )
        );

        verify(repo, never()).findAllById(any());
    }


    @Test
    void bulkAdjustSalary_rejectsMissingEmployees() {

        Employee employee1 = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        when(repo.findAllById(List.of(1L, 999L)))
                .thenReturn(List.of(employee1));

        assertThrows(
                EmployeeNotFoundException.class,
                () -> service.bulkAdjustSalary(
                        List.of(1L, 999L),
                        BigDecimal.valueOf(500)
                )
        );

        verify(repo, never()).saveAll(any());
    }


    @Test
    void bulkAdjustSalary_rejectsNonPositiveResult() {

        Employee employee = createEmployee(
                1L,
                BigDecimal.valueOf(300)
        );

        when(repo.findAllById(List.of(1L)))
                .thenReturn(List.of(employee));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.bulkAdjustSalary(
                        List.of(1L),
                        BigDecimal.valueOf(-300)
                )
        );

        assertEquals(
                BigDecimal.valueOf(300),
                employee.getSalary()
        );

        verify(repo, never()).saveAll(any());
    }


    @Test
    void searchEmployees_returnsPage() {

        Employee employee = createEmployee(
                1L,
                BigDecimal.valueOf(5000)
        );

        Page<Employee> page =
                new PageImpl<>(
                        List.of(employee)
                );

        PageRequest pageable =
                PageRequest.of(0, 10);

        when(repo.searchEmployees(
                "Employee",
                "Engineering",
                pageable
        )).thenReturn(page);

        Page<Employee> result =
                service.searchEmployees(
                        "Employee",
                        "Engineering",
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                1L,
                result.getContent()
                        .get(0)
                        .getId()
        );

        verify(repo).searchEmployees(
                "Employee",
                "Engineering",
                pageable
        );
    }


    @Test
    void salaryReports_delegateToRepository() {

        when(repo.findAverageSalary("Engineering"))
                .thenReturn(BigDecimal.valueOf(7500));

        when(repo.findMinSalary("Engineering"))
                .thenReturn(BigDecimal.valueOf(4000));

        when(repo.findMaxSalary("Engineering"))
                .thenReturn(BigDecimal.valueOf(12000));

        assertEquals(
                BigDecimal.valueOf(7500),
                service.getAverageSalary("Engineering")
        );

        assertEquals(
                BigDecimal.valueOf(4000),
                service.getMinSalary("Engineering")
        );

        assertEquals(
                BigDecimal.valueOf(12000),
                service.getMaxSalary("Engineering")
        );

        verify(repo).findAverageSalary("Engineering");
        verify(repo).findMinSalary("Engineering");
        verify(repo).findMaxSalary("Engineering");
    }


    private Employee createEmployee(
            Long id,
            BigDecimal salary
    ) {

        Employee employee = new Employee();

        employee.setId(id);
        employee.setName("Test Employee");
        employee.setDepartment("Engineering");
        employee.setCountry("India");
        employee.setSalary(salary);

        return employee;
    }
}