package dds.project.meet.logic.commands;

import dds.project.meet.logic.adapters.EventAdapter;
import dds.project.meet.logic.entities.Event;

/**
 * Created by jacosro on 14/05/17.
 */

public class AddCardCommand implements Command {

    private EventAdapter adapter;
    private Event event;
    private int position;

    public AddCardCommand(EventAdapter adapter, Event event) {
        this(adapter, event, -1);
    }

    public AddCardCommand(EventAdapter adapter, Event event, int position) {
        this.adapter = adapter;
        this.event = event;
        this.position = position;
    }

    @Override
    public void execute() {
        adapter.add(event);
    }
}
