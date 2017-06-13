package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardAdapter;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

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

