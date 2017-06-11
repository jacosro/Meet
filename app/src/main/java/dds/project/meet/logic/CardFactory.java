package dds.project.meet.logic;

/**
 * Created by RaulCoroban on 16/04/2017.
 */

public class CardFactory {

    public static Card getCard(){
        return new Card("00:00", 12 , 5, 2016, "Something goo", "Road to Nowhere", 0, 0, "la");
    }

    public static Card getCard(String time, int dateDay, int dateMonth, int dateYear, String name, String location, int persons, int km){
        return new Card(time, dateDay, dateMonth, dateYear , name, location, persons, km, "la" );
    }
}
