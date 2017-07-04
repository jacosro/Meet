package dds.project.meet.persistence.dao.models;

import dds.project.meet.logic.entities.Card;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 9/06/17.
 */

public interface ICardDAO {

    void addCard(Card card, QueryCallback<Boolean> callback);
    void removeCard(Card card, QueryCallback<Boolean> callback);
    void updateCard(Card card, QueryCallback<Boolean> callback);

    void setListenerForUserRemoved(QueryCallback<String> callback);
    void findCardByKey(String key, QueryCallback<Card> callback);
    void getAllCards(QueryCallback<Card> callback);

}
