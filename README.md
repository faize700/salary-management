# Employee Salary Management System

A full-stack Employee Salary Management System built with **Spring Boot (Java)** for the backend and **Angular** for the frontend.

## Features

- Manage employees with ID, name, department, country, and salary
- Server-side pagination for 10,000 employees
- Employee search, department filtering, and sorting
- Individual salary adjustment and absolute salary updates
- Bulk salary adjustments
- Salary analytics: average, minimum, and maximum salary
- Employee create, edit, and delete
- CSV export
- Backend REST API with validation and CORS support
- Audit logging for important salary-management actions
- Seeded dataset of 10,000 employees


## Tech Stack

* **Backend:** Spring Boot, JPA, H2/PostgreSQL
* **Frontend:** Angular 18 (Standalone Components)
* **Languages:** Java, TypeScript, HTML, SCSS
* **Build & Package Management:** Maven, npm
* **Tools:** Git, VS Code

## Running the Project

### Backend

```bash
cd salary-management
mvn spring-boot:run
```

The backend runs at:

`http://localhost:8081/api/employees`

### Frontend

```bash
cd salary-management-ui
npm install
ng serve
```

The frontend runs at:

`http://localhost:4200`

## Repository Structure

```text
salary-management/
├── src/                         # Spring Boot backend
├── salary-management-ui/        # Angular frontend
├── docs/                        # Assessment engineering artifacts
├── README.md                    # Project documentation
├── README-submission-section.md # Assessment submission details
├── seed.py                      # Employee dataset generator
├── Dockerfile                   # Docker File
└── pom.xml                      # Maven configuration
```

## API

The backend exposes REST APIs for managing employee records.

**Base URL:**

```text
http://localhost:8081/api/employees
```

## Notes

* The backend can be configured to use either **H2** for local development or **PostgreSQL** for persistent storage.
* The Angular frontend communicates with the Spring Boot REST API.
* `seed.py` can be used to generate a large dataset for testing and performance evaluation.


## Live Demo

**Application:** https://salary-management-ui-ue95.onrender.com

**Backend API:** https://salary-management-backend-2pgq.onrender.com

**Repository:** https://github.com/faize700/salary-management

The application is deployed with an Angular frontend, Spring Boot backend and PostgreSQL database, with a seeded dataset of 10,000 employees.

## Assessment Documentation

Engineering artifacts for the Incubyte assessment are available in:

- `README-submission-section.md`
- `docs/requirements.md`
- `docs/architecture.md`
- `docs/ai-development.md`
- `docs/trade-offs.md`
- `docs/performance.md`
- `docs/demo-script.md`
- `docs/submission-checklist.md`