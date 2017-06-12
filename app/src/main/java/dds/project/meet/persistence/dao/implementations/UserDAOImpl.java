package dds.project.meet.persistence.dao.implementations;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.QueryCallback;
import dds.project.meet.persistence.dao.models.IUserDAO;

/**
 * Created by jacosro on 9/06/17.
 */

public class UserDAOImpl implements IUserDAO {

    private static final String TAG = "userDAO";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference rootRef;

    private User mUser;

    public UserDAOImpl() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }

    @Override
    public FirebaseUser getCurrentFirebaseUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    private void setCurrentUser(User user) {
        mUser = user;
    }

    @Override
    public User getCurrentUser() {
        return mUser;
    }

    @Override
    public void setCurrentUser(final QueryCallback<User> callback) {
        if (getCurrentFirebaseUser() != null) {
            findUserByUid(getCurrentFirebaseUser().getUid(), new QueryCallback<User>() {
                @Override
                public void result(User data) {
                    setCurrentUser(data);
                    callback.result(data);
                }
            });
        }
    }

    @Override
    public void createNewUser(final User user, String password, final QueryCallback<Boolean> callback) {
        Log.d(TAG + "::createNewUser", "Start");
        mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean success = task.isSuccessful();
                        Log.d(TAG + "::createNewUser", "Create new user successfully: " + success);

                        if (success) {
                            String uid = getCurrentFirebaseUser().getUid();
                            user.setUid(uid);

                            // Add user to users
                            rootRef.child("users").child(uid).setValue(user);

                            // Add username to allUsernames
                            rootRef.child("allUsernames").child(user.getUsername()).setValue(uid);

                        }
                        callback.result(success);
                    }
                });
    }

    @Override
    public void doLogin(String email, String password, final QueryCallback<Boolean> callback) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                callback.result(task.isSuccessful());
            }
        });
    }

    @Override
    public void doSignOut() {
        mFirebaseAuth.signOut();
    }

    @Override
    public void findUserByUid(final String uid, final QueryCallback<User> callback) {
        rootRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "GetUserByUid failed! " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void findUserByUsername(final String username, final QueryCallback<User> callback) {
        rootRef.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "GetUserByUid failed! " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void findUserByEmail(String email, final QueryCallback<User> callback) {
        rootRef.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "findByEmail error " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void removeUserFromCard(Card card, User user, final QueryCallback<Boolean> callback) {
        String key = card.getDbKey();
        String uid = user.getUid();

        if (key != null && uid != null) {
            rootRef.child("card_users").child(key).child(user.getUid()).removeValue();
            callback.result(true);
        } else {
            Log.d(TAG, "Card key or uid null!");
            callback.result(false);
        }
    }

    @Override
    public void getAllUsers(final QueryCallback<Collection<User>> callback) {

        rootRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> res = new ArrayList<User>();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    res.add(user.getValue(User.class));
                }
                Log.d(TAG, res.toString());
                callback.result(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(new ArrayList<User>());
            }
        });
    }

    @Override
    public void getAllUsersOfCard(Card card, QueryCallback<List<User>> callback) {

    }

    @Override
    public void getAllUsernames(final QueryCallback<Collection<String>> callback) {
        Log.d(TAG + "::getAllUsername", "Getting all usernames");

        DatabaseReference ref = mFirebaseDatabase.getReference().child("allUsernames");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> res = new ArrayList<>();

                for (DataSnapshot username : dataSnapshot.getChildren()) {
                    res.add(username.getKey());
                }

                callback.result(res); // "Return" of the method
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error getting usernames!" + databaseError.toString());
            }
        });
    }

    @Override
    public void getAllPhoneNumbers(final QueryCallback<Collection<String>> callback) {
        rootRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> res = new ArrayList<String>();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String phone = user.child("telephone").getValue(String.class);
                    Log.d(TAG, "Phone: " + phone);
                    res.add(phone);
                }
                callback.result(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(new ArrayList<String>());
            }
        });
    }
}
