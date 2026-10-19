package services;

import models.Project;
import utils.ConsoleMenu;

import java.util.Arrays;

public class ProjectService {

    private Project[] projects = new Project[10];
    private int count;

    public boolean addProject(Project project) {
        if (nameExists(project.getName())) {
            return false;
        }
        if (count == projects.length) {
            projects = Arrays.copyOf(projects, projects.length * 2);
        }
        projects[count++] = project;
        return true;
    }

    public boolean nameExists(String name) {
        for (int i = 0; i < count; i++) {
            if (projects[i].getName().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

    public Project findProject(int projectId) {
        for (int i = 0; i < count; i++) {
            if (projects[i].getId() == projectId) {
                return projects[i];
            }
        }
        return null;
    }

    public Project[] getProjects() {
        return Arrays.copyOf(projects, count);
    }

    public Project[] getByType(String type) {
        Project[] result = new Project[count];
        int found = 0;

        for (int i = 0; i < count; i++) {
            if (projects[i].getType().equalsIgnoreCase(type)) {
                result[found++] = projects[i];
            }
        }
        return Arrays.copyOf(result, found);
    }

    public Project[] getByBudgetRange(double min, double max) {
        Project[] result = new Project[count];
        int found = 0;

        for (int i = 0; i < count; i++) {
            double budget = projects[i].getBudget();
            if (budget >= min && budget <= max) {
                result[found++] = projects[i];
            }
        }
        return Arrays.copyOf(result, found);
    }

    public void displayProjects(Project[] list) {
        if (list.length == 0) {
            System.out.println("No projects found.");
            return;
        }

        String[] types = {"Software", "Hardware"};

        for (String type : types) {
            int found = 0;
            for (Project project : list) {
                if (project.getType().equalsIgnoreCase(type)) {
                    found++;
                }
            }
            if (found == 0) {
                continue;
            }

            System.out.println(type + " Projects (" + found + ")");
            System.out.println(ConsoleMenu.LINE);
            System.out.printf(
                    "%-6s | %-20s | %-10s | %-9s | %-9s | %s%n",
                    "ID", "PROJECT NAME", "TYPE", "TEAM SIZE", "BUDGET", "DURATION"
            );
            System.out.println(ConsoleMenu.LINE);

            for (Project project : list) {
                if (project.getType().equalsIgnoreCase(type)) {
                    project.displayProject();
                    System.out.println(ConsoleMenu.LINE);
                }
            }
            System.out.println();
        }
        System.out.println("Total projects: " + list.length);
    }
}
