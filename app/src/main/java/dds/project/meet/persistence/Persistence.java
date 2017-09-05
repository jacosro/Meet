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
    public static final String EVENTS_KEY = "events";
    public static final String USERNAME_UID = "username_uid";
    public static final String USERNAME_EMAIL = "username_email";
    public static final String EVENT_USERS_KEY = "event_users";


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
