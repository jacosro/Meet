package dds.project.meet.logic.memento;

import dds.project.meet.logic.entities.Event;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class Originator {
    public Event currentState;

    public void setState(Event s) {
        this.currentState = s;
    }

    public Event getState() {
        return currentState;
    }

    public Memento saveStateToMemento() {
        return new Memento(currentState);
    }

    public void getStateFromMemento(Memento memo) {
        currentState = memo.getState();
    }
}
