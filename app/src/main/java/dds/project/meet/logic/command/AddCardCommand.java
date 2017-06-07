package dds.project.meet.logic.command;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.Card;
import dds.project.meet.persistence.Persistence;

/**
 * Created by jacosro on 14/05/17.
 */

public class AddCardCommand implements Command {

    private Persistence mPersistence;
    private RecyclerView.Adapter adapter;
    private List<Card> dataSet;
    private Card card;
    private int position;

    public AddCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card) {
        this(adapter, dataSet, card, -1);
    }

    public AddCardCommand(RecyclerView.Adapter adapter, List<Card> dataSet, Card card, int position) {
        this.adapter = adapter;
        this.dataSet = dataSet;
        this.card = card;
        this.position = position;
        this.mPersistence = Persistence.getInstance();
    }

    @Override
    public void execute() {
        // Add card to layout
        if (position >= 0) {
            dataSet.add(position, card);
            adapter.notifyItemInserted(position);
        } else {
            dataSet.add(card);
            adapter.notifyItemInserted(adapter.getItemCount() - 1);
        }

        // Add to database
        mPersistence.addCard(card);
    }
}
