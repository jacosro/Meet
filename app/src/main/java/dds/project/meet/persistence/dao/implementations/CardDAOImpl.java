package dds.project.meet.persistence.dao.implementations;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;
import dds.project.meet.persistence.dao.models.ICardDAO;

/**
 * Created by jacosro on 9/06/17.
 */

public class CardDAOImpl implements ICardDAO {

    private static final String TAG = "CardDAO";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference rootRef;

    public CardDAOImpl() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }



    @Override
    public void addCard(final Card card, final QueryCallback<Boolean> callback) {
        final String key = rootRef.child("cards").push().getKey();
        final String uid = Persistence.getInstance().userDAO.getCurrentFirebaseUser().getUid();

        // Set fields to card
        card.setOwner(uid);
        card.setDbKey(key);

        // Add to cards
        rootRef.child("cards").child(key).setValue(card);

        Log.d(TAG, "Added to cards table");
        Map<String, Object> map = new HashMap<String, Object>(card.getParticipants().size());

        for (User user : card.getParticipants()) {
            map.put(user.getUid(), user.getUsername());
        }
        rootRef.child("card_users").child(key).setValue(map);

        Log.d(TAG, "Added to card_users table");

        callback.result(true);

        /*
        StorageReference storageRef = mFirebaseStorage.getReference();
        storageRef.child(key).putFile(card.getImage()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(TAG, "Upload image: " + task.isSuccessful());
            }
        });
        */

    }

    @Override
    public void addAllCards(Collection<Card> collection) {

    }

    @Override
    public void removeCard(Card card, QueryCallback<Boolean> callback) {
        Log.d(TAG + "::removeCard", "Card: " + card);
        String key = card.getDbKey();
        String owner = card.getOwner();

        if (key != null) {
            // Remove from cards
            rootRef.child("cards").child(key).removeValue();

            // Remove from card_users
            rootRef.child("card_users").child(key).removeValue();

            callback.result(true);

        } else {
            callback.result(false);
            Log.d(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }

    @Override
    public void findCardByKey(final String key, final QueryCallback<Card> callback) {
        rootRef.child("cards").orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Card card = dataSnapshot.child(key).getValue(Card.class);
                card.setDbKey(key);
                Log.d(TAG, "Got card: " + card);
                callback.result(card);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error getting card by key: " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void getAllCards(final QueryCallback<Card> callback) {
        Log.d(TAG, "Getting all cards");
        final String uid = Persistence.getInstance().userDAO.getCurrentFirebaseUser().getUid();
        Log.d(TAG, "uid = " + uid);

        rootRef.child("card_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot card : dataSnapshot.getChildren()) {
                    String key = card.getKey();
                    Log.d(TAG, key);
                    if (card.child(uid).exists()) {
                        findCardByKey(key, callback);
                    }

                }
                callback.result(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error getting all cards: " + databaseError);
                callback.result(null);
            }
        });
    }
}
