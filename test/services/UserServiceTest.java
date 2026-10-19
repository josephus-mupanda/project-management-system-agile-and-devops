package services;

import models.AdminUser;
import models.RegularUser;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService service;
    private User admin;

    @BeforeEach
    void setUp() {
        service = new UserService();
        admin = new AdminUser("Admin", "admin@test.com");
        service.addUser(admin);
    }

    @Test
    void createRegularAndAdminUsers() {
        User regular = service.createUser("Reg", "reg@test.com", false);
        User other = service.createUser("Boss", "boss@test.com", true);

        assertInstanceOf(RegularUser.class, regular);
        assertInstanceOf(AdminUser.class, other);
        assertEquals(3, service.getUsers().length);
    }

    @Test
    void duplicateEmailIsRefusedIgnoringCase() {
        assertNull(service.createUser("Copy", " ADMIN@test.com ", false));
        assertFalse(service.addUser(new RegularUser("Copy", "admin@TEST.com")));
        assertEquals(1, service.getUsers().length);
    }

    @Test
    void emailExistsChecksAllUsers() {
        assertTrue(service.emailExists("admin@test.com"));
        assertFalse(service.emailExists("nobody@test.com"));
    }

    @Test
    void findUserById() {
        User regular = service.createUser("Reg", "reg@test.com", false);

        assertSame(regular, service.findUser(regular.getId()));
        assertSame(admin, service.findUser(admin.getId()));
        assertNull(service.findUser(9999));
    }

    @Test
    void arrayGrowsWhenMoreThanTenUsersAreAdded() {
        for (int i = 0; i < 12; i++) {
            assertNotNull(service.createUser("User " + i, "user" + i + "@test.com", false));
        }

        assertEquals(13, service.getUsers().length);
    }

    @Test
    void returnedArrayIsACopy() {
        User[] users = service.getUsers();
        users[0] = null;

        assertNotNull(service.getUsers()[0]);
    }
}
