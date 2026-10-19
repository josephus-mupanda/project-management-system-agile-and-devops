# Sprint 0 – Planning

## 1. Product vision

**Project Hub** is a small console application that gives a team one clear catalog of its software and hardware projects, and makes sure only admins can change it, so everyone finds project information quickly and the data stays correct.

## 2. Scope decision

The starting code was a full task management system (projects, tasks, users, reports). To focus on Agile and DevOps practice rather than on product size, the scope is reduced to **two areas only**:

- **Users**: regular and admin users, switching user, role-based access.
- **Projects**: software and hardware projects in a catalog.

Everything related to tasks (tasks, task status, completion rates and status reports) is removed during Sprint 0.

### Features already working (kept from the starting code)

| Feature | Notes |
|---|---|
| Browse the project catalog | Filter by all / software / hardware |
| Add a project | Software (tech stack) or hardware (device type), duplicate names refused |
| Search projects by budget range | "No projects found." when nothing matches |
| Create a user | Admin only, regular or admin role, unique email |
| Switch user | Current user and role shown on the main menu |

## 3. Product backlog

Priorities use **High / Medium / Low**. Estimates use **story points** on the Fibonacci scale (1, 2, 3, 5, 8). 1 point is a very small change (one method), 8 points is too big for one sprint and should be split.

| ID | User story | Priority | Points | Sprint |
|---|---|---|---|---|
| US-01 | As an **admin**, I want to update a project's details, so that the catalog stays correct when plans change. | High | 5 | 1 |
| US-02 | As an **admin**, I want to delete a project, so that cancelled projects no longer appear in the catalog. | High | 3 | 1 |
| US-03 | As a **user**, I want to search projects by name, so that I can find a project quickly without scrolling. | Medium | 2 | 2 |
| US-04 | As an **admin**, I want to delete a user, so that people who left the team cannot be selected anymore. | Medium | 3 | 2 |
| US-05 | As the **maintainer**, I want the application to log its actions and errors and to show a health status, so that I can see what happened and whether it runs correctly. | High | 3 | 2 |
| US-06 | As an **admin**, I want to assign an owner (a user) to a project, so that everyone knows who is responsible. | Low | 5 | Not planned |
| US-07 | As a **user**, I want projects and users to be saved to a file, so that data is kept after the program closes. | Low | 8 | Not planned |

### Technical items (enablers)

These are not user features, but they are needed to work in a DevOps way.

| ID | Item | Sprint |
|---|---|---|
| EN-01 | Remove the task feature to reduce the scope to users and projects | 0 |
| EN-02 | Add Maven and JUnit 5 so tests can run with one command | 1 |
| EN-03 | Add a GitHub Actions CI pipeline that builds and tests every push | 1 |

### Why this order

1. **US-01 and US-02 first**: managing the catalog is the core of the product, and today a mistake in a project can never be fixed or removed.
2. **US-05 next**: once the app changes data, we need logs and a health check to know what happened.
3. **US-03 and US-04** are useful but the app is usable without them.
4. **US-06 and US-07** stay in the backlog: they are bigger and not needed for a first working version.

## 4. Acceptance criteria

### US-01 – Update project details (5 pts)

1. Given I am an **admin** and I open a project, when I choose "Update Project", then I can enter a new name, description, budget, team size and duration.
2. When I press **Enter** without typing anything, the current value is kept.
3. When I type a name already used by **another** project, I see `Error: A project with this name already exists.` and I am asked again.
4. When I type a budget, team size or duration that is 0 or less, I see an error and I am asked again.
5. Given I am a **regular** user, when I choose "Update Project", I see `Error: Access denied. Only admins can update projects.`

### US-02 – Delete a project (3 pts)

1. Given I am an **admin** and I open a project, when I choose "Delete Project" and confirm with `y`, the project is removed and I see `Project "<name>" deleted.`
2. When I answer `n`, nothing is deleted.
3. After deletion, the project no longer appears in the catalog, and its ID is never reused.
4. Given I am a **regular** user, I see `Error: Access denied. Only admins can delete projects.`

### US-03 – Search projects by name (2 pts)

1. When I type part of a name (for example `garden`), every project whose name contains that text is shown, ignoring upper and lower case.
2. When nothing matches, I see `No projects found.`
3. When I type nothing, I am asked again.

### US-04 – Delete a user (3 pts)

1. Given I am an **admin**, when I enter a user ID and confirm with `y`, the user is removed from the list.
2. I cannot delete the user I am logged in as: I see `Error: You cannot delete the user you are logged in as.`
3. When the ID does not exist, I see `Error: User not found.`
4. Given I am a **regular** user, I see `Error: Access denied. Only admins can delete users.`

### US-05 – Logging and health check (3 pts)

1. Creating, updating or deleting a project or a user writes a line to `logs/app.log` with the date, time, level and message.
2. If an unexpected error happens in a menu, the program does not crash: the error is logged with level `SEVERE`, the user sees a short message and returns to the main menu.
3. A main menu option "System Health" shows: status (`UP`, or `DEGRADED` if errors happened), uptime, number of projects, number of users and number of errors since start.
4. The `logs` folder is created automatically if it does not exist.

## 5. Definition of Done (DoD)

A backlog item is **Done** only when all of these are true:

- [ ] All acceptance criteria are met and were checked by hand in the console.
- [ ] Unit tests cover the new logic in the services or models, and all tests pass with `mvn verify`.
- [ ] The CI pipeline on GitHub Actions is green for the last commit.
- [ ] The work is in small commits with a clear message that names the story (for example `feat(US-01): ...`).
- [ ] No compiler errors or warnings were introduced, and no unused code is left behind.
- [ ] The README or the docs are updated when the behaviour visible to the user changes.

## 6. Sprint 1 plan

**Sprint goal:** set up the DevOps foundation (build, tests, CI) and let admins keep the project catalog correct.

| Item | Points | Why in this sprint |
|---|---|---|
| EN-02 – Maven and JUnit 5 | – | Needed before any test can run |
| EN-03 – GitHub Actions CI | – | Every following commit is checked automatically |
| US-01 – Update project details | 5 | Highest-value story |
| US-02 – Delete a project | 3 | Completes basic catalog management |

**Committed:** 8 story points (2 stories) plus 2 enablers.

### Task breakdown

1. Add `pom.xml` with JUnit 5 and write first tests for the existing project logic.
2. Add the CI workflow and push to GitHub.
3. US-01: setters with validation in `Project`, `updateProject` in `ProjectService`, update screen in the menu.
4. US-01: unit tests.
5. US-02: `deleteProject` in `ProjectService`, confirmation in the menu.
6. US-02: unit tests.
7. Sprint review and retrospective.
