package dds.project.meet.logic.commands;

import android.util.Log;

import dds.project.meet.logic.adapters.EventAdapter;
import dds.project.meet.logic.entities.Event;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class NewCardCommand implements Command {

    private Persistence mPersistence;
    private EventAdapter adapter;
    private Event event;

    public NewCardCommand(EventAdapter adapter, Event event) {
        this.adapter = adapter;
        this.event = event;
        this.mPersistence = Persistence.getInstance();
    }

    @Override
    public void execute() {
        adapter.add(event);

        mPersistence.eventDAO.addEvent(event, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {
                Log.d("NewCard", "New event added: " + data);
            }
        });
    }
}

