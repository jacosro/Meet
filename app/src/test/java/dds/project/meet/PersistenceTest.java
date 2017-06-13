package dds.project.meet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardFactory;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by jacosro on 12/06/17.
 */

public class PersistenceTest {

    Persistence mPersistence;

    @BeforeClass
    public void setmPersistence() {
        mPersistence = Persistence.getInstance();
    }

    @Test
    public void addCardTest() {
        Card card = CardFactory.getRandomCard();

        mPersistence.cardDAO.addCard(card, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {

            }
        });
    }
}
