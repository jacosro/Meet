package dds.project.meet.logic;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class User implements Comparable<User> {
    private String name;
    private String username;
    private String telephone;
    private String email;

    public User(String name, String username, String telephone, String email) {
        this.name = name;
        this.username = username;
        this.telephone = telephone;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public int compareTo(User user) {
        return name.compareTo(user.name);
    }
}