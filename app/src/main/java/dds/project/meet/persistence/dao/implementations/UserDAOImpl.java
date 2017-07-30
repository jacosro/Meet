package dds.project.meet.persistence.dao.implementations;

import android.net.Uri;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.dao.models.IUserDAO;
import dds.project.meet.persistence.util.QueryCallback;

import static dds.project.meet.persistence.Persistence.*;

/**
 * Created by jacosro on 9/06/17.
 */

public class UserDAOImpl implements IUserDAO {

    private static final String TAG = "userDAO";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;

    private DatabaseReference rootRef;

    private StorageReference storageRootRef;

    private User mUser;

    public UserDAOImpl() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();


        rootRef = mFirebaseDatabase.getReference();
        storageRootRef = mFirebaseStorage.getReferenceFromUrl("gs://meet-b1515.appspot.com/");
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
    public void updateName(String name) {
        mUser.setName(name);
        String uid = mUser.getUid();
        rootRef.child(USERS_KEY).child(uid).child("name").setValue(name);
    }

    @Override
    public void createNewUser(final User user, String password, final QueryCallback<Boolean> callback) {
        mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean success = task.isSuccessful();

                        if (success) {
                            String uid = getCurrentFirebaseUser().getUid();
                            user.setUid(uid);

                            // Add default_sidebar_user_icon to users
                            rootRef.child(USERS_KEY).child(uid).setValue(user);

                            // Add username to allUsernames
                            rootRef.child(ALL_USERNAMES_KEY).child(user.getUsername()).setValue(uid);

                        } else {
                            Log.e(TAG, "createNewUser: " + task.getException());
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
    public void getEmailFromUsername(String username, final QueryCallback<String> callback) {
        rootRef.child(ALL_USERNAMES_KEY).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getValue(String.class);

                if (uid == null) {
                    callback.result(null);
                } else {
                    rootRef.child(USERS_KEY).child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String email = dataSnapshot.getValue(String.class);
                            callback.result(email);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "getEmailFromUsername.getEmail: " + databaseError);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getEmailFromUsername: " + databaseError);
            }
        });
    }

    @Override
    public void doSignOut() {
        mFirebaseAuth.signOut();
    }

    @Override
    public void getUserImage(final QueryCallback<Uri> callback) {
        StorageReference ref = storageRootRef.child(mUser.getUsername() + ".jpg");

        try {
            final File localFile = File.createTempFile(mUser.getUsername(), ".jpg");

            ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        callback.result(Uri.fromFile(localFile));
                    } else {
                        callback.result(null);
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "getUserImage: " + e);
            callback.result(null);
        }
    }

    @Override
    public void updateUserImage(final Uri image, final QueryCallback<Boolean> callback) {
        storageRootRef.child(mUser.getUsername() + ".jpg").delete();
        storageRootRef.child(mUser.getUsername() + ".jpg")
                .putFile(image)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        callback.result(task.isSuccessful());
                    }
                });
    }

    @Override
    public void findUserByUid(final String uid, final QueryCallback<User> callback) {
        rootRef.child(USERS_KEY).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "GetUserByUid failed! " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void findUserByUsername(final String username, final QueryCallback<User> callback) {
        rootRef.child(USERS_KEY).orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "findUserByUsername failed! " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void findUserByEmail(String email, final QueryCallback<User> callback) {
        rootRef.child(USERS_KEY).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.result(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "findByEmail error " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void removeUserFromEvent(Event event, User user, final QueryCallback<Boolean> callback) {
        final String key = event.getDbKey();
        String uid = user.getUid();

        if (key != null && uid != null) {
            rootRef.child(EVENT_USERS_KEY).child(key).child(user.getUid()).removeValue();

            rootRef.child(EVENTS_KEY).child(key).child("persons").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    int persons = mutableData.getValue(Integer.class);
                    mutableData.setValue(persons - 1);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (!b) {
                        Log.e(TAG, "Error on transaction: " + databaseError);
                    }
                }
            });

            callback.result(true);
        } else {
            Log.e(TAG, "Event key or uid null!");
            callback.result(false);
        }
    }

    @Override
    public void getAllUsers(final QueryCallback<Collection<User>> callback) {

        rootRef.child(USERS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> res = new ArrayList<User>();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    res.add(user.getValue(User.class));
                }
                callback.result(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(null);
                Log.e(TAG, "getAllUsers: " + databaseError);
            }
        });
    }

    @Override
    public void getAllUsersOfCard(Event event, final QueryCallback<User> callback) {
        String key = event.getDbKey();

        rootRef.child(EVENT_USERS_KEY).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long count = dataSnapshot.getChildrenCount();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    findUserByUid(user.getKey(), callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getAllUsersOfCard: " + databaseError);
                callback.result(null);
            }
        });
    }

    @Override
    public void getAllUsernames(final QueryCallback<Collection<String>> callback) {
        rootRef.child(ALL_USERNAMES_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e(TAG, "Error getting usernames!" + databaseError.toString());
                callback.result(null);
            }
        });
    }

    @Override
    public void getAllPhoneNumbers(final QueryCallback<Collection<String>> callback) {
        rootRef.child(USERS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> res = new ArrayList<String>();

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String phone = user.child("telephone").getValue(String.class);
                    res.add(phone);
                }
                callback.result(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(null);
                Log.e(TAG, "getAllPhoneNumbers: " + databaseError);
            }
        });
    }
}
