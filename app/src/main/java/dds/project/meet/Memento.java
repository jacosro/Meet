package dds.project.meet;

import java.sql.Timestamp;


import dds.project.meet.logic.Card;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class Memento {
    private Card mementoState;
    private Timestamp stampTime;

    public Memento (Card card) {
        this.mementoState = card;
        this.stampTime = new Timestamp(System.currentTimeMillis());
    }

    public Card getState() {
        return mementoState;
    }

    public Timestamp getTimeStamp() {
        return stampTime;
    }
}
