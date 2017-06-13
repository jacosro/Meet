package dds.project.meet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dds.project.meet.logic.entities.Card;
import dds.project.meet.logic.util.CardFactory;
import dds.project.meet.logic.entities.User;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by jacosro on 13/06/17.
 */

public class CardFactoryTest {

    @Test
    public void getCardTest() {
        Card card = new Card();

        String time = "00:00";
        int dateDay = 1;
        int dateMonth = 1;
        int dateYear = 1970;
        String name = "Card";
        String location = "Valencia";
        int persons = 10;
        int km = 1;
        String description = "Card Description";
        List<User> participants = new ArrayList<>();
        String owner = "me";
        String dbKey = "dbkey";

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

        Card factoryCard = CardFactory.getCard(time, dateDay, dateMonth, dateYear, name, location,
                persons, km, description, participants, owner, dbKey);


        assertEquals(card, factoryCard);

        assertEquals(time, factoryCard.getTime());
        assertEquals(dateDay, factoryCard.getDateDay());
        assertEquals(dateMonth, factoryCard.getDateMonth());
        assertEquals(dateYear, factoryCard.getDateYear());
        assertEquals(name, factoryCard.getName());
        assertEquals(location, factoryCard.getLocation());
        assertEquals(km, factoryCard.getKm());
        assertEquals(description, factoryCard.getDescription());
        assertEquals(participants, factoryCard.getParticipants());
        assertEquals(owner, factoryCard.getOwner());
        assertEquals(dbKey, factoryCard.getDbKey());


    }

    @Test
    public void getRandomCardTest() {
        Card oneCard = CardFactory.getRandomCard();
        Card anotherCard = CardFactory.getRandomCard();

        assertNotSame(oneCard, anotherCard);
    }
}
