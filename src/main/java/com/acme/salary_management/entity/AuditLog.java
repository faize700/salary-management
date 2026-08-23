package com.acme.salary_management.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "audit_log",
    indexes = {
        @Index(name = "idx_audit_employee", columnList = "employeeId"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
    }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(precision = 12, scale = 2)
    private BigDecimal oldSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal newSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal adjustment;

    @Column(nullable = false, length = 100)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getOldSalary() {
        return oldSalary;
    }

    public void setOldSalary(BigDecimal oldSalary) {
        this.oldSalary = oldSalary;
    }

    public BigDecimal getNewSalary() {
        return newSalary;
    }

    public void setNewSalary(BigDecimal newSalary) {
        this.newSalary = newSalary;
    }

    public BigDecimal getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(BigDecimal adjustment) {
        this.adjustment = adjustment;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}