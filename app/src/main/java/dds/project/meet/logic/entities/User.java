package dds.project.meet.logic.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class User implements Comparable<User> {
    private String name;
    private String username;
    private String telephone;
    private String email;

    private String uid;

    public User() {

    }

    public User(String name, String username, String telephone, String email) {
        this.name = name;
        this.username = username;
        this.telephone = telephone;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("username", username);
        map.put("phone", telephone);
        map.put("uid", uid);

        return map;
    }

    @Override
    public int compareTo(User user) {
        return name.compareTo(user.name);
    }

    public String toString() {
        return String.format("User[%s, %s, %s, %s, %s]", name, telephone, username, email, uid);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof User))
            return false;

        User user = (User) o;

        return user.uid.equals(uid) &&
                user.name.equals(name) &&
                user.username.equals(username) &&
                user.telephone.equals(telephone) &&
                user.email.equals(email);
    }
}
