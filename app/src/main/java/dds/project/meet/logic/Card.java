package dds.project.meet.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class Card {
    private String time;
    private int dateDay;
    private int dateMonth;
    private int dateYear;
    private String name;
    private String location;
    private int persons;
    private int km;
    private String image;

    private String DBKey;

    public Card() {

    }

    public Card(String time, int dateDay, int dateMonth, int dateYear, String name, String location, int persons, int km) {
        this.time = time;
        this.dateDay = dateDay;
        this.dateMonth = dateMonth;
        this.dateYear = dateYear;
        this.name = name;
        this.location = location;
        this.persons = persons;
        this.km = km;
    }

    public String getDBKey() {
        return DBKey;
    }

    public void setDBKey(String DBKey) {
        this.DBKey = DBKey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDateDay() {
        return dateDay;
    }

    public void setDateDay(int dateDay) {
        this.dateDay = dateDay;
    }

    public int getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(int dateMonth) {
        this.dateMonth = dateMonth;
    }

    public int getDateYear() {
        return dateYear;
    }

    public void setDateYear(int dateYear) {
        this.dateYear = dateYear;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("time", time);
        map.put("dateDay", dateDay);
        map.put("dateMonth", dateMonth);
        map.put("dateYear", dateYear);
        map.put("name", name);
        map.put("location", location);
        map.put("persons", persons);
        map.put("km", km);
        map.put("image", image);

        return map;
    }

    public String toString() {
        return name;
    }
}
