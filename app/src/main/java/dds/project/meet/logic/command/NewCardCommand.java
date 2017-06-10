package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class NewCardCommand implements Command {

    private Persistence mPersistence;
    private RecyclerView.Adapter adapter;
    private List<Card> dataSet;
    private Card card;
    private int position;

    public NewCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card) {
        this(adapter, dataSet, card, -1);
    }

    public NewCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card, int position) {
        this.adapter = adapter;
        this.dataSet = dataSet;
        this.card = card;
        this.position = position;
        this.mPersistence = Persistence.getInstance();
    }

    @Override
    public void execute() {
        new AddCardCommand(adapter, dataSet, card, position).execute();

        mPersistence.cardDAO.addCard(card, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {
                // Nothing
            }
        });
    }
}

