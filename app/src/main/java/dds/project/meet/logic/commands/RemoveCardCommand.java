package dds.project.meet.logic.commands;

import android.util.Log;

import dds.project.meet.logic.adapters.CardAdapter;
import dds.project.meet.logic.entities.Card;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class RemoveCardCommand implements Command {

    private CardAdapter adapter;
    private Card card;
    private Persistence mPersistence;

    public RemoveCardCommand(CardAdapter adapter, Card card) {
        this.adapter = adapter;
        this.card = card;
        mPersistence = Persistence.getInstance();
    }

    public RemoveCardCommand(CardAdapter adapter, int position) {
        this(adapter, adapter.get(position));
    }

    @Override
    public void execute() {
        adapter.remove(card);

        // Remove from database
        mPersistence.cardDAO.removeCard(card, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {
                Log.d("RemoveCard", "Remove card: " + data);
            }
        });
    }
}
