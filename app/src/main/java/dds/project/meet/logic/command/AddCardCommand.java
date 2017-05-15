package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.Card;

/**
 * Created by jacosro on 14/05/17.
 */

public class AddCardCommand implements Command {

    private RecyclerView.Adapter adapter;
    private List<Card> dataSet;
    private Card card;
    private int position;

    public AddCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card) {
        this.adapter = adapter;
        this.dataSet = dataSet;
        this.card = card;
        this.position = -1;
    }

    public AddCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card, int position) {
        this(adapter, dataSet, card);
        this.position = position;
    }

    @Override
    public void execute() {
        if (position > -1) {
            dataSet.add(position, card);
            adapter.notifyItemInserted(position);
        } else {
            dataSet.add(card);
            adapter.notifyItemInserted(adapter.getItemCount() - 1);
        }
    }
}
