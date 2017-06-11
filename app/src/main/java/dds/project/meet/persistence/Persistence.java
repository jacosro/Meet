package dds.project.meet.persistence;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import dds.project.meet.logic.Card;
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
