# Architecture & Design

```text
HR Manager
    |
    v
Angular UI
    | REST/HTTP
    v
Spring Boot API
    |
    +--> EmployeeController
    +--> EmployeeService
    +--> AuditLogService
    |
    v
Spring Data JPA
    |
    +--> EmployeeRepository
    +--> AuditLogRepository
    |
    v
Relational Database
```

## Backend
The backend uses a layered Spring Boot structure:
- Controller: HTTP/API contract and validation
- Service: business operations
- Repository: persistence and database queries
- DTOs: request boundaries
- Exception handling: centralized API errors
- Audit service: records important salary-management actions

## API
```text
GET    /api/employees
GET    /api/employees/{id}
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}
POST   /api/employees/{id}/adjust
POST   /api/employees/{id}/update
POST   /api/employees/bulk-adjust
GET    /api/employees/report/average-salary
GET    /api/employees/report/min-salary
GET    /api/employees/report/max-salary
```

The employee listing API accepts search, department, page, size, sort field and direction so the database performs dataset operations rather than the browser.

## Frontend
Angular provides search/filter controls, server-side pagination, sorting, salary operations, bulk selection, CRUD modals, export and user feedback.

## Salary Adjustment Flow
```text
Angular EmployeeService
        |
        v
EmployeeController
        |
        v
EmployeeService
   |          |
   v          v
Employee    AuditLog
   |
   v
Database
```
