package dds.project.meet.persistence.dao.implementations;

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

    public UserDAOImpl() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    @Override
    public void createNewUser(final String email, String password, final String username, final String phone, final QueryCallback<Boolean> callback) {
        Log.d(TAG + "::createNewUser", "Start");
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean success = task.isSuccessful();
                        Log.d(TAG + "::createNewUser", "Create new user successfully: " + success);

                        if (success) {
                            DatabaseReference root = mFirebaseDatabase.getReference();
                            String uid = getCurrentUser().getUid();

                            // Add user to users
                            DatabaseReference usersRef = root.child("users").child(uid);
                            usersRef.child("username").setValue(username); // Add uid
                            usersRef.child("email").setValue(email); // Add email
                            usersRef.child("phone").setValue(phone); // Add phone number

                            // Add username to allUsernames
                            DatabaseReference usernamesRef = root.child("allUsernames");
                            usernamesRef.child(username).setValue(uid);
                        }
                        callback.result(success);
                    }
                });
    }

    @Override
    public void doLogin(final String email, final String password, final QueryCallback<Boolean> callback) {
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
    public void getUserByUid(final String uid, final QueryCallback<User> callback) {
        mFirebaseDatabase.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(uid).getValue(User.class);
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
    public void getUserByUsername(final String username, final QueryCallback<User> callback) {
        mFirebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.child("allUsernames").child(username).getValue(String.class);
                User user = dataSnapshot.child("users").child(uid).getValue(User.class);
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
                    Object phone = user.child("phone").getValue();
                    Log.d(TAG, "Phone: " + phone);
                    res.add(phone.toString());
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
