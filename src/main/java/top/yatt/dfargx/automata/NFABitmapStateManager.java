package top.yatt.dfargx.automata;

import java.util.*;

public class NFABitmapStateManager {
    private final NFABitmapState[] iStates;

    public NFABitmapStateManager(List<NFAState> states) {
        int stateCount = states.size();
        this.iStates = new NFABitmapState[stateCount];
        for (NFAState state : states) {
            int id = state.getId();
            iStates[id] = new NFABitmapState(id, this);
        }
        for (NFAState state : states) {
            int id = state.getId();
            NFABitmapState iState = iStates[id];
            if (iState == null) {
                throw new IllegalStateException();
            }
            for (NFAState nfaState : state.getDirectTable()) {
                iState.directRule(nfaState.getId());
            }
            for (Map.Entry<Character, Set<NFAState>> charTransRule : state.getTransitionMap().entrySet()) {
                Character ch = charTransRule.getKey();
                for (NFAState nfaState : charTransRule.getValue()) {
                    iState.transitionRule(ch, nfaState.getId());
                }
            }
        }
        for (NFABitmapState iState : iStates) {
            iState.freeze();
        }
    }

    public NFABitmapState getState(int id) {
        return iStates[id];
    }

    public List<NFABitmapState> getStates() {
        return Collections.unmodifiableList(Arrays.asList(iStates));
    }

    public int stateCount() {
        return iStates.length;
    }

    public NFABitmapStatePack newEmptyPack() {
        return new NFABitmapStatePack(this);
    }
}
