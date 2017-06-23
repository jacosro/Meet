package dds.project.meet.persistence.dao.models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.entities.Card;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 9/06/17.
 */

public interface IUserDAO {

    FirebaseUser getCurrentFirebaseUser();
    User getCurrentUser();
    void setCurrentUser(QueryCallback<User> callback);
    void updateName(String name);

    void createNewUser(User user, String password, QueryCallback<Boolean> callback);
    void doLogin(String email, String password, QueryCallback<Boolean> callback);
    void doSignOut();

    void getUserImage(QueryCallback<Uri> callback);
    void updateUserImage(Uri image, QueryCallback<Boolean> callback);

    void findUserByUid(String uid, QueryCallback<User> callback);
    void findUserByUsername(String username, QueryCallback<User> callback);
    void findUserByEmail(String email, QueryCallback<User> callback);

    void removeUserFromCard(Card card, User user, QueryCallback<Boolean> callback);

    void getAllUsers(QueryCallback<Collection<User>> callback);
    void getAllUsersOfCard(Card card, QueryCallback<List<User>> callback);
    void getAllUsernames(QueryCallback<Collection<String>> callback);
    void getAllPhoneNumbers(QueryCallback<Collection<String>> callback);
}
