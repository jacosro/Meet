package dds.project.meet.persistence.dao.implementations;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;

import dds.project.meet.logic.Card;
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
    public void addCard(Card card, final QueryCallback<Boolean> callback) {
        DatabaseReference ref = rootRef.child("cards");
        final String key = ref.push().getKey();
        final String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        card.setOwner(uid);

        ref.child(key).setValue(card);

        card.setDbKey(key);

        // Add to uid_cards table
        rootRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);

                // Add to card_users
                rootRef.child("card_users").child(key).child(uid).setValue(username);

                callback.result(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(false);
                Log.d(TAG, "AddCard failed: " + databaseError);
            }
        });

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
    public void getCardByKey(final String key, final QueryCallback<Card> callback) {
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
        final String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();
        Log.d(TAG, "uid = " + uid);

        rootRef.child("card_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot card : dataSnapshot.getChildren()) {
                    String key = card.getKey();
                    Log.d(TAG, key);
                    if (card.child(uid).exists()) {
                        getCardByKey(key, callback);
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
