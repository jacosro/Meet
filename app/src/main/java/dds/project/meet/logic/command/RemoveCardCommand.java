package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.persistence.Persistence;

import static dds.project.meet.presentation.MainActivity.adapterCards;

/**
 * Created by jacosro on 14/05/17.
 */

public class RemoveCardCommand implements Command {

    private RecyclerView.Adapter adapter;
    private List<Card> dataSet;
    private Card card;
    private int position;
    private Persistence mPersistence;

    public RemoveCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card) {
        this.adapter = adapter;
        this.dataSet = dataSet;
        this.card = card;
        this.position = -1;
        mPersistence = Persistence.getInstance();
    }

    public RemoveCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, int position) {
        this(adapter, dataSet, dataSet.get(position));
        this.position = position;
    }

    @Override
    public void execute() {
        if (position > -1) {
            dataSet.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            position = dataSet.indexOf(card);
            if (position > -1) {
                dataSet.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }

        // Remove from database
        mPersistence.removeCard(card);
    }
}
