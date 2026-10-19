package utils;

import models.HardwareProject;
import models.Project;
import models.SoftwareProject;
import models.User;
import services.HealthService;
import services.ProjectService;
import services.UserService;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleMenu {

    public static final String LINE = "─".repeat(80);

    private static final Logger LOG = AppLogger.get();

    private static final int BOX_WIDTH = 44;

    private final Scanner scanner;
    private final ProjectService projectService;
    private final UserService userService;
    private final HealthService healthService;
    private User currentUser;

    public ConsoleMenu(Scanner scanner, ProjectService projectService, UserService userService,
                       HealthService healthService) {
        this.scanner = scanner;
        this.projectService = projectService;
        this.userService = userService;
        this.healthService = healthService;
        this.currentUser = userService.getUsers()[0];
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

        int choice = 0;

        do {
            // Error tracking: an unexpected error is logged and the user goes back to the
            // main menu instead of the program crashing.
            try {
                displayMainMenu();
                choice = ValidationUtils.readInt(scanner, "Enter your choice: ");
                handleMainChoice(choice);
            } catch (NoSuchElementException e) {
                // The input was closed (for example at the end of a piped file).
                LOG.warning("Input closed, stopping the menu.");
                return;
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "Unexpected error in the menu", e);
                healthService.recordError();
                System.out.println("\nError: Something went wrong. It was logged; please try again.");
                choice = 0;
            }
        } while (choice != 4);
    }

    private void handleMainChoice(int choice) {
        switch (choice) {
            case 1:
                projectMenu();
                break;
            case 2:
                userMenu();
                break;
            case 3:
                healthService.displayHealth();
                LOG.info("Health check viewed: status " + healthService.getStatus());
                break;
            case 4:
                System.out.println("\nGoodbye.");
                break;
            default:
                System.out.println("Error: Invalid option. Please select 1-4.");
        }
    }

    private void displayMainMenu() {
        printHeader("PROJECT HUB");
        System.out.println("Current User: " + currentUser.getName()
                + " (" + currentUser.getRole() + ")");
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("-----------");
        System.out.println("1. Manage Projects");
        System.out.println("2. Manage Users");
        System.out.println("3. System Health");
        System.out.println("4. Exit");
        System.out.println();
    }

    private boolean requireModifyAccess(String action) {
        if (currentUser.canModify()) {
            return true;
        }
        System.out.println("Error: Access denied. Only admins can " + action + ".");
        LOG.warning("Access denied: " + currentUser.getName() + " tried to " + action);
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
            System.out.println("5. Search by Name");
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
                case 5:
                    String text = ValidationUtils.readText(
                            scanner, "Enter part of the project name: ", "Search text");
                    list = projectService.searchByName(text);
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 0-5.");
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
        LOG.info("Project created: " + project.getDisplayId() + " \"" + project.getName()
                + "\" by " + currentUser.getName());
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
        LOG.info("Project deleted: " + project.getDisplayId() + " \"" + project.getName()
                + "\" by " + currentUser.getName());
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
        LOG.info("Project updated: " + project.getDisplayId() + " \"" + project.getName()
                + "\" by " + currentUser.getName());
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

    private void userMenu() {

        int choice;

        do {
            printHeader("MANAGE USERS");
            System.out.println("Current User: " + currentUser.getName()
                    + " (" + currentUser.getRole() + ")");
            System.out.println();
            userService.displayUsers();
            System.out.println("1. Switch to another user");
            System.out.println("2. Create a new user");
            System.out.println("3. Delete a user");
            System.out.println("4. Back to Main Menu");
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
                    deleteUser();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-4.");
            }
        } while (choice != 4);
    }

    private void deleteUser() {

        if (!requireModifyAccess("delete users")) {
            return;
        }

        int id = ValidationUtils.readId(scanner, "Enter user ID to delete: ", "U");

        if (id == currentUser.getId()) {
            System.out.println("Error: You cannot delete the user you are logged in as.");
            return;
        }

        User user = userService.findUser(id);

        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        boolean confirmed = ValidationUtils.readYesNo(
                scanner, "Delete user \"" + user.getName() + "\"? (y/n): ");

        if (!confirmed) {
            System.out.println("Nothing was deleted.");
            return;
        }

        userService.deleteUser(id, currentUser);
        LOG.info("User deleted: " + user.getDisplayId() + " \"" + user.getName()
                + "\" by " + currentUser.getName());
        System.out.println("\nUser \"" + user.getName() + "\" deleted.");
    }

    private void switchUser() {

        int id = ValidationUtils.readId(scanner, "Enter user ID: ", "U");
        User user = userService.findUser(id);

        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        currentUser = user;
        LOG.info("Switched user to " + user.getDisplayId() + " \"" + user.getName() + "\"");
        System.out.println("\nNow logged in as " + user.getName() + " (" + user.getRole() + ").");
    }

    private void createUser() {

        if (!requireModifyAccess("create users")) {
            return;
        }

        String name = ValidationUtils.readText(scanner, "Enter name: ", "Name");
        String email = ValidationUtils.readEmail(scanner, "Enter email: ");

        while (userService.emailExists(email)) {
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

        User user = userService.createUser(name, email, role == 2);
        LOG.info("User created: " + user.getDisplayId() + " \"" + user.getName() + "\" ("
                + user.getRole() + ") by " + currentUser.getName());
        System.out.println("\nUser " + user.getName() + " created successfully (ID: "
                + user.getDisplayId() + ").");
    }
}
