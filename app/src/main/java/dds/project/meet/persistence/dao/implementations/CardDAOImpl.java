package dds.project.meet.persistence.dao.implementations;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference rootRef;

    public CardDAOImpl() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }



    @Override
    public void addCard(final Card card, final QueryCallback<Boolean> callback) {
        String key = rootRef.child("cards").push().getKey();
        String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        // Set fields to card
        if (card.getOwner() == null) {
            card.setOwner(uid);
        }
        if (card.getDbKey() == null) {
            card.setDbKey(key);
        } else {
            key = card.getDbKey();
        }

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
            String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

            if (uid.equals(owner)) {
                // Remove card from card_users
                rootRef.child("card_users").child(key).removeValue();

                // Remove card from cards
                rootRef.child("cards").child(key).removeValue();
            } else {
                // Remove me from the card
                rootRef.child("card_users").child(key).child(uid).removeValue();
            }

            callback.result(true);

        } else {
            callback.result(false);
            Log.d(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }

    @Override
    public void updateCard(Card card, QueryCallback<Boolean> callback) {
        final String key = card.getDbKey();

        rootRef.child("cards").child(key).updateChildren(card.toMap());
        callback.result(true);
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

        rootRef.child("card_users").keepSynced(true);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error getting all cards: " + databaseError);
                callback.result(null);
            }
        });
    }
}
