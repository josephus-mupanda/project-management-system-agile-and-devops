package models;

import interfaces.Completable;

public class Task implements Completable {

    public enum Status {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public static Status fromText(String text) {
            for (Status status : values()) {
                if (status.label.equalsIgnoreCase(text.trim())) {
                    return status;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static int nextId = 1;

    private final int id;
    private String name;
    private Status status;
    private int hours;
    private User assignedTo;

    public Task(String name, Status status, int hours) {
        this.id = nextId++;
        this.name = name;
        this.status = status;
        this.hours = hours;
    }

    @Override
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public int getId() {
        return id;
    }

    public String getDisplayId() {
        return String.format("TSK%03d", id);
    }

    public String getName() {
        return name;
    }

    public int getHours() {
        return hours;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }
}
