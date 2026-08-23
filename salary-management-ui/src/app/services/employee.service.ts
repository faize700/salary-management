import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Employee {
  id: number;
  name: string;
  department: string;
  country: string;
  salary: number;
  adjustment: number;   // delta adjustment
  newSalary: number;    // absolute update
}

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private apiUrl = 'http://localhost:8081/api/employees';

  constructor(private http: HttpClient) {}

  getEmployees(dept?: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}${dept ? '?dept=' + dept : ''}`);
  }

  adjustSalary(id: number, adjustment: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/adjust`, { salary: adjustment });
  }

  updateSalary(id: number, newSalary: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/update`, { salary: newSalary });
  }
}
