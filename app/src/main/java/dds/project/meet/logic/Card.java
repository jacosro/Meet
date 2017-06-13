package dds.project.meet.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class Card implements Comparable<Card> {

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

    public Card() {

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
        map.put("description", description);
        map.put("participants", participants);
        map.put("owner", owner);
        map.put("dbKey", dbKey);

        return map;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Card))
            return false;

        Card card = (Card) o;

        return dbKey.equals(card.dbKey) && compareTo(card) == 0 && name.equals(card.name) && location.equals(card.location) &&
                persons == card.persons && km == card.km;//  && image.equals(card.image);
    }

    public int compareTo(Card card) {
        if (card.dateYear == dateYear) {
            if (card.dateMonth == dateMonth) {
                if (card.dateDay == dateDay) {
                    return time.compareTo(card.time);
                } else {
                    return dateDay - card.dateDay;
                }
            } else {
                return dateMonth - card.dateMonth;
            }
        } else {
            return dateYear - card.dateYear;
        }
    }
}
