# Task Management System

A console based project and task management system written in Java 21. Users can create projects, add tasks, give them to people, track task status and see how complete each project is.

All data lives in memory and is stored in plain arrays, so nothing is saved when the program closes. This is the base for later labs, where the arrays will become collections and the data will be saved to files.

## Contents

1. [How to run](#how-to-run)
2. [How the program is organised](#how-the-program-is-organised)
3. [Using the application](#using-the-application)
4. [Roles and permissions](#roles-and-permissions)
5. [Class diagram](#class-diagram)
6. [Features mapped to user stories](#features-mapped-to-user-stories)
7. [Project structure](#project-structure)
8. [More documentation](#more-documentation)

## How to run

**Requirements:** JDK 21 and IntelliJ IDEA Community Edition (or any terminal with `javac`).

In IntelliJ:

1. Open the project folder.
2. Set the project SDK to JDK 21 and mark `src` as the sources root.
3. Run `Main`.

From a terminal, in the project root:

```
javac -encoding UTF-8 -d out src/Main.java src/models/*.java src/interfaces/*.java src/services/*.java src/utils/*.java
java -cp out Main
```

If the box characters show up as question marks, run with `java -Dstdout.encoding=UTF-8 -cp out Main`. In IntelliJ, add `-Dstdout.encoding=UTF-8` to the VM options of the run configuration.

When the program starts you are logged in as **Admin**. **Josephus Mupanda (Regular)** also exists, and five sample projects of mine are already loaded (Budget Buddy, Smart Garden Monitor, Portfolio Website, Home Power Meter and Study Planner).

## How the program is organised

The code is split in layers. Each layer only talks to the one below it.

```mermaid
flowchart TD
    A["Main - creates the objects and loads sample data"] --> B["ConsoleMenu - screens and navigation"]
    B --> C["ValidationUtils - reads and checks every input"]
    B --> D["Services - ProjectService, TaskService, ReportService"]
    D --> E["Models - Project, Task, User, StatusReport"]
    E --> F["Arrays in memory"]
```

| Layer | What it does |
|---|---|
| `Main` | Builds the services, loads five projects, a few tasks and two users, then starts the menu. |
| `ConsoleMenu` | Prints the screens, reads the choices and calls the services. |
| `ValidationUtils` | Keeps asking until the input is valid (numbers, text, IDs, status, email). |
| Services | The actual operations: find, filter, add, update, remove, report. They never read the keyboard. |
| Models | The data and the rules attached to it (for example how a completion percentage is computed). |

## Using the application

### Navigation

```mermaid
flowchart TD
    M(["Main Menu"]) --> P["1. Manage Projects"]
    M --> T["2. Manage Tasks"]
    M --> R["3. View Status Reports"]
    M --> U["4. Switch User"]
    M --> X(["5. Exit"])

    P --> P1["Browse Project Catalog"]
    P --> P2["Add New Project"]
    P1 --> F["Filter: all, software, hardware, budget range"]
    F --> D["Enter a project ID to open its details"]
    D --> DO["Add, update or remove tasks"]

    T --> T1["Add New Task"]
    T --> T2["View Tasks by Project"]
    T --> T3["Update Task Status"]
    T --> T4["Remove Task"]
    T --> T5["Calculate Project Completion"]

    U --> U1["Switch to another user"]
    U --> U2["Create a new user - admin only"]
```

Every sub-menu has a way back to the main menu. Typing `0` where it is offered also returns.

### IDs

IDs are shown with a prefix: `PRJ001` for projects, `TSK001` for tasks, `U001` for users. When the program asks for an ID you can type `PRJ001` or just `1`.

### Adding a task

```mermaid
flowchart TD
    A["Enter task name"] --> B["Enter assigned project ID"]
    B --> C{"Project exists?"}
    C -- No --> E1["Error: Project not found"]
    C -- Yes --> D["Enter initial status, hours and assigned user"]
    D --> S{"Valid status?"}
    S -- No --> D
    S -- Yes --> N{"Same name already in this project?"}
    N -- Yes --> E2["Error: duplicate task name"]
    N -- No --> OK["Task is created with the next T-number and stored in the project"]
```

### Updating a task status

```mermaid
flowchart TD
    A{"Is the user an admin?"} -- No --> E1["Error: Access denied"]
    A -- Yes --> B["Enter task ID"]
    B --> C{"Task found?"}
    C -- No --> E2["Error: Task not found"]
    C -- Yes --> D["Enter new status"]
    D --> V{"Pending, In Progress or Completed?"}
    V -- No --> D
    V -- Yes --> OK["Status is updated and the completion rate changes"]
```

### How the completion rate is computed

```mermaid
flowchart LR
    A["Go through the tasks of the project"] --> B["Count total tasks"]
    A --> C["Count completed tasks"]
    B --> D{"Total is 0?"}
    D -- Yes --> Z["0.00%"]
    D -- No --> E["completed / total x 100"]
    C --> E
    E --> F["Round to 2 decimals"]
```

The status report repeats this for every project and then averages the percentages. Example for a report with two projects at 75.00% and 50.00%: the average completion is 62.50%.

### Sample session

```
Current User: Admin (Admin)

Main Menu:
-----------
1. Manage Projects
2. Manage Tasks
3. View Status Reports
4. Switch User
5. Exit

Enter your choice: 3

───────────────────────────────────────────────────────────────────────
PROJECT ID | PROJECT NAME        | TASKS | COMPLETED | PROGRESS (%)
───────────────────────────────────────────────────────────────────────
PRJ001     | Budget Buddy        |   3   |     1     | 33.33%
PRJ002     | Smart Garden Monitor|   2   |     1     | 50%
...
───────────────────────────────────────────────────────────────────────
AVERAGE COMPLETION: 16.67%
```

## Roles and permissions

```mermaid
flowchart LR
    A["Action chosen"] --> B{"currentUser.canModify()"}
    B -- "true: AdminUser" --> C["Action runs"]
    B -- "false: RegularUser" --> D["Error: Access denied"]
```

| Action | Regular | Admin |
|---|---|---|
| Browse projects, view tasks, view reports | yes | yes |
| Add projects and tasks (with hours and assigned user) | yes | yes |
| Update task status, remove tasks | no | yes |
| Create users | no | yes |

## Class diagram

![Class diagram](docs/class-diagram.png)

The diagram shows the model classes only: the interface, the two abstract classes with their subclasses, and how projects, tasks and users relate. The reasons behind these choices are in [design-decisions.md](docs/design-decisions.md).

## Features mapped to user stories

| Story | What it covers | Where it lives |
|---|---|---|
| US-1.1 Browse projects | Catalog with ID, name, type, team size, budget, completion, duration, description, software and hardware listed separately with a total count, and filter by type | `Project`, `SoftwareProject`, `HardwareProject`, `ProjectService`, `ConsoleMenu` |
| US-1.2 Search by budget range | Min and max budget, "No projects found" when empty | `ProjectService.getByBudgetRange` |
| US-2.1 Add task to project | Unique task ID (TSK001...), name, initial status, hours, assigned user, duplicate names refused | `Task`, `Completable`, `TaskService.addTask` |
| US-2.2 Update task status | Find the task by ID, choose an allowed status | `Task.Status`, `TaskService.updateTaskStatus` |
| US-3.1 Create user profiles | Regular and admin users, automatic IDs, role shown when logged in | `User`, `RegularUser`, `AdminUser`, `ConsoleMenu` |
| US-4.1 Completion average | Totals, completed count, percentage rounded to 2 decimals, zero tasks handled | `Project.getCompletionPercentage`, `StatusReport`, `ReportService` |
| US-5.1 Main menu | Numbered options 1 to 5, validated, loops until exit | `ConsoleMenu.start` (do-while with a switch) |

## Project structure

```
src/
  Main.java                  entry point and sample data
  interfaces/
    Completable.java
  models/
    Project.java             abstract
    SoftwareProject.java
    HardwareProject.java
    Task.java
    User.java                abstract
    RegularUser.java
    AdminUser.java
    StatusReport.java
  services/
    ProjectService.java
    TaskService.java
    ReportService.java
  utils/
    ConsoleMenu.java
    ValidationUtils.java
docs/
  class-diagram.png
  design-decisions.md
README.md
```

## More documentation

- [Design decisions](docs/design-decisions.md): why the classes are shaped this way, with diagrams.
