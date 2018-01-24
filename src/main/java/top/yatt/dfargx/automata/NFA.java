package top.yatt.dfargx.automata;

import top.yatt.dfargx.tree.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Only able to accept wfs accessing order, the class constructs a NFA by iterating the input syntax tree recursively
 * from the root node.
 *
 * Created on 2015/5/10.
 */
public class NFA {

    private final Stack<NFAState> stateStack;
    private final NFAStateFactory stateFactory;
    private final List<NFAState> stateList;

    public NFA(Node root) {
        stateList = new ArrayList<>();
        stateFactory = new NFAStateFactory();
        NFAState initState = newState();
        NFAState finalState = newState();
        stateStack = new Stack<>();
        stateStack.push(finalState);
        stateStack.push(initState);
        dfs(root);
    }

    private NFAState newState() {
        NFAState nfaState = stateFactory.create();
        stateList.add(nfaState);
        return nfaState;
    }

    private void dfs(Node node) {
        node.accept(this);
        if (node.hasLeft()) {
            dfs(node.left());
            dfs(node.right());
        }
    }

    public List<NFAState> getStateList() {
        return stateList;
    }

    public void visit(LChar lChar) {
        NFAState i = stateStack.pop();
        NFAState f = stateStack.pop();
        i.transitionRule(lChar.c, f);
    }

    public void visit(LNull lNull) {
        // do nothing
    }

    public void visit(BOr bOr) {
        NFAState i = stateStack.pop();
        NFAState f = stateStack.pop();
        stateStack.push(f);
        stateStack.push(i);
        stateStack.push(f);
        stateStack.push(i);
    }

    public void visit(BConcat bConcat) {
        NFAState i = stateStack.pop();
        NFAState f = stateStack.pop();
        NFAState n = newState();
        stateStack.push(f);
        stateStack.push(n);
        stateStack.push(n);
        stateStack.push(i);
    }

    public void visit(BMany bMany) {
        NFAState i = stateStack.pop();
        NFAState f = stateStack.pop();
        NFAState n = newState();
        i.directRule(n);
        n.directRule(f);
        stateStack.push(n);
        stateStack.push(n);
    }

    public void visit(LClosure lClosure) {
        NFAState i = stateStack.pop();
        NFAState f = stateStack.pop();
        i.directRule(f);
    }
}
