package models;

public abstract class Project {

    private static int nextId = 1;

    private final int id;
    private String name;
    private String description;
    private double budget;
    private int teamSize;
    private int durationWeeks;

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
    }

    public abstract String getType();

    public abstract String getProjectDetails();

    public void displayProject() {
        displayProject(true);
    }

    // Overload: the same row, with the description line optional.
    public void displayProject(boolean showDescription) {
        System.out.printf(
                "%-6s | %-20s | %-10s | %-9d | %-9s | %d weeks%n",
                getDisplayId(), name, getType(), teamSize, "$" + getFormattedBudget(), durationWeeks
        );
        if (showDescription) {
            System.out.println("       | Description: " + description);
        }
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
}
