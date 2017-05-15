package dds.project.meet.logic.memento;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaulCoroban on 13/05/2017.
 */

public class CareTaker {
    private List<Memento> memoList = new ArrayList<Memento>();

    public void add(Memento m) {
        memoList.add(m);
    }

    public Memento get(int index) {
        return memoList.get(index);
    }
}