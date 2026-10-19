# Sprint 2 – Retrospective

## Did the Sprint 1 improvements work?

| Action from Sprint 1 | Applied? | Result |
|---|---|---|
| A1 – Tests in the same commit as the feature | Yes | All 5 code commits of Sprint 2 change files in `test/`. "CI is green" now really means "the new code is tested". |
| A2 – Move users into `UserService` | Yes | `ConsoleMenu` lost 65 lines and users got 6 tests. US-04 (delete a user) then took one small commit, because the rule "cannot delete yourself" could live in the service and be unit tested. |
| A3 – Logging | Yes (US-05) | Every action leaves a line in `logs/app.log`. While checking US-05 by hand, the log showed exactly which steps ran, without reading the console output. |
| A4 – Import the Maven project in the IDE | Yes | IntelliJ recognises `src` and `test`; no more false red errors in tests. |
| A5 – Screenshot right after each push | Partly | Done for US-03, US-04 and the final test run, but missed for the first US-05 commit (logging). |

## What went well

- **The improvements paid off quickly.** Sprint 2 had the same number of points as Sprint 1 but needed fewer commits per story, because each `feat` commit was complete (code + tests + docs).
- **Testing the menu itself.** `ConsoleMenuTest` runs the real menu with typed input and a service that fails on purpose. It is the only way to prove that an unexpected error does not crash the program, which is hard to show by hand.
- **Velocity is stable**: 8 points committed and delivered in both sprints, so the estimates were realistic.

## What did not go well

1. **The smoke test could pass for the wrong reason.** When "Exit" moved from option 3 to option 4, the CI step `echo "3" | java ... Main` would still have finished with success: it would open "System Health", then stop because the input ended. It was updated to `4`, but the step only checks that the program ends, not that it ends normally.
2. **Everything is pushed straight to `main`.** CI runs after the code is already on the main branch, so a red build would already be shared.
3. **`ConsoleMenu` is still the biggest class** (about 480 lines), with project screens, user screens and error handling in one file.
4. **Evidence is still manual.** Screenshots depend on remembering to take them (action A5 was only partly done).

## Lessons learned

- A retrospective action is only useful if it can be checked. "Write tests with the feature" was easy to verify from the commit history; "take screenshots" was not, and it is the one that was only partly done.
- Moving code into a service before adding a feature (A2) is a small investment that makes the next story faster and testable.
- Tests and CI steps that depend on menu numbers break silently when the menu changes. Checks should look at the result (the output), not only at the exit code.

## Improvements for a next sprint

| # | Action | Linked problem |
|---|---|---|
| B1 | Make the smoke test check the output, for example `echo "4" \| java -cp target/classes Main \| grep "Goodbye."`, so it fails if the program does not exit normally. | 1 |
| B2 | Work on a branch per story and merge with a pull request, with the CI check required before merging. | 2 |
| B3 | Split `ConsoleMenu` into `ProjectMenu` and `UserMenu`. | 3 |
| B4 | Use the test report artifact that CI already uploads as testing evidence, instead of screenshots only. | 4 |
