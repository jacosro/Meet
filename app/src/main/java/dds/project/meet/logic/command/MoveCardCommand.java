package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import dds.project.meet.logic.Card;

/**
 * Created by jacosro on 14/05/17.
 */

public class MoveCardCommand implements Command {

    private RecyclerView.Adapter adapter;
    private List<Card> dataSet;
    private Card card;
    private int position0;
    private int position1;

    public MoveCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card, int position0, int position1) {
        if (position0 < 0) {
            throw new IllegalArgumentException("Position0 must be >= 0");
        }
        if (position1 < 0) {
            throw new IllegalArgumentException("Position1 must be >= 0");
        }
        this.adapter = adapter;
        this.dataSet = dataSet;
        this.card = card;
        this.position0 = position0;
        this.position1 = position1;
    }

    public MoveCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, int position0, int position1) {
        this(adapter, dataSet, dataSet.get(position0), position0, position1);
    }

    @Override
    public void execute() {
        dataSet.remove(position0);
        dataSet.add(position1, card);
        adapter.notifyItemMoved(position0, position1);
    }
}
