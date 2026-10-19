package models;

public class AdminUser extends User {

    public AdminUser(String name, String email) {
        super(name, email, "Admin");
    }

    @Override
    public boolean canModify() {
        return true;
    }
}
