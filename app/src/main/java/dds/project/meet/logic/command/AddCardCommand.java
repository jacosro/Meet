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

    private FirebaseDatabase database;
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
        this.database = Persistence.getDB();
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

        // Add card to database
        int id = 0;
        Log.d("AddCardCommand", "Adding to database");
        database.getReference().child("cards").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    Log.d("AddCardCommand", "mutable data is null. Setting value to 1");
                    mutableData.setValue(1);
                } else {
                    int count = mutableData.getValue(Integer.class);
                    mutableData.setValue(count + 1);
                    Log.d("AddCardCommand", "mutable data is " + count + ". Setting value to "+ (count + 1));
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                if (success) {
                    Log.d("AddCardCommand", dataSnapshot.getValue().toString());
                } else {
                    Log.d("AddCardCommand", databaseError.getMessage());
                    Log.d("AddCardCommand", "Menuda puta mierda");
                }
            }
        });
    }
}
