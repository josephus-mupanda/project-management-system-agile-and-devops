package models;

public class StatusReport {

    private final String projectId;
    private final String projectName;
    private final int totalTasks;
    private final int completedTasks;
    private final int inProgressTasks;
    private final int pendingTasks;
    private final double percentage;

    public StatusReport(Project project) {
        this.projectId = project.getDisplayId();
        this.projectName = project.getName();
        this.totalTasks = project.getTaskCount();
        this.completedTasks = project.countByStatus(Task.Status.COMPLETED);
        this.inProgressTasks = project.countByStatus(Task.Status.IN_PROGRESS);
        this.pendingTasks = project.countByStatus(Task.Status.PENDING);
        this.percentage = project.getCompletionPercentage();
    }

    public String getSummary() {
        return projectName + " is " + Project.formatPercent(percentage) + " complete";
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getInProgressTasks() {
        return inProgressTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public double getPercentage() {
        return percentage;
    }
}
