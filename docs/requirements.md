# Salary Management — Requirements

## Goal
Build a web-based salary management application for ACME's HR Manager to replace spreadsheet-based salary administration for an organization with 10,000 employees.

## User Persona
**Primary user:** HR Manager

## In Scope
- Employee listing, search, department filtering, pagination and sorting
- Individual salary adjustment and absolute salary update
- Bulk salary adjustment
- Average, minimum and maximum salary analytics
- Employee create, edit and delete
- CSV export
- Validation, error handling, loading and success feedback
- Audit logging for salary-related changes

## Deliberately Out of Scope
Authentication/authorization, payroll processing, tax calculation, payslip generation, attendance/leave, performance management, employee self-service, compensation approval workflows and notifications.

These are excluded to keep the product focused on salary management and compensation insights rather than building a broader HR/payroll platform.

## Product Decisions
1. Server-side pagination/search/filter/sort because the target dataset contains 10,000 employees.
2. Separate delta adjustment and absolute salary update because they represent different HR workflows.
3. A bulk adjustment endpoint avoids one HTTP request per selected employee.
4. A modular REST application is sufficient; distributed services would add complexity without a requirement for independent scaling.
5. Confirmation and UI feedback reduce accidental compensation changes.
