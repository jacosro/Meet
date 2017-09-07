package dds.project.meet.logic.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.entities.EventDAO;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by RaulCoroban on 16/04/2017.
 */

public class EventFactory {

    private static String [] names = {"Cena Montaditos", "Volley Playa", "Campeonato Fútbol", "Carrera Per la Pau", "Macro-cena ETSINF", "Al Puenting", "Comida La Vella", "Seminario Big Data", "Comic Con"};
    private static String [] times = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
    private static String [] locations = {"UPV", "Valencia", "Sydney", "Mallorca", "White House", "Big Ben", "Tour Eiffel", "Madrid", "Amsterdam", "Athens", "Paris", "Milano", "San Marino"};
    private static String [] descriptions = {"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.", " Et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."};

    public static Event getEmptyEvent() {
        return new Event();
    }

    public static Event getRandomEvent() {
        List<User> participants = new ArrayList<>();

        String time = times[(int) (Math.random() * (times.length))];
        int dateDay = (int) (Math.random() * 30);
        int dateMonth = (int) (Math.random() * 12);
        int dateYear = (int) (Math.random() * (2100 - 2018) + 2018);
        String name = names[(int) (Math.random() * (names.length))];
        String location = locations[(int) (Math.random() * (locations.length))];
        int persons = 1;
        int km = (int) (Math.random() * 500);
        String description = descriptions[(int) (Math.random() * (descriptions.length))];
        String owner = names[(int) (Math.random() * (names.length))];
        String dbKey = null;

        return getEvent(time, dateDay, dateMonth, dateYear, name, location, persons, km, description, participants, owner, dbKey);
    }

    public static Event getEvent(String time, int dateDay, int dateMonth, int dateYear, String name,
                                 String location, int persons, int km, String description, List<User> participants,
                                 String owner, String dbKey) {

        Event event = getEmptyEvent();

        event.setTime(time);
        event.setDateDay(dateDay);
        event.setDateMonth(dateMonth);
        event.setDateYear(dateYear);
        event.setName(name);
        event.setLocation(location);
        event.setPersons(persons);
        event.setKm(km);
        event.setDescription(description);
        event.setParticipants(participants);
        event.setOwner(owner);
        event.setDbKey(dbKey);

        return event;

    }

    public static Event buildEventFromDTO(EventDAO eventDAO) {
        Event res = getEmptyEvent();
        res.setDbKey(eventDAO.getDbKey());

        final List<User> participants = new ArrayList<>();
        Persistence.getInstance().userDAO.getAllUsersOfEvent(res, new QueryCallback<List<User>>() {
            @Override
            public void result(List<User> data) {
                if (data != null) {
                    Log.d("UserResult", data.toString());
                    participants.addAll(data);
                }
            }
        });

        res.setTime(eventDAO.getTime());
        res.setDateDay(eventDAO.getDateDay());
        res.setDateMonth(eventDAO.getDateMonth());
        res.setDateYear(eventDAO.getDateYear());
        res.setName(eventDAO.getName());
        res.setLocation(eventDAO.getLocation());
        res.setPersons(eventDAO.getPersons());
        res.setDescription(eventDAO.getDescription());
        res.setOwner(eventDAO.getOwner());
        res.setParticipants(participants);

        return res;
    }
}
