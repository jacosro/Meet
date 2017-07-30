package dds.project.meet.logic.entities;

import android.support.annotation.NonNull;

import java.util.List;

import dds.project.meet.persistence.entities.EventDAO;

/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class Event implements Comparable<Event> {

    private String time;
    private int dateDay;
    private int dateMonth;
    private int dateYear;
    private String name;
    private String location;
    private int persons;
    private int km;
    private String description;
    private List<User> participants;

    private String owner;
    private String dbKey;

    public Event() {

    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
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

    public List<User> getParticipants() {
        return participants;
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

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Event))
            return false;

        Event event = (Event) o;

        return dbKey.equals(event.dbKey) && compareTo(event) == 0 && name.equals(event.name) && location.equals(event.location) &&
                persons == event.persons && km == event.km && description.equals(event.getDescription()) &&
                participants.equals(event.participants) && owner.equals(event.owner);
    }

    public int compareTo(@NonNull Event event) {
        if (event.dateYear == dateYear) {
            if (event.dateMonth == dateMonth) {
                if (event.dateDay == dateDay) {
                    return time.compareTo(event.time);
                } else {
                    return dateDay - event.dateDay;
                }
            } else {
                return dateMonth - event.dateMonth;
            }
        } else {
            return dateYear - event.dateYear;
        }
    }

    public EventDAO toDTO() {
        EventDAO res = new EventDAO();

        res.setTime(time);
        res.setDateDay(dateDay);
        res.setDateMonth(dateMonth);
        res.setDateYear(dateYear);
        res.setName(name);
        res.setLocation(location);
        res.setPersons(persons);
        res.setDescription(description);
        res.setOwner(owner);
        res.setDbKey(dbKey);

        return res;
    }
}
