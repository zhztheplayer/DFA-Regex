package top.yatt.dfargx.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Use this NFAState container to gain faster hashing/comparison from a hashset of NFAStates.
 */
public class NFAIStatePack {
    private final long[] stateBitmap;
    private final NFAIStateManager manager;

    public NFAIStatePack(NFAIStateManager manager) {
        stateBitmap = newStateBitmap(manager.stateCount());
        this.manager = manager;
    }

    private long[] newStateBitmap(int stateCount) {
        int length = (stateCount - 1) / 63 + 1; // ceiling
        return new long[length];
    }

    public void addState(int stateID) {
        int bucket = stateID / 63;
        int offset = stateID % 63;
        long f = 1L << offset;
        stateBitmap[bucket] |= f;
    }

    public void addAll(NFAIStatePack other) {
        if (manager != other.manager) {
            throw new IllegalArgumentException("MFAIStatePack to union must share the same manager with the union target");
        }
        for (int i = 0; i < stateBitmap.length; i++) {
            stateBitmap[i] |= other.stateBitmap[i];
        }
    }

    public boolean contains(int stateID) {
        int bucket = stateID / 63;
        int offset = stateID % 63;
        long f = 1L << offset;
        return (stateBitmap[bucket] & f) != 0L;
    }
    
    public boolean isEmpty() {
        for (long b : stateBitmap) {
            if (b != 0L) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        // todo verify this logic
        int size = 0;
        for (long b : stateBitmap) {
            while (b != 0L) {
                size += (b & 1L);
                b = b >> 1;
            }
        }
        return size;
    }

    public List<NFAIState> asList() {
        final List<NFAIState> list = new ArrayList<>();
        for (int id = 0; id < manager.stateCount(); id++) {
            if (contains(id)) {
                list.add(manager.getState(id));
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(stateBitmap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NFAIStatePack)) {
            return false;
        }
        return Arrays.equals(stateBitmap, ((NFAIStatePack) o).stateBitmap);
    }

    @Override
    public String toString() {
        return "NFAIStatePack{" +
                asList().toString() +
                '}';
    }
}
