package top.yatt.dfargx.automata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Bitmap based NFA State. This should always be used to represent each NFA States once the NFA is fully constructed.
 */
public class NFABitmapState {
    private final NFABitmapStatePack directTable;
    private final NFABitmapStateManager manager;
    private final Map<Character, NFABitmapStatePack> transitionMap = new HashMap<>();
    private final int id;

    private boolean writable = true;

    public NFABitmapState(int id, NFABitmapStateManager manager) {
        this.id = id;
        this.directTable = manager.newEmptyPack();
        this.manager = manager;
    }

    public void transitionRule(char ch, int targetID) {
        ensureWritable();
        NFABitmapStatePack pack = transitionMap.get(ch);
        if (pack == null) {
            pack = manager.newEmptyPack();
            transitionMap.put(ch, pack);
        }
        pack.addState(targetID);
    }

    public void directRule(int targetID) {
        ensureWritable();
        directTable.addState(targetID);
    }

    public NFABitmapStatePack getDirectTable() {
        return directTable;
    }

    public Map<Character, NFABitmapStatePack> getTransitionMap() {
        return Collections.unmodifiableMap(transitionMap);
    }

    public int getId() {
        return id;
    }

    public void freeze() {
        directTable.freeze();
        for (Map.Entry<Character, NFABitmapStatePack> entry : transitionMap.entrySet()) {
            entry.getValue().freeze();
        }
        writable = false;
    }

    private void ensureWritable() {
        if (!writable) {
            throw new IllegalStateException("NFAIState is not writable");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NFABitmapState nfaiState = (NFABitmapState) o;
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
