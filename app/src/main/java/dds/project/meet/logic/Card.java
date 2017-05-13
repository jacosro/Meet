package dds.project.meet.logic;

/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class Card {
    private String time;
    private String date;
    private String name;
    private String location;
    private int persons;
    private int km;
    private String image;

    public Card(String time, String date, String name, String location, int persons, int km) {
        this.time = time;
        this.date = date;
        this.name = name;
        this.location = location;
        this.persons = persons;
        this.km = km;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
