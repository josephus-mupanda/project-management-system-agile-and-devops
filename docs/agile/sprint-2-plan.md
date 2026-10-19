# Sprint 2 – Plan

**Sprint goal:** make the application observable (logs, error tracking, health check) and complete user and project management, applying the improvements from the Sprint 1 retrospective.

## Capacity

Sprint 1 velocity was **8 points**, so Sprint 2 commits to **8 points**, plus the refactoring from retrospective action A2.

## Selected items

| Item | Points | Why in this sprint |
|---|---|---|
| A2 – Move users into `UserService` with tests | – | Retrospective action, needed before US-04 |
| US-03 – Search projects by name | 2 | Quick win for every user |
| US-04 – Delete a user | 3 | Builds on the new `UserService` |
| US-05 – Logging and health check | 3 | Monitoring requirement, retrospective action A3 |

**Committed:** 8 story points (3 stories) plus 1 improvement item.

The acceptance criteria of each story are in [sprint-0-planning.md](sprint-0-planning.md#4-acceptance-criteria).

## How the retrospective actions are applied

| Action | Applied in this sprint by |
|---|---|
| A1 – Tests in the same commit as the feature | Every `feat` commit below includes its tests |
| A2 – `UserService` | First item of the sprint |
| A3 – Logging | US-05 |
| A4 – Import Maven project in the IDE | Done at the start of the sprint |
| A5 – Evidence right after each push | One CI screenshot per story in `docs/evidence` |

## Task breakdown

1. Create `UserService` (add, find, email check, list) and `UserServiceTest`; `ConsoleMenu` uses it.
2. US-03: `ProjectService.searchByName`, a catalog option "Search by Name", and tests in the same commit.
3. US-04: `UserService.deleteUser` that refuses to delete the current user, a menu option, and tests in the same commit.
4. US-05: an `AppLogger` writing to `logs/app.log`, log lines for every create, update and delete, and error tracking in the main menu.
5. US-05: a `HealthService` and a "System Health" option in the main menu, with tests.
6. Sprint review and retrospective.
