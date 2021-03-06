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

import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.logic.util.EventFactory;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.dao.models.IEventDAO;
import dds.project.meet.persistence.entities.EventDAO;
import dds.project.meet.persistence.util.QueryCallback;

import static dds.project.meet.persistence.Persistence.*;
/**
 * Created by jacosro on 9/06/17.
 */

public class EventDAOImpl implements IEventDAO {

    private static final String TAG = "CardDAO";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference rootRef;

    private QueryCallback<String> onUserRemovedCallback = null;

    public EventDAOImpl() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = mFirebaseDatabase.getReference();
    }



    @Override
    public void addEvent(final Event event, final QueryCallback<Boolean> callback) {
        String key = rootRef.child(EVENTS_KEY).push().getKey();
        String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        // Set fields to event
        if (event.getOwner() == null) {
            event.setOwner(uid);
        }
        if (event.getDbKey() == null) {
            event.setDbKey(key);
        } else {
            key = event.getDbKey();
        }

        // Add to cards
        rootRef.child(EVENTS_KEY).child(key).setValue(event.toDTO());

        Map<String, Object> map = new HashMap<String, Object>(event.getParticipants().size());

        for (User user : event.getParticipants()) {
            map.put(user.getUid(), user.getUsername());
        }
        rootRef.child(EVENT_USERS_KEY).child(key).setValue(map);

        callback.result(true);

    }

    @Override
    public void removeEvent(Event event, QueryCallback<Boolean> callback) {
        Log.d(TAG + "::removeEvent", "Event: " + event);
        String key = event.getDbKey();
        String owner = event.getOwner();

        if (key != null) {
            String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

            if (uid.equals(owner)) {
                // Remove event from card_users
                rootRef.child(EVENT_USERS_KEY).child(key).removeValue();

                // Remove event from cards
                rootRef.child(EVENTS_KEY).child(key).removeValue();
            } else {
                // Remove me from the event
                rootRef.child(EVENT_USERS_KEY).child(key).child(uid).removeValue();
            }

            callback.result(true);

        } else {
            callback.result(false);
            Log.e(TAG, "Key of Event " + event.getName() + " is null!");
        }
    }

    @Override
    public void updateEvent(Event event, QueryCallback<Boolean> callback) {
        final String key = event.getDbKey();

        Map<String, Object> map = new HashMap<String, Object>(event.getParticipants().size());

        for (User user : event.getParticipants()) {
            map.put(user.getUid(), user.getUsername());
        }
        rootRef.child(EVENT_USERS_KEY).child(key).setValue(map);
        rootRef.child(EVENTS_KEY).child(key).updateChildren(event.toDTO().toMap());
        callback.result(true);
    }

    @Override
    public void setListenerForUserRemoved(QueryCallback<String> callback) {
        onUserRemovedCallback = callback;
    }

    @Override
    public void findEventByKey(final String key, final QueryCallback<Event> callback) {
        rootRef.child(EVENTS_KEY).orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventDAO card = dataSnapshot.child(key).getValue(EventDAO.class);
                callback.result(EventFactory.buildEventFromDTO(card));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "findEventByKey: " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void getAllEvents(final QueryCallback<Event> callback) {
        final String uid = Persistence.getInstance().userDAO.getCurrentUser().getUid();

        rootRef.child(EVENT_USERS_KEY).keepSynced(true);
       /*
        rootRef.child(EVENT_USERS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot card : dataSnapshot.getChildren()) {
                    String key = card.getKey();
                    if (card.child(uid).exists()) {
                        findEventByKey(key, callback);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getAllEvents: " + databaseError);
                callback.result(null);
            }
        });
*/
        rootRef.child(EVENT_USERS_KEY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child(uid).exists()) {
                    findEventByKey(dataSnapshot.getKey(), callback);
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
                Log.e(TAG, "getAllEvents.ChildEventListener: " + databaseError);
                callback.result(null);
            }
        });
    }
}
