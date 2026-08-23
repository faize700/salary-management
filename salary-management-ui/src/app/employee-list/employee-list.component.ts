import { Component, OnInit } from '@angular/core';
import { EmployeeService, Employee } from '../services/employee.service';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, CurrencyPipe],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(data => {
      this.employees = data.map(e => ({
        ...e,
        adjustment: 0,       // initialize delta
        newSalary: e.salary  // initialize absolute
      }));
    });
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
}
