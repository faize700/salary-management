package com.acme.salary_management.service;

import com.acme.salary_management.entity.AuditLog;
import com.acme.salary_management.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(
            Long employeeId,
            String action,
            BigDecimal oldSalary,
            BigDecimal newSalary,
            BigDecimal adjustment
    ) {
        AuditLog auditLog = new AuditLog();

        auditLog.setEmployeeId(employeeId);
        auditLog.setAction(action);
        auditLog.setOldSalary(oldSalary);
        auditLog.setNewSalary(newSalary);
        auditLog.setAdjustment(adjustment);

        // Authentication will replace this with the actual logged-in user.
        auditLog.setPerformedBy("HR Admin");

        auditLog.setTimestamp(LocalDateTime.now());

        repository.save(auditLog);
    }
}