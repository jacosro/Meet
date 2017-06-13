package dds.project.meet.logic.commands;

import dds.project.meet.logic.adapters.CardAdapter;
import dds.project.meet.logic.entities.Card;

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
