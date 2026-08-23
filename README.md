# Employee Salary Management System

A full-stack Employee Salary Management System built with **Spring Boot (Java)** for the backend and **Angular** for the frontend.

## Features

* Manage employees with ID, name, department, country, and salary
* Adjust employee salaries interactively from the UI
* Backend REST API with CORS enabled
* Seed script for generating large employee datasets

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
├── src/                    # Spring Boot backend
│
├── salary-management-ui/  # Angular frontend
│
├── seed.py                # Employee dataset generator
│
└── requirements.md        # Assessment requirements
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
