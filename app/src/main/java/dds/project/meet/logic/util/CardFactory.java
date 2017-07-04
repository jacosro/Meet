package dds.project.meet.logic.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dds.project.meet.logic.entities.Card;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.entities.CardDTO;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by RaulCoroban on 16/04/2017.
 */

public class CardFactory {

    private static String [] names = {"Cena Montaditos", "Volley Playa", "Campeonato Fútbol", "Carrera Per la Pau", "Macro-cena ETSINF", "Al Puenting", "Comida La Vella", "Seminario Big Data", "Comic Con"};
    private static String [] times = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
    private static String [] locations = {"UPV", "Valencia", "Sydney", "Mallorca", "White House", "Big Ben", "Tour Eiffel", "Madrid", "Amsterdam", "Athens", "Paris", "Milano", "San Marino"};
    private static String [] descriptions = {"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.", " Et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."};

    public static Card getEmptyCard() {
        return new Card();
    }

    public static Card getRandomCard() {
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

        return getCard(time, dateDay, dateMonth, dateYear, name, location, persons, km, description, participants, owner, dbKey);
    }

    public static Card getCard(String time, int dateDay, int dateMonth, int dateYear, String name,
                               String location, int persons, int km, String description, List<User> participants,
                               String owner, String dbKey) {

        Card card = new Card();

        card.setTime(time);
        card.setDateDay(dateDay);
        card.setDateMonth(dateMonth);
        card.setDateYear(dateYear);
        card.setName(name);
        card.setLocation(location);
        card.setPersons(persons);
        card.setKm(km);
        card.setDescription(description);
        card.setParticipants(participants);
        card.setOwner(owner);
        card.setDbKey(dbKey);

        return card;

    }

    public static Card getCardFromDTO(CardDTO cardDTO) {
        Card res = new Card();
        res.setDbKey(cardDTO.getDbKey());

        final List<User> participants = new ArrayList<>();
        Persistence.getInstance().userDAO.getAllUsersOfCard(res, new QueryCallback<List<User>>() {
            @Override
            public void result(List<User> data) {
                if (data != null) {
                    participants.addAll(data);
                }
            }
        });

        res.setTime(cardDTO.getTime());
        res.setDateDay(cardDTO.getDateDay());
        res.setDateMonth(cardDTO.getDateMonth());
        res.setDateYear(cardDTO.getDateYear());
        res.setName(cardDTO.getName());
        res.setLocation(cardDTO.getLocation());
        res.setPersons(cardDTO.getPersons());
        res.setDescription(cardDTO.getDescription());
        res.setOwner(cardDTO.getOwner());
        res.setParticipants(participants);

        return res;
    }
}
