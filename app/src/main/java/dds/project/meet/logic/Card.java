package dds.project.meet.logic;

import android.net.Uri;

import java.util.HashMap;
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
    private Uri image;

    private String owner;
    private String dbKey;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
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

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Card))
            return false;

        Card card = (Card) o;

        return dbKey.equals(card.dbKey) & compareTo(card) == 0 & name.equals(card.name) & location.equals(card.location) &
                persons == card.persons & km == card.km & image.equals(card.image);
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
