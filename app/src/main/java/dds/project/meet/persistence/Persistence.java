package dds.project.meet.persistence;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;

import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.command.Command;

/**
 * Created by jacosro on 29/05/17.
 */

public class Persistence {

    private static final String TAG = "Persistence";

    private static Persistence INSTANCE = null;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;

    private Persistence() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mFirebaseStorage = FirebaseStorage.getInstance();
    }

    public static Persistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Persistence();
        }
        return INSTANCE;
    }

    // Auth methods
    public FirebaseUser getUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    public void createNewUser(final String email, final String password, final String username, final String phone, final QueryCallback<Boolean> callback) {
        Task<AuthResult> task =
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean success = task.isSuccessful();

                if (success) {
                    writeNewUser(email, username, phone);
                }
                callback.result(success);
            }
        });
    }

    public void doLogin(final String email, final String password, final QueryCallback<Boolean> callback) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                callback.result(task.isSuccessful());
            }
        });
    }

    public void doSignOut() {
        mFirebaseAuth.signOut();
    }

    // Database methods
    private void writeNewUser(String email, String username, String phone) {
        DatabaseReference root = mFirebaseDatabase.getReference();
        String uid = getUser().getUid();

        // Add user to users
        DatabaseReference usersRef = root.child("users").child(uid);
        usersRef.child("username").setValue(username); // Add uid
        usersRef.child("email").setValue(email); // Add email
        usersRef.child("phone").setValue(phone); // Add phone number

        // Add username to allUsernames
        DatabaseReference usernamesRef = root.child("allUsernames");
        usernamesRef.child(username).setValue(uid);

    }

    public void getAllUsernames(final QueryCallback<List<String>> callback) {
        Log.d(TAG, "Getting all usernames");

        DatabaseReference ref = mFirebaseDatabase.getReference().child("allUsernames");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside of onDataChange!");
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

    public void addCard(Card card) {
        Map<String, Object> data = card.toMap();

        DatabaseReference ref = mFirebaseDatabase.getReference().child("cards");
        final String key = ref.push().getKey();
        ref.child(key).updateChildren(data);

        // Add to uid_cards table
        final String uid = getUser().getUid();
        mFirebaseDatabase.getReference().child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);

                DatabaseReference ref = mFirebaseDatabase.getReference().child("card_users");
                ref.child(key).child(uid).setValue(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        card.setDBKey(key);

    }

    public void removeCard(Card card) {
        String key = card.getDBKey();

        if (key != null) {
            DatabaseReference root = mFirebaseDatabase.getReference();

            // Remove from cards
            root.child("cards").child(key).removeValue();

            // Remove from card_users
            root.child("card_users").child(key).removeValue();
        } else {
            Log.d(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }


}
