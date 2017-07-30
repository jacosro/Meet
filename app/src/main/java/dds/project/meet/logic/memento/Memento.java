package dds.project.meet.logic.memento;

import java.sql.Timestamp;

import dds.project.meet.logic.entities.Event;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class Memento {
    private Event mementoState;
    private Timestamp stampTime;

    public Memento (Event event) {
        this.mementoState = event;
        this.stampTime = new Timestamp(System.currentTimeMillis());
    }

    public Event getState() {
        return mementoState;
    }

    public Timestamp getTimeStamp() {
        return stampTime;
    }
}
