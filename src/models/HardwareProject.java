package models;

public class HardwareProject extends Project {

    private String deviceType;

    public HardwareProject(String name, String description, double budget, int teamSize,
                           int durationWeeks, String deviceType) {
        super(name, description, budget, teamSize, durationWeeks);
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String getType() {
        return "Hardware";
    }

    @Override
    public String getProjectDetails() {
        return "Device Type: " + deviceType;
    }
}
