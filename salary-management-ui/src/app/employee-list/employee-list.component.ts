import {
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import {
  EmployeeService,
  Employee,
  EmployeeRequest
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

  // =========================================================
  // Pagination
  // =========================================================

  pageSize = 10;
  pageIndex = 0;

  totalEmployees = 0;
  totalPages = 0;

  // =========================================================
  // Search / Filter
  // =========================================================

  searchTerm = '';
  selectedDept = '';

  // =========================================================
  // Sorting
  // =========================================================

  sortColumn = 'id';
  sortDirection: 'asc' | 'desc' = 'asc';

  // =========================================================
  // Analytics
  // =========================================================

  averageSalary = 0;
  minSalary = 0;
  maxSalary = 0;

  // =========================================================
  // UI State
  // =========================================================

  isLoading = false;
  isUpdating = false;

  errorMessage = '';
  successMessage = '';

  // =========================================================
  // CRUD
  // =========================================================

  showEmployeeModal = false;
  showDeleteModal = false;

  isEditMode = false;
  isSavingEmployee = false;
  isDeletingEmployee = false;

  employeeBeingDeleted: Employee | null = null;

  employeeForm: EmployeeRequest = {
    name: '',
    department: '',
    country: '',
    salary: 0
  };

  editingEmployeeId: number | null = null;

  // =========================================================
  // Dropdown options
  // =========================================================

  readonly departments = [
    'HR',
    'Engineering',
    'Finance',
    'Marketing',
    'Sales'
  ];

  readonly countries = [
    'India',
    'USA',
    'UK',
    'Brazil',
    'Germany'
  ];

  // =========================================================
  // Bulk Salary Operations
  // =========================================================

  selectedEmployeeIds = new Set<number>();

  bulkAdjustment = 0;

  isBulkProcessing = false;

  // =========================================================
  // RxJS
  // =========================================================

  private readonly destroy$ =
    new Subject<void>();

  private readonly searchSubject =
    new Subject<string>();

  constructor(
    private employeeService: EmployeeService
  ) {}

  // =========================================================
  // INIT
  // =========================================================

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

        this.clearSelection();

        this.loadEmployees();
      });

    this.loadEmployees();
    this.loadReports();
  }

  // =========================================================
  // LOAD EMPLOYEES
  // =========================================================

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

  // =========================================================
  // SEARCH
  // =========================================================

  onSearchChange(value: string): void {
    this.searchSubject.next(value);
  }

  // =========================================================
  // DEPARTMENT
  // =========================================================

  onDepartmentChange(): void {

    this.pageIndex = 0;

    this.clearSelection();

    this.loadEmployees();
    this.loadReports();
  }

  // =========================================================
  // PAGINATION
  // =========================================================

  onPageChange(event: PageEvent): void {

    this.pageIndex =
      event.pageIndex;

    this.pageSize =
      event.pageSize;

    this.clearSelection();

    this.loadEmployees();
  }

  // =========================================================
  // SORTING
  // =========================================================

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

    this.clearSelection();

    this.loadEmployees();
  }

  // =========================================================
  // CREATE / EDIT EMPLOYEE
  // =========================================================

  openAddEmployeeModal(): void {

    this.clearMessages();

    this.isEditMode = false;

    this.editingEmployeeId = null;

    this.employeeForm = {
      name: '',
      department: '',
      country: '',
      salary: 0
    };

    this.showEmployeeModal = true;
  }

  openEditEmployeeModal(
    employee: Employee
  ): void {

    this.clearMessages();

    this.isEditMode = true;

    this.editingEmployeeId =
      employee.id;

    this.employeeForm = {
      name: employee.name,
      department: employee.department,
      country: employee.country,
      salary: employee.salary
    };

    this.showEmployeeModal = true;
  }

  closeEmployeeModal(): void {

    if (this.isSavingEmployee) {
      return;
    }

    this.showEmployeeModal = false;

    this.editingEmployeeId = null;
  }

  saveEmployee(): void {

    const validationError =
      this.validateEmployeeForm();

    if (validationError) {

      this.showError(
        validationError
      );

      return;
    }

    const request: EmployeeRequest = {
      name: this.employeeForm.name.trim(),
      department: this.employeeForm.department.trim(),
      country: this.employeeForm.country.trim(),
      salary: Number(this.employeeForm.salary)
    };

    this.isSavingEmployee = true;
    this.isUpdating = true;

    this.clearMessages();

    if (
      this.isEditMode &&
      this.editingEmployeeId !== null
    ) {

      this.employeeService
        .updateEmployee(
          this.editingEmployeeId,
          request
        )
        .pipe(
          takeUntil(this.destroy$)
        )
        .subscribe({

          next: () => {

            this.isSavingEmployee = false;
            this.isUpdating = false;

            this.showEmployeeModal = false;

            this.showSuccess(
              'Employee updated successfully.'
            );

            this.loadEmployees();
            this.loadReports();
          },

          error: error => {

            this.isSavingEmployee = false;
            this.isUpdating = false;

            this.showError(
              this.getErrorMessage(
                error,
                'Unable to update employee.'
              )
            );
          }
        });

    } else {

      this.employeeService
        .createEmployee(request)
        .pipe(
          takeUntil(this.destroy$)
        )
        .subscribe({

          next: () => {

            this.isSavingEmployee = false;
            this.isUpdating = false;

            this.showEmployeeModal = false;

            this.showSuccess(
              'Employee created successfully.'
            );

            this.pageIndex = 0;

            this.loadEmployees();
            this.loadReports();
          },

          error: error => {

            this.isSavingEmployee = false;
            this.isUpdating = false;

            this.showError(
              this.getErrorMessage(
                error,
                'Unable to create employee.'
              )
            );
          }
        });
    }
  }

  // =========================================================
  // DELETE EMPLOYEE
  // =========================================================

  openDeleteConfirmation(
    employee: Employee
  ): void {

    this.clearMessages();

    this.employeeBeingDeleted =
      employee;

    this.showDeleteModal = true;
  }

  closeDeleteConfirmation(): void {

    if (this.isDeletingEmployee) {
      return;
    }

    this.showDeleteModal = false;

    this.employeeBeingDeleted = null;
  }

  confirmDeleteEmployee(): void {

    if (
      !this.employeeBeingDeleted
    ) {
      return;
    }

    const employeeId =
      this.employeeBeingDeleted.id;

    this.isDeletingEmployee = true;
    this.isUpdating = true;

    this.clearMessages();

    this.employeeService
      .deleteEmployee(employeeId)
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: () => {

          this.isDeletingEmployee = false;
          this.isUpdating = false;

          this.showDeleteModal = false;

          this.selectedEmployeeIds.delete(
            employeeId
          );

          this.employeeBeingDeleted = null;

          this.showSuccess(
            'Employee deleted successfully.'
          );

          /*
           * If the last employee on the current
           * page was deleted, move back one page.
           */
          if (
            this.employees.length === 1 &&
            this.pageIndex > 0
          ) {
            this.pageIndex--;
          }

          this.loadEmployees();
          this.loadReports();
        },

        error: error => {

          this.isDeletingEmployee = false;
          this.isUpdating = false;

          this.showError(
            this.getErrorMessage(
              error,
              'Unable to delete employee.'
            )
          );
        }
      });
  }

  // =========================================================
  // EMPLOYEE FORM VALIDATION
  // =========================================================

  private validateEmployeeForm():
    string | null {

    const name =
      this.employeeForm.name?.trim();

    const department =
      this.employeeForm.department?.trim();

    const country =
      this.employeeForm.country?.trim();

    const salary =
      Number(this.employeeForm.salary);

    if (!name) {
      return 'Employee name is required.';
    }

    if (name.length < 2) {
      return 'Employee name must contain at least 2 characters.';
    }

    if (!department) {
      return 'Please select a department.';
    }

    if (!country) {
      return 'Please select a country.';
    }

    if (
      Number.isNaN(salary) ||
      salary <= 0
    ) {
      return 'Salary must be greater than zero.';
    }

    return null;
  }

  // =========================================================
  // INDIVIDUAL SALARY ADJUSTMENT
  // =========================================================

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

  // =========================================================
  // INDIVIDUAL ABSOLUTE SALARY UPDATE
  // =========================================================

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

  // =========================================================
  // BULK SELECTION
  // =========================================================

  toggleEmployeeSelection(
    employeeId: number
  ): void {

    if (
      this.selectedEmployeeIds.has(
        employeeId
      )
    ) {

      this.selectedEmployeeIds.delete(
        employeeId
      );

    } else {

      this.selectedEmployeeIds.add(
        employeeId
      );
    }
  }

  isEmployeeSelected(
    employeeId: number
  ): boolean {

    return this.selectedEmployeeIds.has(
      employeeId
    );
  }

  toggleSelectAll(): void {

    if (this.isAllPageSelected) {

      this.employees.forEach(employee => {

        this.selectedEmployeeIds.delete(
          employee.id
        );

      });

    } else {

      this.employees.forEach(employee => {

        this.selectedEmployeeIds.add(
          employee.id
        );

      });
    }
  }

  get isAllPageSelected(): boolean {

    return (
      this.employees.length > 0 &&
      this.employees.every(
        employee =>
          this.selectedEmployeeIds.has(
            employee.id
          )
      )
    );
  }

  get selectedCount(): number {

    return this.selectedEmployeeIds.size;
  }

  clearSelection(): void {

    this.selectedEmployeeIds.clear();

    this.bulkAdjustment = 0;
  }

  // =========================================================
  // BULK SALARY ADJUSTMENT
  // =========================================================

  applyBulkAdjustment(): void {

    if (
      this.selectedEmployeeIds.size === 0
    ) {

      this.showError(
        'Please select at least one employee.'
      );

      return;
    }

    const adjustment =
      Number(this.bulkAdjustment);

    if (
      Number.isNaN(adjustment) ||
      adjustment === 0
    ) {

      this.showError(
        'Please enter a valid adjustment amount.'
      );

      return;
    }

    const selectedCount =
      this.selectedEmployeeIds.size;

    const confirmed =
      window.confirm(
        `Apply ${this.formatAmount(adjustment)} ` +
        `salary adjustment to ${selectedCount} ` +
        `${selectedCount === 1 ? 'employee' : 'employees'}?`
      );

    if (!confirmed) {
      return;
    }

    this.isBulkProcessing = true;
    this.isUpdating = true;

    this.clearMessages();

    this.employeeService
      .bulkAdjustSalary(
        Array.from(
          this.selectedEmployeeIds
        ),
        adjustment
      )
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({

        next: response => {

          this.isBulkProcessing = false;
          this.isUpdating = false;

          this.showSuccess(
            response.message ||
            `${response.updatedEmployees} employees updated successfully.`
          );

          this.clearSelection();

          this.loadEmployees();
          this.loadReports();
        },

        error: error => {

          this.isBulkProcessing = false;
          this.isUpdating = false;

          this.showError(
            this.getErrorMessage(
              error,
              'Unable to apply bulk salary adjustment.'
            )
          );
        }
      });
  }

  // =========================================================
  // REPORTS
  // =========================================================

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
          this.averageSalary =
            value ?? 0,

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
          this.minSalary =
            value ?? 0,

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
          this.maxSalary =
            value ?? 0,

        error: () =>
          this.maxSalary = 0
      });
  }

  // =========================================================
  // CSV EXPORT
  // =========================================================

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

        this.escapeCsv(
          employee.name
        ),

        this.escapeCsv(
          employee.department
        ),

        this.escapeCsv(
          employee.country
        ),

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

  // =========================================================
  // TEMPLATE GETTERS
  // =========================================================

  get sortedEmployees(): Employee[] {

    return this.employees;
  }

  get filteredEmployees(): Employee[] {

    return this.employees;
  }

  get firstDisplayedEmployee(): number {

    if (this.totalEmployees === 0) {
      return 0;
    }

    return (
      this.pageIndex *
      this.pageSize
    ) + 1;
  }

  get lastDisplayedEmployee(): number {

    return Math.min(
      (this.pageIndex + 1) *
      this.pageSize,

      this.totalEmployees
    );
  }

  // =========================================================
  // CLEAR SEARCH
  // =========================================================

  clearSearch(): void {

    this.searchTerm = '';

    this.pageIndex = 0;

    this.clearSelection();

    this.loadEmployees();
  }

  // =========================================================
  // MESSAGES
  // =========================================================

  private showSuccess(
    message: string
  ): void {

    this.successMessage =
      message;

    this.errorMessage = '';

    setTimeout(() => {

      this.successMessage = '';

    }, 3500);
  }

  private showError(
    message: string
  ): void {

    this.errorMessage =
      message;

    this.successMessage = '';

    setTimeout(() => {

      this.errorMessage = '';

    }, 5000);
  }

  private clearMessages(): void {

    this.errorMessage = '';
    this.successMessage = '';
  }

  // =========================================================
  // HELPERS
  // =========================================================

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

      return `"${value.replace(
        /"/g,
        '""'
      )}"`;
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

  // =========================================================
  // DESTROY
  // =========================================================

  ngOnDestroy(): void {

    this.destroy$.next();
    this.destroy$.complete();
  }
}