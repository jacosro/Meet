package dds.project.meet.persistence;

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
        mFirebaseStorage = FirebaseStorage.getInstance();
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

    public Task<AuthResult> createNewUser(final String email, final String password, final String username, final String phone) {
        Task<AuthResult> task = mFirebaseAuth.createUserWithEmailAndPassword(email, password);

        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                writeNewUser(email, username, phone);
            }
        });
        return task;
    }

    public Task<AuthResult> doLogin(String email, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void doSignOut() {
        mFirebaseAuth.signOut();
    }

    // Database methods
    private void writeNewUser(String email, String username, String phone) {
        DatabaseReference root = mFirebaseDatabase.getReference();
        String uid = getUser().getUid();

        // Add user to users
        DatabaseReference users = root.child("users").child(uid);
        users.child("username").setValue(username); // Add uid
        users.child("email").setValue(email); // Add email
        users.child("phone").setValue(phone); // Add phone number

        // Add username to allUsernames
        DatabaseReference usernames = root.child("allUsernames");
        usernames.child(username).setValue(uid);

    }

    /*     Can't check if an username exists :/      */
    public boolean isUsernameTaken(final String newUsername) {
        Log.d(TAG, "Defining ref");
        DatabaseReference ref = mFirebaseDatabase.getReference().child("allUsernames");
        return ref.child(newUsername) != null; // Returns the url. Never is null

        /*
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside of onDataChange!");
                Iterable<DataSnapshot> uids = dataSnapshot.getChildren();
                Log.d(TAG, uids.toString());
                for (DataSnapshot uid : uids) {
                    Log.d(TAG, uid.toString());
                    if (uid.getValue(String.class).equals(newUsername)) {
                        res[0] = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
        return res[0];
        */
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
                DatabaseReference ref = mFirebaseDatabase.getReference().child("user_card");
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
            DatabaseReference ref = mFirebaseDatabase.getReference().child("cards");
            ref.child(key).removeValue();

            String uid = getUser().getUid();
            ref = mFirebaseDatabase.getReference().child("user_card");
            ref.child(key).removeValue();
        } else {
            Log.d(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }
}
