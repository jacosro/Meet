package dds.project.meet.persistence.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacosro on 30/06/17.
 */

public class EventDAO {

    private String time;
    private int dateDay;
    private int dateMonth;
    private int dateYear;
    private String name;
    private String location;
    private int persons;
    private String description;

    private String owner;
    private String dbKey;

    public EventDAO() {

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDateMonth(int dateMonth) {
        this.dateMonth = dateMonth;
    }

    public void setDateDay(int dateDay) {
        this.dateDay = dateDay;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getTime() {
        return time;
    }

    public int getDateDay() {
        return dateDay;
    }

    public void setDateYear(int dateYear) {
        this.dateYear = dateYear;
    }

    public int getDateMonth() {
        return dateMonth;
    }

    public int getDateYear() {
        return dateYear;
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

    public String toString() {
        return name;
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
        map.put("description", description);
        map.put("owner", owner);
        map.put("dbKey", dbKey);

        return map;
    }
}
