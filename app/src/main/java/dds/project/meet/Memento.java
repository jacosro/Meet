package dds.project.meet;

import dds.project.meet.logic.Card;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class Memento {
    private Card mementoState;

    public Memento (Card card) {
        this.mementoState = card;
    }

    public Card getState() {
        return mementoState;
    }
}
