package models;

public abstract class User {

    private static int nextId = 1;

    private final int id;
    private String name;
    private String email;
    private String role;

    public User(String name, String email, String role) {
        this.id = nextId++;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public abstract boolean canModify();

    public int getId() {
        return id;
    }

    public String getDisplayId() {
        return String.format("U%03d", id);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
