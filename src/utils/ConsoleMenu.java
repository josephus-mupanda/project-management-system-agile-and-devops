package utils;

import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.User;
import services.ProjectService;

import java.util.Arrays;
import java.util.Scanner;

public class ConsoleMenu {

    public static final String LINE = "─".repeat(80);

    private static final int BOX_WIDTH = 44;

    private final Scanner scanner;
    private final ProjectService projectService;
    private User[] users;
    private int userCount;
    private User currentUser;

    public ConsoleMenu(Scanner scanner, ProjectService projectService, User[] initialUsers) {
        this.scanner = scanner;
        this.projectService = projectService;
        this.users = Arrays.copyOf(initialUsers, Math.max(10, initialUsers.length * 2));
        this.userCount = initialUsers.length;
        this.currentUser = initialUsers[0];
    }

    public static void printHeader(String title) {
        int left = (BOX_WIDTH - title.length()) / 2;
        int right = BOX_WIDTH - title.length() - left;

        System.out.println();
        System.out.println("╔" + "═".repeat(BOX_WIDTH) + "╗");
        System.out.println("║" + " ".repeat(left) + title + " ".repeat(right) + "║");
        System.out.println("╚" + "═".repeat(BOX_WIDTH) + "╝");
        System.out.println();
    }

    public void start() {

        int choice;

        do {
            displayMainMenu();
            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    projectMenu();
                    break;
                case 2:
                    switchUserMenu();
                    break;
                case 3:
                    System.out.println("\nGoodbye.");
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-3.");
            }
        } while (choice != 3);
    }

    private void displayMainMenu() {
        printHeader("PROJECT HUB");
        System.out.println("Current User: " + currentUser.getName()
                + " (" + currentUser.getRole() + ")");
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("-----------");
        System.out.println("1. Manage Projects");
        System.out.println("2. Switch User");
        System.out.println("3. Exit");
        System.out.println();
    }

    private boolean requireModifyAccess(String action) {
        if (currentUser.canModify()) {
            return true;
        }
        System.out.println("Error: Access denied. Only admins can " + action + ".");
        return false;
    }

    // Projects

    private void projectMenu() {

        int choice;

        do {
            printHeader("MANAGE PROJECTS");
            System.out.println("1. Browse Project Catalog");
            System.out.println("2. Add New Project");
            System.out.println("3. Back to Main Menu");
            System.out.println();

            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    browseCatalog();
                    break;
                case 2:
                    addProject();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-3.");
            }
        } while (choice != 3);
    }

    private void browseCatalog() {

        while (true) {
            printHeader("PROJECT CATALOG");
            System.out.println("Filter Options:");
            System.out.println("1. View All Projects (" + projectService.getProjects().length + ")");
            System.out.println("2. Software Projects Only");
            System.out.println("3. Hardware Projects Only");
            System.out.println("4. Search by Budget Range");
            System.out.println("0. Back");
            System.out.println();

            int choice = ValidationUtils.readInt(scanner, "Enter filter choice: ");
            Project[] list;

            switch (choice) {
                case 0:
                    return;
                case 1:
                    list = projectService.getProjects();
                    break;
                case 2:
                    list = projectService.getByType("Software");
                    break;
                case 3:
                    list = projectService.getByType("Hardware");
                    break;
                case 4:
                    list = searchByBudget();
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 0-4.");
                    continue;
            }

            System.out.println();
            projectService.displayProjects(list);
            if (list.length > 0) {
                openProjectFromList();
            }
        }
    }

    private Project[] searchByBudget() {

        double min = ValidationUtils.readDouble(scanner, "Enter minimum budget: ");
        double max = ValidationUtils.readDouble(scanner, "Enter maximum budget: ");

        while (min < 0 || max < min) {
            System.out.println("Error: Invalid range. Minimum must be 0 or more and not above maximum.\n");
            min = ValidationUtils.readDouble(scanner, "Enter minimum budget: ");
            max = ValidationUtils.readDouble(scanner, "Enter maximum budget: ");
        }

        return projectService.getByBudgetRange(min, max);
    }

    private void openProjectFromList() {

        int id = ValidationUtils.readId(
                scanner, "Enter project ID to view details (or 0 to return): ", "PRJ"
        );

        if (id == 0) {
            return;
        }

        Project project = projectService.findProject(id);

        if (project == null) {
            System.out.println("Error: Project not found.");
            return;
        }

        projectDetails(project);
    }

    private void addProject() {

        printHeader("ADD NEW PROJECT");
        System.out.println("1. Software Project");
        System.out.println("2. Hardware Project");
        System.out.println();

        int type = ValidationUtils.readInt(scanner, "Choose project type: ");

        while (type != 1 && type != 2) {
            System.out.println("Error: Please choose 1 or 2.\n");
            type = ValidationUtils.readInt(scanner, "Enter again: ");
        }

        String name = ValidationUtils.readText(scanner, "Enter project name: ", "Project name");

        while (projectService.nameExists(name)) {
            System.out.println("Error: A project with this name already exists.\n");
            name = ValidationUtils.readText(scanner, "Enter again: ", "Project name");
        }

        String description = ValidationUtils.readText(scanner, "Enter description: ", "Description");
        double budget = ValidationUtils.readPositiveDouble(scanner, "Enter budget: ", "Budget");
        int teamSize = ValidationUtils.readPositiveInt(scanner, "Enter team size: ", "Team size");
        int duration = ValidationUtils.readPositiveInt(
                scanner, "Enter duration in weeks: ", "Duration");

        Project project;

        if (type == 1) {
            String techStack = ValidationUtils.readText(scanner, "Enter tech stack: ", "Tech stack");
            project = new SoftwareProject(name, description, budget, teamSize, duration, techStack);
        } else {
            String deviceType = ValidationUtils.readText(scanner, "Enter device type: ", "Device type");
            project = new HardwareProject(name, description, budget, teamSize, duration, deviceType);
        }

        projectService.addProject(project);
        System.out.println("\nProject successfully created (ID: " + project.getDisplayId() + ").");
    }

    private void projectDetails(Project project) {

        int choice;

        do {
            showProject(project);

            System.out.println("\nOptions:");
            System.out.println("1. Update Project");
            System.out.println("2. Delete Project");
            System.out.println("3. Back to Catalog");
            System.out.println();

            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    updateProject(project);
                    break;
                case 2:
                    if (deleteProject(project)) {
                        return;
                    }
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-3.");
            }
        } while (choice != 3);
    }

    // Returns true when the project was deleted, so the details screen can close.
    private boolean deleteProject(Project project) {

        if (!requireModifyAccess("delete projects")) {
            return false;
        }

        boolean confirmed = ValidationUtils.readYesNo(
                scanner, "Delete project \"" + project.getName() + "\"? (y/n): ");

        if (!confirmed) {
            System.out.println("Nothing was deleted.");
            return false;
        }

        projectService.deleteProject(project.getId());
        System.out.println("\nProject \"" + project.getName() + "\" deleted.");
        return true;
    }

    private void updateProject(Project project) {

        if (!requireModifyAccess("update projects")) {
            return;
        }

        printHeader("UPDATE PROJECT: " + project.getDisplayId());
        System.out.println("Press Enter to keep the current value shown in brackets.\n");

        String name = ValidationUtils.readTextOrKeep(
                scanner, "Name [" + project.getName() + "]: ", project.getName());

        while (projectService.nameTakenByOther(name, project.getId())) {
            System.out.println("Error: A project with this name already exists.\n");
            name = ValidationUtils.readTextOrKeep(scanner, "Enter again: ", project.getName());
        }

        String description = ValidationUtils.readTextOrKeep(
                scanner, "Description [" + project.getDescription() + "]: ", project.getDescription());
        double budget = ValidationUtils.readPositiveDoubleOrKeep(
                scanner, "Budget [" + project.getFormattedBudget() + "]: ", "Budget", project.getBudget());
        int teamSize = ValidationUtils.readPositiveIntOrKeep(
                scanner, "Team size [" + project.getTeamSize() + "]: ", "Team size", project.getTeamSize());
        int duration = ValidationUtils.readPositiveIntOrKeep(
                scanner, "Duration in weeks [" + project.getDurationWeeks() + "]: ", "Duration",
                project.getDurationWeeks());

        projectService.updateProject(project.getId(), name, description, budget, teamSize, duration);
        System.out.println("\nProject " + project.getDisplayId() + " updated successfully.");
    }

    private void showProject(Project project) {

        printHeader("PROJECT DETAILS: " + project.getDisplayId());

        System.out.println("Project Name: " + project.getName());
        System.out.println("Type: " + project.getType());
        System.out.println(project.getProjectDetails());
        System.out.println("Description: " + project.getDescription());
        System.out.println("Team Size: " + project.getTeamSize());
        System.out.println("Duration: " + project.getDurationWeeks() + " weeks");
        System.out.println("Budget: $" + project.getFormattedBudget());
    }

    // Users

    private void switchUserMenu() {

        int choice;

        do {
            printHeader("SWITCH USER");
            System.out.println("Current User: " + currentUser.getName()
                    + " (" + currentUser.getRole() + ")");
            System.out.println();
            displayUsers();
            System.out.println("1. Switch to another user");
            System.out.println("2. Create a new user");
            System.out.println("3. Back to Main Menu");
            System.out.println();

            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    switchUser();
                    break;
                case 2:
                    createUser();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-3.");
            }
        } while (choice != 3);
    }

    private void switchUser() {

        int id = ValidationUtils.readId(scanner, "Enter user ID: ", "U");
        User user = findUser(id);

        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        currentUser = user;
        System.out.println("\nNow logged in as " + user.getName() + " (" + user.getRole() + ").");
    }

    private void createUser() {

        if (!requireModifyAccess("create users")) {
            return;
        }

        String name = ValidationUtils.readText(scanner, "Enter name: ", "Name");
        String email = ValidationUtils.readEmail(scanner, "Enter email: ");

        while (emailExists(email)) {
            System.out.println("Error: A user with this email already exists.\n");
            email = ValidationUtils.readEmail(scanner, "Enter again: ");
        }

        System.out.println("1. Regular User");
        System.out.println("2. Admin User");
        int role = ValidationUtils.readInt(scanner, "Choose role: ");

        while (role != 1 && role != 2) {
            System.out.println("Error: Please choose 1 or 2.\n");
            role = ValidationUtils.readInt(scanner, "Enter again: ");
        }

        User user = addUser(name, email, role == 2);
        System.out.println("\nUser " + user.getName() + " created successfully (ID: "
                + user.getDisplayId() + ").");
    }

    private User findUser(int userId) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getId() == userId) {
                return users[i];
            }
        }
        return null;
    }

    private boolean emailExists(String email) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    private User addUser(String name, String email, boolean admin) {
        if (userCount == users.length) {
            users = Arrays.copyOf(users, users.length * 2);
        }

        User user = admin ? new AdminUser(name, email) : new RegularUser(name, email);
        users[userCount++] = user;
        return user;
    }

    private void displayUsers() {
        System.out.println(LINE);
        System.out.printf("%-4s | %-20s | %-8s | %s%n", "ID", "NAME", "ROLE", "EMAIL");
        System.out.println(LINE);

        for (int i = 0; i < userCount; i++) {
            User user = users[i];
            System.out.printf(
                    "%-4s | %-20s | %-8s | %s%n",
                    user.getDisplayId(), user.getName(), user.getRole(), user.getEmail()
            );
        }
        System.out.println(LINE);
    }
}
