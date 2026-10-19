package services;

import models.HardwareProject;
import models.Project;
import models.SoftwareProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectServiceTest {

    private ProjectService service;
    private Project budgetBuddy;
    private Project garden;

    @BeforeEach
    void setUp() {
        service = new ProjectService();
        budgetBuddy = new SoftwareProject("Budget Buddy", "Expense tracker", 8000, 3, 12, "Java");
        garden = new HardwareProject("Smart Garden", "Moisture sensor", 5000, 2, 8, "Arduino");
        service.addProject(budgetBuddy);
        service.addProject(garden);
    }

    @Test
    void addedProjectsAreReturned() {
        assertEquals(2, service.getProjects().length);
    }

    @Test
    void duplicateNameIsRefusedIgnoringCase() {
        Project copy = new SoftwareProject("budget buddy", "Copy", 100, 1, 1, "Java");

        assertFalse(service.addProject(copy));
        assertEquals(2, service.getProjects().length);
    }

    @Test
    void arrayGrowsWhenMoreThanTenProjectsAreAdded() {
        for (int i = 0; i < 15; i++) {
            service.addProject(new SoftwareProject("Project " + i, "d", 100, 1, 1, "Java"));
        }

        assertEquals(17, service.getProjects().length);
    }

    @Test
    void findProjectById() {
        assertSame(garden, service.findProject(garden.getId()));
        assertNull(service.findProject(9999));
    }

    @Test
    void filterByType() {
        Project[] software = service.getByType("Software");
        Project[] hardware = service.getByType("hardware");

        assertEquals(1, software.length);
        assertSame(budgetBuddy, software[0]);
        assertEquals(1, hardware.length);
        assertSame(garden, hardware[0]);
    }

    @Test
    void budgetRangeIncludesBothLimits() {
        assertEquals(2, service.getByBudgetRange(5000, 8000).length);
        assertEquals(1, service.getByBudgetRange(6000, 9000).length);
    }

    @Test
    void budgetRangeWithNoMatchReturnsEmptyArray() {
        assertEquals(0, service.getByBudgetRange(1, 10).length);
    }

    @Test
    void updateProjectChangesItsDetails() {
        boolean updated = service.updateProject(
                budgetBuddy.getId(), "  Budget Buddy Pro ", "New text", 9000, 4, 10);

        assertTrue(updated);
        assertEquals("Budget Buddy Pro", budgetBuddy.getName());
        assertEquals(9000, budgetBuddy.getBudget());
        assertEquals(4, budgetBuddy.getTeamSize());
        assertEquals(10, budgetBuddy.getDurationWeeks());
    }

    @Test
    void updateProjectCanKeepItsOwnName() {
        assertTrue(service.updateProject(budgetBuddy.getId(), "BUDGET BUDDY", "d", 100, 1, 1));
    }

    @Test
    void updateProjectRefusesNameOfAnotherProject() {
        boolean updated = service.updateProject(
                budgetBuddy.getId(), "smart garden", "d", 100, 1, 1);

        assertFalse(updated);
        assertEquals("Budget Buddy", budgetBuddy.getName());
    }

    @Test
    void updateUnknownProjectReturnsFalse() {
        assertFalse(service.updateProject(9999, "Name", "d", 100, 1, 1));
    }

    @Test
    void nameTakenByOtherIgnoresTheProjectItself() {
        assertFalse(service.nameTakenByOther("Budget Buddy", budgetBuddy.getId()));
        assertTrue(service.nameTakenByOther("Budget Buddy", garden.getId()));
    }

    @Test
    void returnedArrayIsACopy() {
        Project[] projects = service.getProjects();
        projects[0] = null;

        assertNotNull(service.getProjects()[0]);
    }
}
