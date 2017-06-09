package dds.project.meet.persistence;

/**
 * Created by jacosro on 9/06/17.
 */

public interface QueryCallback<E> {

    void result(E data);
}
