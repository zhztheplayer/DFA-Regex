package top.yatt.dfargx.automata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable NFA State. This should always be used to represent each NFA States once the NFA is fully constructed.
 */
public class NFAIState {
    private final NFAIStatePack directTable;
    private final NFAIStateManager manager;
    private final int stateCount;
    private final Map<Character, NFAIStatePack> transitionMap = new HashMap<>();
    private final int id;

    public NFAIState(int id, int stateCount, NFAIStateManager manager) {
        this.id = id;
        this.stateCount = stateCount;
        this.directTable = manager.newEmptyPack();
        this.manager = manager;
    }

    public void transitionRule(char ch, int targetID) {
        NFAIStatePack pack = transitionMap.get(ch);
        if (pack == null) {
            pack = manager.newEmptyPack();
            transitionMap.put(ch, pack);
        }
        pack.addState(targetID);
    }

    public void directRule(int targetID) {
        directTable.addState(targetID);
    }

    public NFAIStatePack getDirectTable() {
        return directTable;
    }

    public Map<Character, NFAIStatePack> getTransitionMap() {
        return transitionMap;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NFAIState nfaiState = (NFAIState) o;
        return id == nfaiState.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NFAIState{" +
                "id=" + id +
                '}';
    }
}
