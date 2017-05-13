package dds.project.meet.logic;

import dds.project.meet.logic.Card;

/**
 * Created by RaulCoroban on 16/04/2017.
 */

public class CardFactory {

    public Card getCard(){
        return new Card("00:00", "21 MARCH" , "Something goo", "Road to Nowhere", 0, 0);
    }

    public static Card getCard(String time, String date, String name, String location, int persons, int km){
        return new Card(time, date, name, location, persons, km);
    }
}
