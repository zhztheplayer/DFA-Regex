package top.yatt.dfargx.automata;

/**
 * Created on 2015/5/10.
 */
public class NFAStateFactory {
    private int nextID;

    public NFAStateFactory() {
        nextID = 0;
    }

    public NFAState create() {
        return new NFAState(nextID++);
    }
}
