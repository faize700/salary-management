import { Component, OnInit } from '@angular/core';
import { EmployeeService, Employee } from '../services/employee.service';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, CurrencyPipe } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, CurrencyPipe, MatPaginatorModule],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];

  constructor(private employeeService: EmployeeService) {}

  pageSize: number = 10;   // employees per page
  pageIndex: number = 0;   // current page
  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(data => {
      this.employees = data.map(e => ({
        ...e,
        adjustment: 0,       // initialize delta
        newSalary: e.salary  // initialize absolute
      }));
    });
  }
  // Slice employees for current page
  get paginatedEmployees(): Employee[] {
    const start = this.pageIndex * this.pageSize;
    return this.employees.slice(start, start + this.pageSize);
  }

  onPageChange(event: any): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  adjustSalary(id: number, adjustment: number): void {
    this.employeeService.adjustSalary(id, adjustment).subscribe(() => {
      this.loadEmployees();
    });
  }

  updateSalary(id: number, newSalary: number): void {
    this.employeeService.updateSalary(id, newSalary).subscribe(() => {
      this.loadEmployees();
    });
  }

  averageSalary: number = 0;
  minSalary: number = 0;
  maxSalary: number = 0;

  selectedDept: string = '';

  loadReports(): void {
    this.employeeService.getAverageSalary(this.selectedDept).subscribe(val => this.averageSalary = val);
    this.employeeService.getMinSalary(this.selectedDept).subscribe(val => this.minSalary = val);
    this.employeeService.getMaxSalary(this.selectedDept).subscribe(val => this.maxSalary = val);
  }

  ngOnInit(): void {
    this.loadEmployees();
    this.loadReports();
  }
  searchTerm: string = '';

  get filteredEmployees(): Employee[] {
    if (!this.searchTerm) {
      return this.paginatedEmployees;
    }
    const term = this.searchTerm.toLowerCase();
    return this.paginatedEmployees.filter(e =>
      e.name.toLowerCase().includes(term) ||
      e.department.toLowerCase().includes(term) ||
      e.country.toLowerCase().includes(term)
    );
  }

}
