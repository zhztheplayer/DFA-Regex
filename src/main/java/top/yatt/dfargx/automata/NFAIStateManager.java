package top.yatt.dfargx.automata;

import java.util.*;

public class NFAIStateManager {
    private final NFAIState[] iStates;

    public NFAIStateManager(List<NFAState> states) {
        int stateCount = states.size();
        this.iStates = new NFAIState[stateCount];
        for (NFAState state : states) {
            int id = state.getId();
            iStates[id] = new NFAIState(id, stateCount, this);
        }
        for (NFAState state : states) {
            int id = state.getId();
            NFAIState iState = iStates[id];
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
    }

    public NFAIState getState(int id) {
        return iStates[id];
    }

    public List<NFAIState> getStates() {
        return Collections.unmodifiableList(Arrays.asList(iStates));
    }

    public int stateCount() {
        return iStates.length;
    }

    public NFAIStatePack newEmptyPack() {
        return new NFAIStatePack(this);
    }
}
