import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Employee {
  id: number;
  name: string;
  department: string;
  country: string;
  salary: number;

  // UI-only fields
  adjustment: number;
  newSalary: number;
}

export interface EmployeeRequest {
  name: string;
  department: string;
  country: string;
  salary: number;
}

export interface EmployeePage {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly apiUrl =
    'https://salary-management-backend-2pgq.onrender.com/api/employees';
    // 'http://localhost:8081/api/employees';

  constructor(private http: HttpClient) {}

  /**
   * Server-side employee search,
   * filtering, pagination and sorting.
   */
  getEmployees(
    page: number = 0,
    size: number = 10,
    search: string = '',
    dept: string = '',
    sortColumn: string = 'id',
    sortDirection: string = 'asc'
  ): Observable<EmployeePage> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', `${sortColumn},${sortDirection}`);

    if (search.trim()) {
      params = params.set('search', search.trim());
    }

    if (dept.trim()) {
      params = params.set('dept', dept.trim());
    }

    return this.http.get<EmployeePage>(
      this.apiUrl,
      { params }
    );
  }

  /**
   * Get employee by ID.
   */
  getEmployee(id: number): Observable<Employee> {
    return this.http.get<Employee>(
      `${this.apiUrl}/${id}`
    );
  }

  /**
   * Create employee.
   */
  createEmployee(
    employee: EmployeeRequest
  ): Observable<Employee> {

    return this.http.post<Employee>(
      this.apiUrl,
      employee
    );
  }

  /**
   * Update employee.
   */
  updateEmployee(
    id: number,
    employee: EmployeeRequest
  ): Observable<Employee> {

    return this.http.put<Employee>(
      `${this.apiUrl}/${id}`,
      employee
    );
  }

  /**
   * Delete employee.
   */
  deleteEmployee(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  /**
   * Adjust salary by a delta amount.
   */
  adjustSalary(
    id: number,
    adjustment: number
  ): Observable<Employee> {

    return this.http.post<Employee>(
      `${this.apiUrl}/${id}/adjust`,
      {
        salary: adjustment
      }
    );
  }

  /**
   * Set salary to an absolute value.
   */
  updateSalary(
    id: number,
    newSalary: number
  ): Observable<Employee> {

    return this.http.post<Employee>(
      `${this.apiUrl}/${id}/update`,
      {
        salary: newSalary
      }
    );
  }

  /**
   * Salary analytics.
   */
  getAverageSalary(
    dept?: string
  ): Observable<number> {

    let params = new HttpParams();

    if (dept?.trim()) {
      params = params.set('dept', dept.trim());
    }

    return this.http.get<number>(
      `${this.apiUrl}/report/average-salary`,
      { params }
    );
  }

  getMinSalary(
    dept?: string
  ): Observable<number> {

    let params = new HttpParams();

    if (dept?.trim()) {
      params = params.set('dept', dept.trim());
    }

    return this.http.get<number>(
      `${this.apiUrl}/report/min-salary`,
      { params }
    );
  }

  getMaxSalary(
    dept?: string
  ): Observable<number> {

    let params = new HttpParams();

    if (dept?.trim()) {
      params = params.set('dept', dept.trim());
    }

    return this.http.get<number>(
      `${this.apiUrl}/report/max-salary`,
      { params }
    );
  }

  /**
   * Bulk salary adjustment.
   */
  bulkAdjustSalary(
    employeeIds: number[],
    adjustment: number
  ): Observable<{
    message: string;
    updatedEmployees: number;
  }> {

    return this.http.post<{
      message: string;
      updatedEmployees: number;
    }>(
      `${this.apiUrl}/bulk-adjust`,
      {
        employeeIds,
        adjustment
      }
    );
  }
}