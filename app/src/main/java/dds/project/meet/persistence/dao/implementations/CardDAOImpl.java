package dds.project.meet.persistence.dao.implementations;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public void addCard(Card card, QueryCallback<Boolean> callback) {
        DatabaseReference ref = rootRef.child("cards");
        final String key = ref.push().getKey();
        ref.child(key).setValue(card);

        card.setDBKey(key);

        // Add to uid_cards table
        final String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();
        final String cardName = card.getName();

        rootRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);

                // Add to card_users
                rootRef.child("card_users").child(key).child(uid).setValue(username);

                // Add to user_cards
                rootRef.child("user_cards").child(uid).child(key).setValue(cardName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "AddCard failed: " + databaseError);
            }
        });

    }

    @Override
    public void addAllCards(Collection<Card> collection) {

    }

    @Override
    public void removeCard(Card card, QueryCallback<Boolean> callback) {
        String key = card.getDBKey();
        String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        if (key != null) {
            // Remove from cards
            rootRef.child("cards").child(key).removeValue();

            // Remove from card_users
            rootRef.child("card_users").child(key).removeValue();

            //Remove from user_cards
            rootRef.child("user_cards").child(uid).child(key).removeValue();
        } else {
            Log.d(TAG, "Key of Card " + card.getName() + " is null!");
        }
    }

    @Override
    public void getAllCards(final QueryCallback<Collection<Card>> callback) {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Card> list = new ArrayList<>();

                // For each card key
                for (DataSnapshot key : dataSnapshot.child("user_cards").getChildren()) {
                    Card card = dataSnapshot.child("cards").child(key.getKey()).getValue(Card.class);
                    list.add(card);
                    // todo: if I put the callback here, will it load card one by one in MainActivity?
                }

                callback.result(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getAllCards error! " + databaseError);
                callback.result(null);
            }
        });
    }
}
