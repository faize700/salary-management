# AI-Assisted Development Workflow

AI was used intentionally as an engineering acceleration tool.

## Development Loop
1. Clarify the requirement and expected behavior.
2. Break the requirement into small tasks.
3. Ask AI for implementation approaches, code suggestions, tests or debugging assistance.
4. Review suggestions against the existing architecture.
5. Implement or adapt the suggestion.
6. Compile and run automated tests.
7. Manually verify browser/API behavior.
8. Refine the implementation.
9. Commit the verified change incrementally.

## AI Use Cases
- Backend/frontend structure
- REST DTOs and service methods
- Unit-test ideas and implementations
- Debugging compilation/test failures
- Server-side pagination/search/sorting
- Angular table interactions
- Bulk salary operations
- UI refinement
- Documentation and trade-off analysis

## Verification Principle
AI output was treated as a proposal, not as the source of truth. Changes were validated with Maven tests, manual browser testing and API/network inspection.

## Prompt Pattern
```text
Context: explain the current architecture and relevant code.
Goal: describe the exact behavior required.
Constraints: preserve existing APIs/design unless necessary.
Request: propose an implementation and explain trade-offs.
Validation: include tests or verification steps.
```
