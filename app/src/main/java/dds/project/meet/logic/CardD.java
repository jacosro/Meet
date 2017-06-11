package dds.project.meet.logic;


import java.sql.Timestamp;

/**
 * Created by RaulCoroban on 11/06/2017.
 */

public class CardD extends Card {
    private Timestamp stampTime;

    public CardD (Card c, Timestamp t) {
        super(c.getTime(), c.getDateDay(), c.getDateMonth(), c.getDateYear(), c.getName(), c.getLocation(), c.getPersons(), c.getKm());
        this.stampTime = t;
    }

    public Timestamp getStampTime() {
        return stampTime;
    }

    public void setStampTime(Timestamp stampTime) {
        this.stampTime = stampTime;
    }
}
