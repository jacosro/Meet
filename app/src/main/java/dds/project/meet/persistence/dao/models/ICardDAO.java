package dds.project.meet.persistence.dao.models;

import android.app.DownloadManager;

import java.util.Collection;

import dds.project.meet.logic.Card;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by jacosro on 9/06/17.
 */

public interface ICardDAO {

    void addCard(Card card, QueryCallback<Boolean> callback);
    void addAllCards(Collection<Card> collection);
    void removeCard(Card card, QueryCallback<Boolean> callback);
    void updateCard(Card card, QueryCallback<Boolean> callback);

    void findCardByKey(String key, QueryCallback<Card> callback);
    void getAllCards(QueryCallback<Card> callback);

}
