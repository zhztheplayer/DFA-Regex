package top.yatt.dfargx.automata;

import top.yatt.dfargx.util.CommonSets;

import java.util.*;

/**
 * Created on 2015/5/10.
 */
public class DFA {

    private int[][] transitionTable;
    // init state
    private int is;
    // rejected state
    private int rs;
    // final states
    private boolean[] fs;

    public DFA(List<NFAState> nfaStateList) {
        transitionTable = null;
        is = rs = -1;
        fs = null;
        convert(nfaStateList);
    }

    public int[][] getTransitionTable() {
        return transitionTable;
    }

    public int getRejectedState() {
        return rs;
    }

    public int getInitState() {
        return is;
    }

    public boolean[] getFinalStates() {
        return fs;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DFA)) {
            return false;
        }
        DFA other = (DFA) o;

        int [][] checked = new int[transitionTable.length][other.transitionTable.length];
        return dfaEquivalenceCheck(other, is, other.is, checked);
    }

    private boolean dfaEquivalenceCheck(DFA other, int initialState, int otherInitialState, int [][] checked) {
        // transitions for this DFA for state initialState
        int initialTransitions[] = transitionTable[initialState];
        // transitions for the other DFA for state otherInitialState
        int otherInitialTransitions[] = other.transitionTable[otherInitialState];

        // For every possible transition from initialState (and otherInitialState)
        for (int i = 0; i < initialTransitions.length; i++) {

            // if the target state is already computed in previous iterations, skip
            if (checked[initialTransitions[i]][otherInitialTransitions[i]] == 1) {
                continue;
            }
            // mark the transition as computed
            checked[initialTransitions[i]][otherInitialTransitions[i]] = 1;

            if (fs[initialTransitions[i]] != other.fs[otherInitialTransitions[i]]) {
                // one transition goes to a final state and the other does not, this DFA is not equivalent
                return false;
            } else if ((initialTransitions[i] == rs && otherInitialTransitions[i] != other.rs)
                    || (initialTransitions[i] != rs && otherInitialTransitions[i] == other.rs)) {
                // one transition goes to rejected state and the other does not, this DFA is not equivalent
                return false;
            } else if (fs[initialTransitions[i]] == false && other.fs[otherInitialTransitions[i]] == false) {
                // both transitions go to intermediate states, needs further computing using current states as initial
                if (!dfaEquivalenceCheck(other, initialTransitions[i], otherInitialTransitions[i], checked)) {
                    // the transition is not equivalent further down, this DFA is not equivalent
                    return false;
                }
            }
        }
        // All transitions check were equivalent, this DFA is equivalent
        return true;
    }

    private void convert(List<NFAState> nfaStateList) {
        NFAState initState = nfaStateList.get(0);
        NFAState finalState = nfaStateList.get(1);

        Map<NFAState, Set<NFAState>> closureMap = calculateClosure(nfaStateList);

        // construct a NFA first
        Map<NFAState, Map<Character, Set<NFAState>>> nfaTransitionMap = new HashMap<>();
        for (NFAState state : nfaStateList) {
            Map<Character, Set<NFAState>> subMap = new HashMap<>();
            for (char ch = 0; ch < CommonSets.ENCODING_LENGTH; ch++) {
                Set<NFAState> closure = closureMap.get(state);
                Set<NFAState> reachable = traceReachable(closure, ch, closureMap);
                if (!reachable.isEmpty()) {
                    subMap.put(ch, reachable);
                }
            }
            nfaTransitionMap.put(state, subMap);
        }

        // Construct an original DFA using the constructed NFA. Each key which is set of nfa states is a new dfa state.
        Map<Set<NFAState>, Map<Character, Set<NFAState>>> originalDFATransitionMap = new HashMap<>();
        constructOriginalDFA(closureMap.get(initState), nfaTransitionMap, originalDFATransitionMap);

        // construct minimum DFA
        minimize(originalDFATransitionMap, closureMap.get(initState), finalState);
    }

    private void constructOriginalDFA(Set<NFAState> stateSet, Map<NFAState, Map<Character, Set<NFAState>>> nfaTransitionMap, Map<Set<NFAState>, Map<Character, Set<NFAState>>> originalDFATransitionMap) {

        Stack<Set<NFAState>> stack = new Stack<>();
        stack.push(stateSet);

        do {
            Set<NFAState> pop = stack.pop();
            Map<Character, Set<NFAState>> subMap = originalDFATransitionMap.get(pop);
            if (subMap == null) {
                subMap = new HashMap<>();
                originalDFATransitionMap.put(pop, subMap);
            }
            for (char ch = 0; ch < CommonSets.ENCODING_LENGTH; ch++) {
                Set<NFAState> union = new HashSet<>();
                for (NFAState state : pop) {
                    Set<NFAState> nfaSet = nfaTransitionMap.get(state).get(ch);
                    if (nfaSet != null) {
                        union.addAll(nfaSet);
                    }
                }
                if (!union.isEmpty()) {
                    subMap.put(ch, union);
                    if (!originalDFATransitionMap.containsKey(union)) {
                        stack.push(union);
                    }
                }
            }
        } while (!stack.isEmpty());
    }

    private Map<NFAState, Set<NFAState>> calculateClosure(List<NFAState> nfaStateList) {
        Map<NFAState, Set<NFAState>> map = new HashMap<>();
        for (NFAState state : nfaStateList) {
            Set<NFAState> closure = new HashSet<>();
            dfsClosure(state, closure);
            map.put(state, closure);
        }
        return map;
    }

    private void dfsClosure(NFAState state, Set<NFAState> closure) {
        Stack<NFAState> nfaStack = new Stack<>();
        nfaStack.push(state);
        do {
            NFAState pop = nfaStack.pop();
            closure.add(pop);
            for (NFAState next : pop.getDirectTable()) {
                if (!closure.contains(next)) {
                    nfaStack.push(next);
                }
            }
        } while (!nfaStack.isEmpty());
    }

    private Set<NFAState> traceReachable(Set<NFAState> closure, char ch, Map<NFAState, Set<NFAState>> closureMap) {
        Set<NFAState> result = new HashSet<>();
        for (NFAState closureState : closure) {
            Map<Character, Set<NFAState>> transitionMap = closureState.getTransitionMap();
            Set<NFAState> stateSet = transitionMap.get(ch);
            if (stateSet != null) {
                for (NFAState state : stateSet) {
                    result.addAll(closureMap.get(state)); // closure of all the reachable states by scanning a char of the given closure.
                }
            }
        }
        return result;
    }

    private void minimize(Map<Set<NFAState>, Map<Character, Set<NFAState>>> oriDFATransitionMap, Set<NFAState> initClosure, NFAState finalNFAState) {
        Map<Integer, int[]> renamedDFATransitionTable = new HashMap<>();
        Map<Integer, Boolean> finalFlags = new HashMap<>();
        Map<Set<NFAState>, Integer> stateRenamingMap = new HashMap<>();
        int initStateAfterRenaming = -1;
        int renamingStateID = 1;

        // rename all states
        for (Set<NFAState> nfaState : oriDFATransitionMap.keySet()) {
            if (initStateAfterRenaming == -1 && nfaState.equals(initClosure)) {
                initStateAfterRenaming = renamingStateID; // preserve init state id
            }
            stateRenamingMap.put(nfaState, renamingStateID++);
        }

        renamedDFATransitionTable.put(0, newRejectedState()); // the rejected state 0
        finalFlags.put(0, false);

        // construct renamed dfa transition table
        for (Map.Entry<Set<NFAState>, Map<Character, Set<NFAState>>> entry : oriDFATransitionMap.entrySet()) {
            renamingStateID = stateRenamingMap.get(entry.getKey());
            int[] state = newRejectedState();
            for (Map.Entry<Character, Set<NFAState>> row : entry.getValue().entrySet()) {
                state[row.getKey()] = stateRenamingMap.get(row.getValue());
            }
            renamedDFATransitionTable.put(renamingStateID, state);
            if (entry.getKey().contains(finalNFAState)) {
                finalFlags.put(renamingStateID, true);
            } else {
                finalFlags.put(renamingStateID, false);
            }
        }

        // group states to final states and non-final states
        Map<Integer, Integer> groupFlags = new HashMap<>();
        for (int i = 0; i < finalFlags.size(); i++) {
            boolean b = finalFlags.get(i);
            if (b) {
                groupFlags.put(i, 0);
            } else {
                groupFlags.put(i, 1);
            }
        }

        int groupTotal = 2;
        int preGroupTotal;
        do { // splitting, group id is the final state id
            preGroupTotal = groupTotal;
            for (int sensitiveGroup = 0; sensitiveGroup < preGroupTotal; sensitiveGroup++) {
                //  <target group table, state id set>
                Map<Map<Integer, Integer>, Set<Integer>> invertMap = new HashMap<>();
                for (int sid = 0; sid < groupFlags.size(); sid++) { //use state id to iterate
                    int group = groupFlags.get(sid);
                    if (sensitiveGroup == group) {
                        Map<Integer, Integer> targetGroupTable = new HashMap<>(CommonSets.ENCODING_LENGTH);
                        for (char ch = 0; ch < CommonSets.ENCODING_LENGTH; ch++) {
                            int targetState = renamedDFATransitionTable.get(sid)[ch];
                            int targetGroup = groupFlags.get(targetState);
                            targetGroupTable.put((int) ch, targetGroup);
                        }
                        Set<Integer> stateIDSet = invertMap.get(targetGroupTable);
                        if (stateIDSet == null) {
                            stateIDSet = new HashSet<>();
                            invertMap.put(targetGroupTable, stateIDSet);
                        }
                        stateIDSet.add(sid);
                    }
                }

                boolean first = true;
                for (Set<Integer> stateIDSet : invertMap.values()) {
                    if (first) {
                        first = false;
                    } else {
                        for (int sid : stateIDSet) {
                            groupFlags.put(sid, groupTotal);
                        }
                        groupTotal++;
                    }
                }
            }
        } while (preGroupTotal != groupTotal);

        // determine initial group state
        is = groupFlags.get(initStateAfterRenaming);

        // determine rejected group state
        rs = groupFlags.get(0);

        // determine final group states
        Set<Integer> finalGroupFlags = new HashSet<>();
        for (int i = 0, groupFlagsSize = groupFlags.size(); i < groupFlagsSize; i++) {
            Integer groupFlag = groupFlags.get(i);
            if (finalFlags.get(i)) {
                finalGroupFlags.add(groupFlag);
            }
        }
        fs = new boolean[groupTotal];
        for (int i = 0; i < groupTotal; i++) {
            fs[i] = finalGroupFlags.contains(i);
        }

        // construct the output transition table
        transitionTable = new int[groupTotal][];

        for (int groupID = 0; groupID < groupTotal; groupID++) {
            for (int sid = 0; sid < groupFlags.size(); sid++) {
                if (groupID == groupFlags.get(sid)) {
                    int[] oriState = renamedDFATransitionTable.get(sid);
                    int[] state = new int[CommonSets.ENCODING_LENGTH];
                    for (char ch = 0; ch < CommonSets.ENCODING_LENGTH; ch++) {
                        int next = oriState[ch];
                        state[ch] = groupFlags.get(next);
                    }
                    transitionTable[groupID] = state;
                    break;
                }
            }
        }
    }


    private int[] newRejectedState() {
        int[] state = new int[CommonSets.ENCODING_LENGTH];
        rejectAll(state);
        return state;
    }

    private void rejectAll(int[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] = 0;
        }
    }
}
