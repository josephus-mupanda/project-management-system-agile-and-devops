package services;

import models.AdminUser;
import models.RegularUser;
import models.User;
import utils.ConsoleMenu;

import java.util.Arrays;

public class UserService {

    private User[] users = new User[10];
    private int count;

    public boolean addUser(User user) {
        if (emailExists(user.getEmail())) {
            return false;
        }
        if (count == users.length) {
            users = Arrays.copyOf(users, users.length * 2);
        }
        users[count++] = user;
        return true;
    }

    // Returns the new user, or null when the email is already used.
    public User createUser(String name, String email, boolean admin) {
        User user = admin ? new AdminUser(name.trim(), email.trim())
                : new RegularUser(name.trim(), email.trim());
        return addUser(user) ? user : null;
    }

    public boolean emailExists(String email) {
        for (int i = 0; i < count; i++) {
            if (users[i].getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    public User findUser(int userId) {
        for (int i = 0; i < count; i++) {
            if (users[i].getId() == userId) {
                return users[i];
            }
        }
        return null;
    }

    public User[] getUsers() {
        return Arrays.copyOf(users, count);
    }

    public void displayUsers() {
        System.out.println(ConsoleMenu.LINE);
        System.out.printf("%-4s | %-20s | %-8s | %s%n", "ID", "NAME", "ROLE", "EMAIL");
        System.out.println(ConsoleMenu.LINE);

        for (int i = 0; i < count; i++) {
            User user = users[i];
            System.out.printf(
                    "%-4s | %-20s | %-8s | %s%n",
                    user.getDisplayId(), user.getName(), user.getRole(), user.getEmail()
            );
        }
        System.out.println(ConsoleMenu.LINE);
    }
}
