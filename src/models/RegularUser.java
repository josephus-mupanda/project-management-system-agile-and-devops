package models;

public class RegularUser extends User {

    public RegularUser(String name, String email) {
        super(name, email, "Regular");
    }

    @Override
    public boolean canModify() {
        return false;
    }
}
