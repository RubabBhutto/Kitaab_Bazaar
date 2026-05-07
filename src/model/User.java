//Nighat

package model;

import exception.InvalidUserInputException;

public class User implements Manageable {

   
    private int    id;
    private String name;
    private String phone;
    private String password;
    private String role;    
    private boolean active; 

    public User(int id, String name, String phone, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.phone    = phone;
        this.password = password;
        this.role     = role;
        this.active   = true;
    }

    public User() { this.active = true; }

    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getPhone()    { return phone; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;
    }

    public void setPhone(String phone) throws InvalidUserInputException {
        if (phone == null || !phone.matches("\\d{11}"))
            throw new InvalidUserInputException("phone",
                "Phone must be exactly 11 digits. Got: " + phone);
        this.phone = phone;
    }

    public void setPassword(String pass) {
        if (pass == null || pass.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        this.password = pass;
    }

    public void setRole(String role)   { this.role = role; }
    public void setActive(boolean a)   { this.active = a; }


    @Override
    public boolean isActive() { return active; }

    @Override
    public String getSummary() {
        return "User[" + id + "] " + name + " | Role: " + role
             + " | Phone: " + phone + " | Active: " + active;
    }

    public String getInfo() {
        return "Name: " + name + " | Role: " + role;
    }

    @Override
    public String toString() {
        return "User[id=" + id + ", name=" + name + ", role=" + role + "]";
    }
}
