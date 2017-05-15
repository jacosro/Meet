package dds.project.meet.logic.memento;

import dds.project.meet.logic.Card;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class Originator {
    public Card currentState;

    public void setState(Card s) {
        this.currentState = s;
    }

    public Card getState() {
        return currentState;
    }

    public Memento saveStateToMemento() {
        return new Memento(currentState);
    }

    public void getStateFromMemento(Memento memo) {
        currentState = memo.getState();
    }
}
