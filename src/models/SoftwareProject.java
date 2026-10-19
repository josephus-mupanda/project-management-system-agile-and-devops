package models;

public class SoftwareProject extends Project {

    private String techStack;

    public SoftwareProject(String name, String description, double budget, int teamSize,
                           int durationWeeks, String techStack) {
        super(name, description, budget, teamSize, durationWeeks);
        this.techStack = techStack;
    }

    public String getTechStack() {
        return techStack;
    }

    @Override
    public String getType() {
        return "Software";
    }

    @Override
    public String getProjectDetails() {
        return "Tech Stack: " + techStack;
    }
}
