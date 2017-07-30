package dds.project.meet.logic.commands;

import android.util.Log;

import dds.project.meet.logic.adapters.EventAdapter;
import dds.project.meet.logic.entities.Event;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 14/05/17.
 */

public class RemoveCardCommand implements Command {

    private EventAdapter adapter;
    private Event event;
    private Persistence mPersistence;

    public RemoveCardCommand(EventAdapter adapter, Event event) {
        this.adapter = adapter;
        this.event = event;
        mPersistence = Persistence.getInstance();
    }

    public RemoveCardCommand(EventAdapter adapter, int position) {
        this(adapter, adapter.get(position));
    }

    @Override
    public void execute() {
        adapter.remove(event);

        // Remove from database
        mPersistence.eventDAO.removeEvent(event, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean data) {
                Log.d("RemoveCard", "Remove event: " + data);
            }
        });
    }
}
