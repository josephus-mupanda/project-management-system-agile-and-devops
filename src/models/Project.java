package models;

import java.util.Arrays;

public abstract class Project {

    private static int nextId = 1;

    private final int id;
    private String name;
    private String description;
    private double budget;
    private int teamSize;
    private int durationWeeks;
    private Task[] tasks;
    private int taskCount;

    public Project(String name, String description, double budget, int teamSize, int durationWeeks) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (budget <= 0) {
            throw new IllegalArgumentException("Budget must be greater than 0.");
        }
        if (teamSize <= 0) {
            throw new IllegalArgumentException("Team size must be greater than 0.");
        }
        if (durationWeeks <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0.");
        }
        this.id = nextId++;
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.teamSize = teamSize;
        this.durationWeeks = durationWeeks;
        this.tasks = new Task[10];
    }

    public abstract String getType();

    public abstract String getProjectDetails();

    public void displayProject() {
        displayProject(true);
    }

    // Overload: the same row, with the description line optional.
    public void displayProject(boolean showDescription) {
        System.out.printf(
                "%-6s | %-20s | %-10s | %-9d | %-9s | %s%n",
                getDisplayId(), name, getType(), teamSize, "$" + getFormattedBudget(),
                formatPercent(getCompletionPercentage())
        );
        if (showDescription) {
            System.out.println("       | Description: " + description);
        }
    }

    public static String formatPercent(double value) {
        return new java.text.DecimalFormat("0.##").format(value) + "%";
    }

    public String getFormattedBudget() {
        if (budget == Math.floor(budget)) {
            return String.format("%,.0f", budget);
        }
        return String.format("%,.2f", budget);
    }

    public int getId() {
        return id;
    }

    public String getDisplayId() {
        return String.format("PRJ%03d", id);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getBudget() {
        return budget;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void addTask(Task task) {
        if (taskCount == tasks.length) {
            tasks = Arrays.copyOf(tasks, tasks.length * 2);
        }
        tasks[taskCount++] = task;
    }

    public boolean removeTask(int taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId() == taskId) {
                for (int j = i; j < taskCount - 1; j++) {
                    tasks[j] = tasks[j + 1];
                }
                tasks[--taskCount] = null;
                return true;
            }
        }
        return false;
    }

    public Task[] getTasks() {
        return Arrays.copyOf(tasks, taskCount);
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int countByStatus(Task.Status status) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public double getCompletionPercentage() {
        if (taskCount == 0) {
            return 0.0;
        }
        double percentage = (double) countByStatus(Task.Status.COMPLETED) / taskCount * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }

}
