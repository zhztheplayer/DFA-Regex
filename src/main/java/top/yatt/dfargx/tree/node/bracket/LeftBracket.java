package top.yatt.dfargx.tree.node.bracket;

import top.yatt.dfargx.automata.NFA;
import top.yatt.dfargx.stack.OperatingStack;
import top.yatt.dfargx.stack.ShuntingStack;
import top.yatt.dfargx.tree.node.BranchNode;
import top.yatt.dfargx.tree.node.Node;

/**
 * Created on 2015/5/12.
 */
public class LeftBracket extends BranchNode {
    @Override
    public void accept(NFA nfa) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void operate(Node left, Node right) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node copy() {
        return new LeftBracket();
    }

    @Override
    public void accept(OperatingStack operatingStack) {
        operatingStack.visit(this);
    }

    @Override
    public void accept(ShuntingStack shuntingStack) {
        shuntingStack.visit(this);
    }

    @Override
    public String toString() {
        return "[(]";
    }

    @Override
    public int getPri() {
        return -1;
    }
}
