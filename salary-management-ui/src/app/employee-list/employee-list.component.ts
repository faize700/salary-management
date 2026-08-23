import {
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import {
  EmployeeService,
  Employee
} from '../services/employee.service';

import { FormsModule } from '@angular/forms';

import {
  NgFor,
  NgIf,
  NgClass,
  CurrencyPipe,
  DecimalPipe
} from '@angular/common';

import {
  MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';

import {
  Subject,
  takeUntil,
  debounceTime,
  distinctUntilChanged
} from 'rxjs';

@Component({
  selector: 'app-employee-list',
  standalone: true,

  imports: [
  FormsModule,
  NgFor,
  NgIf,
  NgClass,
  CurrencyPipe,
  DecimalPipe,
  MatPaginatorModule
],

  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent
  implements OnInit, OnDestroy {

  employees: Employee[] = [];

  // Pagination
  pageSize = 10;
  pageIndex = 0;

  totalEmployees = 0;
  totalPages = 0;

  // Search/filter
  searchTerm = '';
  selectedDept = '';

  // Sorting
  sortColumn = 'id';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Analytics
  averageSalary = 0;
  minSalary = 0;
  maxSalary = 0;

  // UI state
  isLoading = false;
  isUpdating = false;
  errorMessage = '';
  successMessage = '';

  private readonly destroy$ =
    new Subject<void>();

  private readonly searchSubject =
    new Subject<string>();

  constructor(
    private employeeService: EmployeeService
  ) {}

  ngOnInit(): void {

    this.searchSubject
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(search => {

        this.searchTerm = search;
        this.pageIndex = 0;

        this.loadEmployees();
      });

    this.loadEmployees();
    this.loadReports();
  }

  /**
   * Load employees using server-side
   * pagination, filtering and sorting.
   */
  loadEmployees(): void {

    this.isLoading = true;
    this.errorMessage = '';

    this.employeeService
      .getEmployees(
        this.pageIndex,
        this.pageSize,
        this.searchTerm,
        this.selectedDept,
        this.sortColumn,
        this.sortDirection
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: response => {

          this.employees =
            response.content.map(employee => ({
              ...employee,

              adjustment: 0,

              newSalary: employee.salary
            }));

          this.totalEmployees =
            response.totalElements;

          this.totalPages =
            response.totalPages;

          this.isLoading = false;
        },

        error: error => {

          console.error(
            'Failed to load employees',
            error
          );

          this.errorMessage =
            this.getErrorMessage(
              error,
              'Unable to load employees.'
            );

          this.isLoading = false;
        }
      });
  }

  /**
   * Search employees.
   *
   * Debounced so we don't call the backend
   * for every keystroke.
   */
  onSearchChange(value: string): void {

    this.searchSubject.next(value);
  }

  /**
   * Department filter.
   */
  onDepartmentChange(): void {

    this.pageIndex = 0;

    this.loadEmployees();
    this.loadReports();
  }

  /**
   * Server-side pagination.
   */
  onPageChange(event: PageEvent): void {

    this.pageIndex =
      event.pageIndex;

    this.pageSize =
      event.pageSize;

    this.loadEmployees();
  }

  /**
   * Server-side sorting.
   */
  setSort(
    column: string
  ): void {

    if (this.sortColumn === column) {

      this.sortDirection =
        this.sortDirection === 'asc'
          ? 'desc'
          : 'asc';

    } else {

      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.pageIndex = 0;

    this.loadEmployees();
  }

  /**
   * Salary delta adjustment.
   */
  adjustSalary(
    id: number,
    adjustment: number
  ): void {

    if (
      adjustment === null ||
      adjustment === undefined ||
      Number.isNaN(Number(adjustment)) ||
      Number(adjustment) === 0
    ) {

      this.showError(
        'Please enter a valid adjustment amount.'
      );

      return;
    }

    const confirmed =
      window.confirm(
        `Apply a salary adjustment of ${this.formatAmount(adjustment)}?`
      );

    if (!confirmed) {
      return;
    }

    this.isUpdating = true;
    this.clearMessages();

    this.employeeService
      .adjustSalary(
        id,
        Number(adjustment)
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: () => {

          this.isUpdating = false;

          this.showSuccess(
            'Salary adjusted successfully.'
          );

          this.loadEmployees();
          this.loadReports();
        },

        error: error => {

          this.isUpdating = false;

          this.showError(
            this.getErrorMessage(
              error,
              'Unable to adjust salary.'
            )
          );
        }
      });
  }

  /**
   * Absolute salary update.
   */
  updateSalary(
    id: number,
    newSalary: number
  ): void {

    if (
      newSalary === null ||
      newSalary === undefined ||
      Number.isNaN(Number(newSalary)) ||
      Number(newSalary) <= 0
    ) {

      this.showError(
        'Salary must be greater than zero.'
      );

      return;
    }

    const confirmed =
      window.confirm(
        `Set salary to ${this.formatAmount(newSalary)}?`
      );

    if (!confirmed) {
      return;
    }

    this.isUpdating = true;
    this.clearMessages();

    this.employeeService
      .updateSalary(
        id,
        Number(newSalary)
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: () => {

          this.isUpdating = false;

          this.showSuccess(
            'Salary updated successfully.'
          );

          this.loadEmployees();
          this.loadReports();
        },

        error: error => {

          this.isUpdating = false;

          this.showError(
            this.getErrorMessage(
              error,
              'Unable to update salary.'
            )
          );
        }
      });
  }

  /**
   * Load salary analytics.
   */
  loadReports(): void {

    this.employeeService
      .getAverageSalary(
        this.selectedDept
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: value =>
          this.averageSalary = value ?? 0,

        error: () =>
          this.averageSalary = 0
      });

    this.employeeService
      .getMinSalary(
        this.selectedDept
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: value =>
          this.minSalary = value ?? 0,

        error: () =>
          this.minSalary = 0
      });

    this.employeeService
      .getMaxSalary(
        this.selectedDept
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: value =>
          this.maxSalary = value ?? 0,

        error: () =>
          this.maxSalary = 0
      });
  }

  /**
   * Export currently loaded records.
   *
   * The exported data respects the current
   * search/filter/sort/page displayed in the UI.
   */
  exportToCSV(): void {

    if (!this.employees.length) {

      this.showError(
        'There are no employees to export.'
      );

      return;
    }

    const headers = [
      'ID',
      'Name',
      'Department',
      'Country',
      'Salary'
    ];

    const rows =
      this.employees.map(employee => [
        employee.id,
        this.escapeCsv(employee.name),
        this.escapeCsv(employee.department),
        this.escapeCsv(employee.country),
        employee.salary
      ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row =>
        row.join(',')
      )
    ].join('\n');

    const blob =
      new Blob(
        [csvContent],
        {
          type:
            'text/csv;charset=utf-8;'
        }
      );

    const url =
      URL.createObjectURL(blob);

    const link =
      document.createElement('a');

    link.href = url;

    link.download =
      'employee-salaries.csv';

    document.body.appendChild(link);

    link.click();

    document.body.removeChild(link);

    URL.revokeObjectURL(url);

    this.showSuccess(
      'CSV exported successfully.'
    );
  }

  /**
   * The HTML already expects sortedEmployees.
   * Since sorting now happens on the backend,
   * this simply exposes the server response.
   */
  get sortedEmployees(): Employee[] {

    return this.employees;
  }

  /**
   * Compatibility getter for existing template.
   */
  get filteredEmployees(): Employee[] {

    return this.employees;
  }

  /**
   * Clear search.
   */
  clearSearch(): void {

    this.searchTerm = '';
    this.pageIndex = 0;

    this.loadEmployees();
  }

  private showSuccess(
    message: string
  ): void {

    this.successMessage = message;
    this.errorMessage = '';

    setTimeout(() => {

      this.successMessage = '';

    }, 3500);
  }

  private showError(
    message: string
  ): void {

    this.errorMessage = message;
    this.successMessage = '';

    setTimeout(() => {

      this.errorMessage = '';

    }, 5000);
  }

  private clearMessages(): void {

    this.errorMessage = '';
    this.successMessage = '';
  }

  private formatAmount(
    amount: number
  ): string {

    return new Intl.NumberFormat(
      'en-US',
      {
        style: 'currency',
        currency: 'USD',
        maximumFractionDigits: 0
      }
    ).format(amount);
  }

  private escapeCsv(
    value: string
  ): string {

    if (
      value.includes(',') ||
      value.includes('"') ||
      value.includes('\n')
    ) {

      return `"${value.replace(/"/g, '""')}"`;
    }

    return value;
  }

  private getErrorMessage(
    error: any,
    fallback: string
  ): string {

    return (
      error?.error?.message ||
      error?.message ||
      fallback
    );
  }

  ngOnDestroy(): void {

    this.destroy$.next();
    this.destroy$.complete();
  }

  get firstDisplayedEmployee(): number {

    if (this.totalEmployees === 0) {
      return 0;
    }

    return (
      this.pageIndex * this.pageSize
    ) + 1;
  }

  get lastDisplayedEmployee(): number {

    return Math.min(
      (this.pageIndex + 1) * this.pageSize,
      this.totalEmployees
    );
  }

}