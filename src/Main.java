import models.AdminUser;
import models.HardwareProject;
import models.RegularUser;
import models.SoftwareProject;
import models.User;
import services.ProjectService;
import utils.ConsoleMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ProjectService projectService = new ProjectService();

        User admin = new AdminUser("Admin", "admin@gmail.com");
        User josephus = new RegularUser("Josephus Mupanda", "josephus@gmail.com");

        projectService.addProject(new SoftwareProject(
                "Budget Buddy", "Personal expense and savings tracker", 8000, 3, 12, "Java"));
        projectService.addProject(new HardwareProject(
                "Smart Garden Monitor", "Soil moisture sensor that waters my plants", 5000, 2, 8,
                "Arduino sensor board"));
        projectService.addProject(new SoftwareProject(
                "Portfolio Website", "My personal website to show my projects", 3000, 1, 6,
                "HTML, CSS and JavaScript"));
        projectService.addProject(new HardwareProject(
                "Home Power Meter", "Device that tracks electricity use at home", 12000, 3, 10,
                "Energy meter module"));
        projectService.addProject(new SoftwareProject(
                "Study Planner", "Timetable and revision planner for my courses", 4500, 2, 8, "Java"));

        Scanner scanner = new Scanner(System.in);

        ConsoleMenu menu = new ConsoleMenu(
                scanner,
                projectService,
                new User[]{admin, josephus}
        );

        menu.start();

        scanner.close();
    }
}
