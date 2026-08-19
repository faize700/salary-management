package com.acme.salary_management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private String country;
    private BigDecimal salary;

    // getters and setters
}
