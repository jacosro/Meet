package dds.project.meet.persistence.dao.implementations;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dds.project.meet.logic.entities.Card;
import dds.project.meet.logic.entities.User;
import dds.project.meet.logic.util.CardFactory;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.dao.models.ICardDAO;
import dds.project.meet.persistence.entities.CardDTO;
import dds.project.meet.persistence.util.QueryCallback;

import static dds.project.meet.persistence.Persistence.*;
/**
 * Created by jacosro on 9/06/17.
 */

public class CardDAOImpl implements ICardDAO {

    private static final String TAG = "CardDAO";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference rootRef;

    private QueryCallback<String> onUserRemovedCallback = null;

    public CardDAOImpl() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }



    @Override
    public void addCard(final Card card, final QueryCallback<Boolean> callback) {
        String key = rootRef.child(CARDS_KEY).push().getKey();
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
        rootRef.child(CARDS_KEY).child(key).setValue(card.toDTO());

        Map<String, Object> map = new HashMap<String, Object>(card.getParticipants().size());

        for (User user : card.getParticipants()) {
            map.put(user.getUid(), user.getUsername());
        }
        rootRef.child(CARD_USERS_KEY).child(key).setValue(map);

        callback.result(true);

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
                rootRef.child(CARD_USERS_KEY).child(key).removeValue();

                // Remove card from cards
                rootRef.child(CARDS_KEY).child(key).removeValue();
            } else {
                // Remove me from the card
                rootRef.child(CARD_USERS_KEY).child(key).child(uid).removeValue();
            }

            callback.result(true);

        } else {
            callback.result(false);
            Log.e(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }

    @Override
    public void updateCard(Card card, QueryCallback<Boolean> callback) {
        final String key = card.getDbKey();

        Map<String, Object> map = new HashMap<String, Object>(card.getParticipants().size());

        for (User user : card.getParticipants()) {
            map.put(user.getUid(), user.getUsername());
        }
        rootRef.child(CARD_USERS_KEY).child(key).setValue(map);
        rootRef.child(CARDS_KEY).child(key).updateChildren(card.toDTO().toMap());
        callback.result(true);
    }

    @Override
    public void setListenerForUserRemoved(QueryCallback<String> callback) {
        onUserRemovedCallback = callback;
    }

    @Override
    public void findCardByKey(final String key, final QueryCallback<Card> callback) {
        rootRef.child(CARDS_KEY).orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CardDTO card = dataSnapshot.child(key).getValue(CardDTO.class);
                callback.result(CardFactory.getCardFromDTO(card));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "findCardByKey: " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void getAllCards(final QueryCallback<Card> callback) {
        final String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        rootRef.child(CARD_USERS_KEY).keepSynced(true);
       /*
        rootRef.child(CARD_USERS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot card : dataSnapshot.getChildren()) {
                    String key = card.getKey();
                    if (card.child(uid).exists()) {
                        findCardByKey(key, callback);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getAllCards: " + databaseError);
                callback.result(null);
            }
        });
*/
        rootRef.child(CARD_USERS_KEY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child(uid).exists()) {
                    findCardByKey(dataSnapshot.getKey(), callback);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                String cardKey = null;

                if (dataSnapshot.child(uid).exists()) {
                    cardKey = key;
                } else if (uid.equals(key)) {
                    cardKey = dataSnapshot.getRef().getParent().getKey();
                }

                if (onUserRemovedCallback != null) {
                    onUserRemovedCallback.result(cardKey);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getAllCards.ChildEventListener: " + databaseError);
                callback.result(null);
            }
        });
    }
}
