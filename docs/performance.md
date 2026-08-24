# Performance Considerations

## Dataset
The application is designed around 10,000 employees.

## Server-side Pagination
Only the requested page is returned to the browser.

## Server-side Sorting
Spring Data `Pageable`/`Sort` delegates ordering to the database.

## Search and Filtering
Search and department filtering are performed by the repository query.

## Salary Analytics
Average, minimum and maximum salary use database aggregation queries instead of downloading all employees.

## API Input Protection
Pagination values are validated and the backend limits the maximum page size.

## Bulk Operations
Bulk adjustment uses one API request for multiple employee IDs.

## UI Responsiveness
Loading indicators and disabled controls reduce accidental duplicate operations.

## Future Production Improvements
Possible next steps include database indexes, query-plan analysis, optimized text search, connection-pool tuning, caching, background processing for very large bulk jobs, authentication/authorization and observability.
