package dds.project.meet.persistence.util;

/**
 * Created by jacosro on 9/06/17.
 */

public interface QueryCallback<E> {

    class EmptyCallBack<E> implements QueryCallback<E> {

        public EmptyCallBack() {}

        @Override
        public void result(E data) {
            // Empty callback
        }
    }

    void result(E data);

}
