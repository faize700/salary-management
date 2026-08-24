# Engineering Trade-offs

## Server-side vs Client-side Operations
**Decision:** Server-side pagination, search, filtering and sorting.

**Reason:** The application is designed for 10,000 employees. Loading the full dataset into the browser would increase payload size and client-side work.

## REST vs Distributed Services
**Decision:** Modular Spring Boot application.

**Reason:** The assessment does not require independently deployed services. A modular application is simpler to test, deploy and understand.

## Bulk Salary Adjustment
**Decision:** Dedicated bulk endpoint.

**Reason:** One request for a collection of IDs is more efficient and easier to reason about than one HTTP request per employee.

## Delta vs Absolute Salary Update
Two explicit operations are provided:
- Adjust: add/subtract a delta from current salary.
- Update: replace current salary with an absolute value.

## Scope Control
Authentication, payroll, tax, payslips, attendance and approval workflows were intentionally excluded to keep the product focused on the stated problem.

## Page Size Protection
The backend constrains page size rather than trusting arbitrary client input, preventing unnecessarily large responses.
