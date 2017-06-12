package dds.project.meet.logic;

/**
 * Created by RaulCoroban on 16/04/2017.
 */

public class CardFactory {

    private static String [] names = {"Cena Montaditos", "Volley Playa", "Campeonato Fútbol", "Carrera Per la Pau", "Macro-cena ETSINF", "Al Puenting", "Comida La Vella", "Seminario Big Data", "Comic Con"};
    private static String [] times = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
    private static String [] locations = {"UPV", "Valencia", "Sydney", "Mallorca", "White House", "Big Ben", "Tour Eiffel", "Madrid", "Amsterdam", "Athens", "Paris", "Milano", "San Marino"};
    private static String [] descriptions = {"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.", " Et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."};

    public static Card getCard(){
        return new Card("00:00", 12 , 5, 2016, "Something goo", "Road to Nowhere", 0, 0, "la");
    }

    public static Card getRandomCard() {
        return new Card(times[(int) Math.random() * (times.length)], (int) Math.random() * 30, (int) Math.random() * 12, (int) Math.random() * (2100 - 2018) + 2018, names[(int)Math.random() * (names.length)],locations[(int)Math.random() * (locations.length)] , (int) Math.random() * (45 - 1) + 1, (int) Math.random() * 500, descriptions[(int)Math.random() * (descriptions.length)]);
    }

    public static Card getCard(String time, int dateDay, int dateMonth, int dateYear, String name, String location, int persons, int km, String desc){
        return new Card(time, dateDay, dateMonth, dateYear , name, location, persons, km, desc);
    }
}
