# Sprint 1 – Retrospective

Format: **What went well / What did not go well / What we will change**.

## What went well

- **Small, focused commits.** Every commit does one thing and names the story (`feat(US-01)`, `test(US-02)`), so the history tells the story of the sprint.
- **CI from the start.** The pipeline was set up before the first feature, so every feature commit was built and tested automatically. It was green on every push.
- **Acceptance criteria written in advance.** Because the criteria were written in Sprint 0, it was clear when a story was finished. They were used directly as the manual check list.
- **Validation in one place.** `Project.updateDetails` checks every value before changing anything, so an invalid update can never leave a project half changed.

## What did not go well

1. **Tests came one commit after the feature.** For US-01 and US-02 the feature was pushed first and its tests in the next commit. During that time the CI was green, but it was not testing the new code yet, so "green" did not mean "the new feature works".
2. **User logic cannot be tested.** Users (list, search, create) live inside `ConsoleMenu`, mixed with the screens. Nothing about users is covered by unit tests, and US-04 (delete a user) would make this worse.
3. **No trace of what happened.** When checking the features by hand, the only way to know what the program did was to read the console output. If something goes wrong, nothing is recorded.
4. **IDE setup slowed me down.** The editor showed red errors in the test files (`org.junit cannot be resolved`) because the Maven project was not imported, although Maven built everything correctly. Time was lost checking errors that were not real.
5. **Evidence was collected late.** Screenshots of the CI were taken at the end of the sprint instead of when each push finished.

## Improvements for Sprint 2

| # | Action | Linked problem | How we will know it worked |
|---|---|---|---|
| A1 | **Write the tests in the same commit as the feature.** A `feat` commit is only pushed with its tests. | 1 | Every `feat(US-..)` commit in Sprint 2 also changes a file in `test/` |
| A2 | **Move user logic into a new `UserService`, with unit tests, before starting US-04.** | 2 | `ConsoleMenu` no longer stores users; `UserServiceTest` exists and passes |
| A3 | **Add logging to a file (part of US-05)**, so every create, update, delete and error leaves a trace. | 3 | `logs/app.log` contains one line per action |
| A4 | **Import the project as a Maven project in the IDE** (IntelliJ: right-click `pom.xml` → Add as Maven Project). | 4 | No false red errors in test files |
| A5 | **Take the CI screenshot right after each story is pushed.** | 5 | Sprint 2 evidence has one screenshot per story |
