package com.acme.salary_management.repository;

import com.acme.salary_management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT e
        FROM Employee e
        WHERE
            (:dept IS NULL OR :dept = '' OR e.department = :dept)
        AND
            (
                :search IS NULL
                OR :search = ''
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.department) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.country) LIKE LOWER(CONCAT('%', :search, '%'))
            )
        """)
    Page<Employee> searchEmployees(
            @Param("search") String search,
            @Param("dept") String dept,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(AVG(e.salary), 0)
        FROM Employee e
        WHERE (:dept IS NULL OR :dept = '' OR e.department = :dept)
        """)
    BigDecimal findAverageSalary(@Param("dept") String dept);

    @Query("""
        SELECT COALESCE(MIN(e.salary), 0)
        FROM Employee e
        WHERE (:dept IS NULL OR :dept = '' OR e.department = :dept)
        """)
    BigDecimal findMinSalary(@Param("dept") String dept);

    @Query("""
        SELECT COALESCE(MAX(e.salary), 0)
        FROM Employee e
        WHERE (:dept IS NULL OR :dept = '' OR e.department = :dept)
        """)
    BigDecimal findMaxSalary(@Param("dept") String dept);
}