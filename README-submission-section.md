# Assessment Submission

## Product

Salary Management for an HR Manager managing compensation data for 10,000 employees.

## Features

- Employee CRUD
- Server-side pagination, search, filtering and sorting
- Individual and bulk salary adjustments
- Salary analytics
- CSV export
- Validation and error handling
- Audit logging
- Automated tests

## Engineering Artifacts

The `docs/` directory contains:

- Requirements and product scope
- Architecture and design decisions
- AI-assisted development workflow
- Engineering trade-offs
- Performance considerations
- Demo script
- Submission checklist

## Deployment

**Live Application:**  
https://salary-management-ui-ue95.onrender.com

**Backend API:**  
https://salary-management-backend-2pgq.onrender.com

**Employees API:**  
https://salary-management-backend-2pgq.onrender.com/api/employees?page=0&size=10

The deployed dataset contains 10,000 employees.

## Demo Video

[Add the final demo video link here after recording.]

## Repository

https://github.com/faize700/salary-management

## Technical Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Angular 18
- TypeScript
- Maven
- npm
- Docker
- Render

## Key Engineering Decisions

The application uses server-side pagination, search, filtering and sorting so that the browser does not need to load all 10,000 employees.

Salary analytics are calculated through backend/database operations rather than downloading the complete dataset to the browser.

Bulk salary adjustment is exposed through a dedicated API to avoid issuing one HTTP request per selected employee.

The backend is structured using controller, service and repository layers to keep HTTP concerns, business logic and persistence concerns separated.

The solution deliberately avoids unnecessary distributed services because the assessment does not require independent service scaling.

## AI-Assisted Development

AI tools were used throughout development for implementation acceleration, debugging, test generation, UI refinement and documentation.

AI-generated suggestions were reviewed and validated through compilation, automated tests, API verification and manual browser testing.

The repository contains `docs/ai-development.md` describing the development workflow and verification approach.