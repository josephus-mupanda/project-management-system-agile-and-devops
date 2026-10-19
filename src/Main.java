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
import utils.ConsoleMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ProjectService projectService = new ProjectService();
        TaskService taskService = new TaskService();
        ReportService reportService = new ReportService(projectService);

        User admin = new AdminUser("Admin", "admin@gmail.com");
        User josephus = new RegularUser("Josephus Mupanda", "josephus@gmail.com");

        Project budgetBuddy = new SoftwareProject(
                "Budget Buddy", "Personal expense and savings tracker", 8000, 3, 12, "Java");
        Project gardenMonitor = new HardwareProject(
                "Smart Garden Monitor", "Soil moisture sensor that waters my plants", 5000, 2, 8,
                "Arduino sensor board");
        Project portfolio = new SoftwareProject(
                "Portfolio Website", "My personal website to show my projects", 3000, 1, 6,
                "HTML, CSS and JavaScript");
        Project powerMeter = new HardwareProject(
                "Home Power Meter", "Device that tracks electricity use at home", 12000, 3, 10,
                "Energy meter module");
        Project studyPlanner = new SoftwareProject(
                "Study Planner", "Timetable and revision planner for my courses", 4500, 2, 8, "Java");

        projectService.addProject(budgetBuddy);
        projectService.addProject(gardenMonitor);
        projectService.addProject(portfolio);
        projectService.addProject(powerMeter);
        projectService.addProject(studyPlanner);

        taskService.addTask(budgetBuddy, "Design Database", Task.Status.COMPLETED, 6, null);
        taskService.addTask(budgetBuddy, "Build Expense Screen", Task.Status.IN_PROGRESS, 10, josephus);
        taskService.addTask(budgetBuddy, "Write Unit Tests", Task.Status.PENDING, 5, null);
        taskService.addTask(gardenMonitor, "Wire Moisture Sensor", Task.Status.COMPLETED, 4, null);
        taskService.addTask(gardenMonitor, "Calibrate Water Pump", Task.Status.PENDING, 3, null);

        Scanner scanner = new Scanner(System.in);

        ConsoleMenu menu = new ConsoleMenu(
                scanner,
                projectService,
                taskService,
                reportService,
                new User[]{admin, josephus}
        );

        menu.start();

        scanner.close();
    }
}
