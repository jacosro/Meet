package dds.project.meet.persistence.dao.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by jacosro on 9/06/17.
 */

public interface IUserDAO {

    FirebaseUser getCurrentUser();
    void createNewUser(String email, String password, String username, String phone, QueryCallback<Boolean> callback);
    void doLogin(String email, String password, QueryCallback<Boolean> callback);
    void doSignOut();

    void getUserByUid(String uid, QueryCallback<User> callback);
    void getUserByUsername(String username, QueryCallback<User> callback);

    void removeUserFromCard(Card card, User user, QueryCallback<Boolean> callback);

    void getAllUsersOfCard(Card card, QueryCallback<List<User>> callback);
    void getAllUsernames(QueryCallback<Collection<String>> callback);
    void getAllPhoneNumbers(QueryCallback<Collection<String>> callback);
}
