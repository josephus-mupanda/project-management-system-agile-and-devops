package services;

import models.Project;
import models.StatusReport;
import utils.ConsoleMenu;

public class ReportService {

    private final ProjectService projectService;

    public ReportService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public StatusReport[] generateReports() {
        Project[] projects = projectService.getProjects();
        StatusReport[] reports = new StatusReport[projects.length];

        for (int i = 0; i < projects.length; i++) {
            reports[i] = new StatusReport(projects[i]);
        }
        return reports;
    }

    public void displayProjectReport() {
        StatusReport[] reports = generateReports();

        ConsoleMenu.printHeader("PROJECT STATUS REPORT");

        if (reports.length == 0) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println(ConsoleMenu.LINE);
        System.out.printf(
                "%-10s | %-19s | %-5s | %-9s | %s%n",
                "PROJECT ID", "PROJECT NAME", "TASKS", "COMPLETED", "PROGRESS (%)"
        );
        System.out.println(ConsoleMenu.LINE);

        double totalPercentage = 0;
        int total = 0;
        int completed = 0;
        int inProgress = 0;
        int pending = 0;

        for (StatusReport report : reports) {
            System.out.printf(
                    "%-10s | %-19s | %s | %s | %s%n",
                    report.getProjectId(),
                    report.getProjectName(),
                    center(String.valueOf(report.getTotalTasks()), 5),
                    center(String.valueOf(report.getCompletedTasks()), 9),
                    Project.formatPercent(report.getPercentage())
            );

            totalPercentage += report.getPercentage();
            total += report.getTotalTasks();
            completed += report.getCompletedTasks();
            inProgress += report.getInProgressTasks();
            pending += report.getPendingTasks();
        }

        System.out.println(ConsoleMenu.LINE);
        System.out.println("AVERAGE COMPLETION: " + Project.formatPercent(totalPercentage / reports.length));
        System.out.println(ConsoleMenu.LINE);

        System.out.println("\nTask Statistics:");
        System.out.println("Total: " + total);
        System.out.println("Completed: " + completed);
        System.out.println("In Progress: " + inProgress);
        System.out.println("Pending: " + pending);

        System.out.println("\nSummary:");
        for (StatusReport report : reports) {
            System.out.println(report.getSummary());
        }
    }

    private String center(String text, int width) {
        int left = (width - text.length()) / 2;
        return " ".repeat(left) + text + " ".repeat(width - text.length() - left);
    }
}
