package dds.project.meet.logic.commands;

import android.util.Log;

import dds.project.meet.logic.adapters.CardAdapter;
import dds.project.meet.logic.entities.Card;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class NewCardCommand implements Command {

    private Persistence mPersistence;
    private CardAdapter adapter;
    private Card card;

    public NewCardCommand(CardAdapter adapter, Card card) {
        this.adapter = adapter;
        this.card = card;
        this.mPersistence = Persistence.getInstance();
    }

    @Override
    public void execute() {
        adapter.add(card);

        mPersistence.cardDAO.addCard(card, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {
                Log.d("NewCard", "New card added: " + data);
            }
        });
    }
}

