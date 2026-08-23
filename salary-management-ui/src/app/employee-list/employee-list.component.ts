import { Component, OnInit } from '@angular/core';
import { EmployeeService, Employee } from '../services/employee.service';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  adjustment: number = 0;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(data => this.employees = data);
  }

  adjustSalary(id: number): void {
    this.employeeService.adjustSalary(id, this.adjustment).subscribe(() => {
      this.loadEmployees();
      this.adjustment = 0;
    });
  }
}
