package utils;

import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.Task;
import models.User;
import services.ProjectService;
import services.ReportService;
import services.TaskService;

import java.util.Arrays;
import java.util.Scanner;

public class ConsoleMenu {

    public static final String LINE = "─".repeat(80);

    private static final int BOX_WIDTH = 44;

    private final Scanner scanner;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ReportService reportService;
    private User[] users;
    private int userCount;
    private User currentUser;

    public ConsoleMenu(
            Scanner scanner,
            ProjectService projectService,
            TaskService taskService,
            ReportService reportService,
            User[] initialUsers
    ) {
        this.scanner = scanner;
        this.projectService = projectService;
        this.taskService = taskService;
        this.reportService = reportService;
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
                    taskMenu();
                    break;
                case 3:
                    reportService.displayProjectReport();
                    break;
                case 4:
                    switchUserMenu();
                    break;
                case 5:
                    System.out.println("\nGoodbye.");
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-5.");
            }
        } while (choice != 5);
    }

    private void displayMainMenu() {
        printHeader("JAVA PROJECT MANAGEMENT SYSTEM");
        System.out.println("Current User: " + currentUser.getName()
                + " (" + currentUser.getRole() + ")");
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("-----------");
        System.out.println("1. Manage Projects");
        System.out.println("2. Manage Tasks");
        System.out.println("3. View Status Reports");
        System.out.println("4. Switch User");
        System.out.println("5. Exit");
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
            System.out.println("1. Add New Task");
            System.out.println("2. Update Task Status");
            System.out.println("3. Remove Task");
            System.out.println("4. Back to Main Menu");
            System.out.println();

            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addTask(project);
                    break;
                case 2:
                    updateTaskStatus(project);
                    break;
                case 3:
                    removeTask(project);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-4.");
            }
        } while (choice != 4);
    }

    private void showProject(Project project) {

        printHeader("PROJECT DETAILS: " + project.getDisplayId());

        System.out.println("Project Name: " + project.getName());
        System.out.println("Type: " + project.getType());
        System.out.println(project.getProjectDetails());
        System.out.println("Team Size: " + project.getTeamSize());
        System.out.println("Duration: " + project.getDurationWeeks() + " weeks");
        System.out.println("Budget: $" + project.getFormattedBudget());
        System.out.println("\nAssociated Tasks:");
        taskService.displayTasks(project);
        System.out.println("Completion Rate: " + Project.formatPercent(project.getCompletionPercentage()));
    }

    // Tasks

    private void taskMenu() {

        int choice;

        do {
            printHeader("MANAGE TASKS");
            System.out.println("1. Add New Task");
            System.out.println("2. View Tasks by Project");
            System.out.println("3. Update Task Status");
            System.out.println("4. Remove Task");
            System.out.println("5. Calculate Project Completion");
            System.out.println("6. Back to Main Menu");
            System.out.println();

            choice = ValidationUtils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addTask(null);
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    updateTaskStatus(null);
                    break;
                case 4:
                    removeTask(null);
                    break;
                case 5:
                    calculateCompletion();
                    break;
                case 6:
                    break;
                default:
                    System.out.println("Error: Invalid option. Please select 1-6.");
            }
        } while (choice != 6);
    }

    private void calculateCompletion() {

        projectService.displayProjects(projectService.getProjects());

        Project project = askForProject("Enter project ID: ");
        if (project == null) {
            return;
        }

        System.out.println("\nProject: " + project.getName());
        System.out.println("Total tasks: " + project.getTaskCount());
        System.out.println("Completed tasks: " + project.countByStatus(Task.Status.COMPLETED));
        System.out.println("Pending tasks: " + project.countByStatus(Task.Status.PENDING));
        System.out.println("In progress tasks: " + project.countByStatus(Task.Status.IN_PROGRESS));
        System.out.printf("Completion: %.2f%%%n", project.getCompletionPercentage());
    }

    private void viewTasks() {

        projectService.displayProjects(projectService.getProjects());

        Project project = askForProject("Enter project ID: ");
        if (project == null) {
            return;
        }

        System.out.println("\nProject: " + project.getName());
        taskService.displayTasks(project);
        System.out.println("Completion Rate: " + Project.formatPercent(project.getCompletionPercentage()));
    }

    // A null scope means the project is asked for, as in the add task screen.
    private void addTask(Project scope) {

        printHeader("ADD NEW TASK");

        String name = ValidationUtils.readText(scanner, "Enter task name: ", "Task name");

        Project project = scope;
        if (project == null) {
            project = askForProject("Enter assigned project ID: ");
            if (project == null) {
                return;
            }
        }

        Task.Status status = ValidationUtils.readStatus(
                scanner, "Enter initial status (Pending/In Progress/Completed): "
        );
        int hours = ValidationUtils.readPositiveInt(scanner, "Enter estimated hours: ", "Hours");

        User assignee = null;
        System.out.println();
        displayUsers();
        int userId = ValidationUtils.readId(
                scanner, "Enter assigned user ID (or 0 for nobody): ", "U"
        );
        while (userId != 0 && findUser(userId) == null) {
            System.out.println("Error: User not found.\n");
            userId = ValidationUtils.readId(scanner, "Enter again (or 0 for nobody): ", "U");
        }
        if (userId != 0) {
            assignee = findUser(userId);
        }

        if (taskService.addTask(project, name, status, hours, assignee)) {
            System.out.println("\nTask \"" + name + "\" added successfully to Project "
                    + project.getDisplayId() + "!");
        } else {
            System.out.println("Error: A task with this name already exists in the project.");
        }
    }

    private void updateTaskStatus(Project scope) {

        if (!requireModifyAccess("update tasks")) {
            return;
        }

        int taskId = ValidationUtils.readId(scanner, "Enter task ID: ", "TSK");
        Project owner = ownerOf(scope, taskId);

        if (owner == null) {
            System.out.println("Error: Task not found.");
            return;
        }

        Task task = taskService.findTask(owner, taskId);
        Task.Status status = ValidationUtils.readStatus(scanner, "Enter new status: ");
        task.setStatus(status);
        System.out.println("\nTask \"" + task.getName() + "\" marked as " + status + ".");
    }

    private void removeTask(Project scope) {

        if (!requireModifyAccess("delete tasks")) {
            return;
        }

        int taskId = ValidationUtils.readId(scanner, "Enter task ID: ", "TSK");
        Project owner = ownerOf(scope, taskId);

        if (owner == null) {
            System.out.println("Error: Task not found.");
            return;
        }

        Task task = taskService.findTask(owner, taskId);
        taskService.removeTask(owner, taskId);
        System.out.println("\nTask \"" + task.getName() + "\" removed from Project "
                + owner.getDisplayId() + ".");
    }

    private Project askForProject(String message) {

        int id = ValidationUtils.readId(scanner, message, "PRJ");
        Project project = projectService.findProject(id);

        if (project == null) {
            System.out.println("Error: Project not found.");
        }
        return project;
    }

    // Returns the project holding the task, looking only in the scope when one is given.
    private Project ownerOf(Project scope, int taskId) {

        if (scope != null) {
            return taskService.findTask(scope, taskId) != null ? scope : null;
        }

        for (Project project : projectService.getProjects()) {
            if (taskService.findTask(project, taskId) != null) {
                return project;
            }
        }
        return null;
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
