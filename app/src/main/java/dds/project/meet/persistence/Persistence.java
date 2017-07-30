package dds.project.meet.persistence;

import com.google.firebase.database.FirebaseDatabase;

import dds.project.meet.persistence.dao.implementations.EventDAOImpl;
import dds.project.meet.persistence.dao.implementations.UserDAOImpl;
import dds.project.meet.persistence.dao.models.IEventDAO;
import dds.project.meet.persistence.dao.models.IUserDAO;

/**
 * Created by jacosro on 29/05/17.
 */

public class Persistence {

    public static final String USERS_KEY = "users";
    public static final String CARDS_KEY = "cards";
    public static final String ALL_USERNAMES_KEY = "allUsernames";
    public static final String CARD_USERS_KEY = "card_users";

    private static Persistence INSTANCE = null;

    public IUserDAO userDAO;
    public IEventDAO eventDAO;

    private Persistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        userDAO = new UserDAOImpl();
        eventDAO = new EventDAOImpl();
    }

    public static Persistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Persistence();
        }
        return INSTANCE;
    }

}
