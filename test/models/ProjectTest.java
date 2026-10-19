package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectTest {

    @Test
    void softwareProjectHasSoftwareTypeAndTechStack() {
        Project project = new SoftwareProject("App", "Mobile app", 1000, 2, 4, "Java");

        assertEquals("Software", project.getType());
        assertEquals("Tech Stack: Java", project.getProjectDetails());
    }

    @Test
    void hardwareProjectHasHardwareTypeAndDeviceType() {
        Project project = new HardwareProject("Sensor", "Soil sensor", 500, 1, 2, "Arduino");

        assertEquals("Hardware", project.getType());
        assertEquals("Device Type: Arduino", project.getProjectDetails());
    }

    @Test
    void eachProjectGetsADifferentId() {
        Project first = new SoftwareProject("First", "d", 100, 1, 1, "Java");
        Project second = new SoftwareProject("Second", "d", 100, 1, 1, "Java");

        assertNotEquals(first.getId(), second.getId());
        assertTrue(first.getDisplayId().startsWith("PRJ"));
    }

    @Test
    void emptyNameIsRefused() {
        assertThrows(IllegalArgumentException.class,
                () -> new SoftwareProject("  ", "d", 100, 1, 1, "Java"));
    }

    @Test
    void budgetTeamSizeAndDurationMustBePositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new SoftwareProject("A", "d", 0, 1, 1, "Java"));
        assertThrows(IllegalArgumentException.class,
                () -> new SoftwareProject("A", "d", 100, 0, 1, "Java"));
        assertThrows(IllegalArgumentException.class,
                () -> new SoftwareProject("A", "d", 100, 1, -2, "Java"));
    }

    @Test
    void budgetIsFormattedWithoutDecimalsWhenWhole() {
        Project whole = new SoftwareProject("Whole", "d", 8000, 1, 1, "Java");
        Project decimal = new SoftwareProject("Decimal", "d", 99.5, 1, 1, "Java");

        assertEquals("8,000", whole.getFormattedBudget());
        assertEquals("99.50", decimal.getFormattedBudget());
    }

    @Test
    void updateDetailsChangesAllValues() {
        Project project = new SoftwareProject("Old", "Old text", 100, 1, 1, "Java");

        project.updateDetails("New", "New text", 250, 4, 6);

        assertEquals("New", project.getName());
        assertEquals("New text", project.getDescription());
        assertEquals(250, project.getBudget());
        assertEquals(4, project.getTeamSize());
        assertEquals(6, project.getDurationWeeks());
    }

    @Test
    void invalidUpdateChangesNothing() {
        Project project = new SoftwareProject("Keep", "Text", 100, 1, 1, "Java");

        assertThrows(IllegalArgumentException.class,
                () -> project.updateDetails("Changed", "Text", 0, 1, 1));

        assertEquals("Keep", project.getName());
        assertEquals(100, project.getBudget());
    }

    @Test
    void onlyAdminsCanModify() {
        assertTrue(new AdminUser("Admin", "admin@test.com").canModify());
        assertFalse(new RegularUser("Reg", "reg@test.com").canModify());
    }
}
