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
import dds.project.meet.logic.CardAdapter;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class AddCardCommand implements Command {

    private CardAdapter adapter;
    private Card card;
    private int position;

    public AddCardCommand(CardAdapter adapter, Card card) {
        this(adapter, card, -1);
    }

    public AddCardCommand(CardAdapter adapter, Card card, int position) {
        this.adapter = adapter;
        this.card = card;
        this.position = position;
    }

    @Override
    public void execute() {
        adapter.add(card);
    }
}
