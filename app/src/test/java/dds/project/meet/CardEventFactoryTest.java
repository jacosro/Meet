package dds.project.meet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dds.project.meet.logic.util.CardFactory;
import dds.project.meet.logic.entities.User;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by jacosro on 13/06/17.
 */

public class CardEventFactoryTest {

    @Test
    public void getCardTest() {
        CardEvent cardEvent = new CardEvent();

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

        cardEvent.setTime(time);
        cardEvent.setDateDay(dateDay);
        cardEvent.setDateMonth(dateMonth);
        cardEvent.setDateYear(dateYear);
        cardEvent.setName(name);
        cardEvent.setLocation(location);
        cardEvent.setPersons(persons);
        cardEvent.setKm(km);
        cardEvent.setDescription(description);
        cardEvent.setParticipants(participants);
        cardEvent.setOwner(owner);
        cardEvent.setDbKey(dbKey);

        CardEvent factoryCardEvent = CardFactory.getCard(time, dateDay, dateMonth, dateYear, name, location,
                persons, km, description, participants, owner, dbKey);


        assertEquals(cardEvent, factoryCardEvent);

        assertEquals(time, factoryCardEvent.getTime());
        assertEquals(dateDay, factoryCardEvent.getDateDay());
        assertEquals(dateMonth, factoryCardEvent.getDateMonth());
        assertEquals(dateYear, factoryCardEvent.getDateYear());
        assertEquals(name, factoryCardEvent.getName());
        assertEquals(location, factoryCardEvent.getLocation());
        assertEquals(km, factoryCardEvent.getKm());
        assertEquals(description, factoryCardEvent.getDescription());
        assertEquals(participants, factoryCardEvent.getParticipants());
        assertEquals(owner, factoryCardEvent.getOwner());
        assertEquals(dbKey, factoryCardEvent.getDbKey());


    }

    @Test
    public void getRandomCardTest() {
        CardEvent oneCardEvent = CardFactory.getRandomCard();
        CardEvent anotherCardEvent = CardFactory.getRandomCard();

        assertNotSame(oneCardEvent, anotherCardEvent);
    }
}
