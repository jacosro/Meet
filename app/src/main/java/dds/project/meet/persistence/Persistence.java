package dds.project.meet.persistence;

import com.google.firebase.database.FirebaseDatabase;

import dds.project.meet.persistence.dao.implementations.CardDAOImpl;
import dds.project.meet.persistence.dao.implementations.UserDAOImpl;
import dds.project.meet.persistence.dao.models.ICardDAO;
import dds.project.meet.persistence.dao.models.IUserDAO;

/**
 * Created by jacosro on 29/05/17.
 */

public class Persistence {

    private static Persistence INSTANCE = null;

    public IUserDAO userDAO;
    public ICardDAO cardDAO;

    private Persistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        userDAO = new UserDAOImpl();
        cardDAO = new CardDAOImpl();
    }

    public static Persistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Persistence();
        }
        return INSTANCE;
    }

}
