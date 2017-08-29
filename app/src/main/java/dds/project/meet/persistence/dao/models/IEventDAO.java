package dds.project.meet.persistence.dao.models;

import java.util.Collection;
import java.util.List;

import dds.project.meet.logic.entities.Event;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 9/06/17.
 */

public interface IEventDAO {

    void addEvent(Event event, QueryCallback<Boolean> callback);
    void removeEvent(Event event, QueryCallback<Boolean> callback);
    void updateEvent(Event event, QueryCallback<Boolean> callback);

    void setListenerForUserRemoved(QueryCallback<String> callback);
    void findEventByKey(String key, QueryCallback<Event> callback);
    void getAllEvents(QueryCallback<List<Event>> callback);
    void setListenerForNewEvents(QueryCallback<Event> callback);

}
