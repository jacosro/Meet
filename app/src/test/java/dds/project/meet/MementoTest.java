package dds.project.meet;

import org.junit.Test;

import dds.project.meet.logic.memento.CareTaker;
import dds.project.meet.logic.memento.Memento;
import dds.project.meet.logic.memento.Originator;
import dds.project.meet.logic.util.CardFactory;

import static junit.framework.Assert.assertEquals;

/**
 * Created by jacosro on 13/06/17.
 */

public class MementoTest {

    @Test
    public void putCard() {
        CardEvent cardEvent = CardFactory.getRandomCard();

        Originator originator = new Originator();
        originator.setState(cardEvent);

        Memento memento = originator.saveStateToMemento();

        CareTaker careTaker = new CareTaker();
        careTaker.add(memento);

        assertEquals(cardEvent, originator.getState());
        assertEquals(1, careTaker.getMemoListSize());
    }

    @Test
    public void removeCard() {
        CardEvent cardEvent = CardFactory.getRandomCard();

        Originator originator = new Originator();
        originator.setState(cardEvent);

        Memento memento = originator.saveStateToMemento();

        CareTaker careTaker = new CareTaker();
        careTaker.add(memento);

        careTaker.undo();

        assertEquals(0, careTaker.getMemoListSize());
    }

}
