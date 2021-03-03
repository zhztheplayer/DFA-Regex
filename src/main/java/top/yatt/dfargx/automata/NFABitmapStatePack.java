package top.yatt.dfargx.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Use this NFAState container to gain faster hashing/comparison from a hashset of NFAStates.
 */
public class NFABitmapStatePack {
    private final long[] stateBitmap;
    private final NFABitmapStateManager manager;

    private volatile boolean writable = true;
    private List<NFABitmapState> asList = null;

    public NFABitmapStatePack(NFABitmapStateManager manager) {
        stateBitmap = newStateBitmap(manager.stateCount());
        this.manager = manager;
    }

    private long[] newStateBitmap(int stateCount) {
        int length = (stateCount - 1) / 63 + 1; // ceiling
        return new long[length];
    }

    public void addState(int stateID) {
        ensureWritable();
        int bucket = stateID / 63;
        int offset = stateID % 63;
        long f = 1L << offset;
        stateBitmap[bucket] |= f;
    }

    public void addAll(NFABitmapStatePack other) {
        ensureWritable();
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
                size += b & 1L;
                b = b >> 1;
            }
        }
        return size;
    }

    public List<NFABitmapState> asList() {
        if (writable) {
            throw new UnsupportedOperationException("Pack is still writable. Call asList only when writing is finished");
        }
        if (asList == null) {
            asList = toList();
        }
        return asList;
    }

    private List<NFABitmapState> toList() {
        final List<NFABitmapState> list = new ArrayList<>();
        for (int id = 0; id < manager.stateCount(); id++) {
            if (contains(id)) {
                list.add(manager.getState(id));
            }
        }
        return Collections.unmodifiableList(list);
    }

    public void freeze() {
        writable = false;
    }

    private void ensureWritable() {
        if (!writable) {
            throw new IllegalStateException("Pack is not writable");
        }
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
        if (!(o instanceof NFABitmapStatePack)) {
            return false;
        }
        return Arrays.equals(stateBitmap, ((NFABitmapStatePack) o).stateBitmap);
    }

    @Override
    public String toString() {
        return "NFAIStatePack{" +
                toList().toString() +
                '}';
    }
}
