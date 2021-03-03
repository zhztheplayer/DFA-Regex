package top.yatt.dfargx;

import top.yatt.dfargx.automata.DFA;
import top.yatt.dfargx.automata.NFA;
import top.yatt.dfargx.tree.SyntaxTree;

public class RegexComparator {

    /**
     * Checks if the matching space of <code>regexp1</code> contains the matching space of <code>regexp1</code>.
     */
    public static final boolean contains(String regexp1, String regexp2) {
        SyntaxTree syntaxTree = new SyntaxTree(regexp1);
        NFA nfa = new NFA(syntaxTree.getRoot());
        DFA dfa = new DFA(nfa.asBitmapStateManager());

        // by definition if the matching space of A is equal than the matching space of A|B then B is contained in A
        // similarly, if DFA(A) is identical to DFA(A|B) then B is contained in A.
        SyntaxTree syntaxTree2 = new SyntaxTree(regexp1 + "|" + regexp2);
        NFA nfa2 = new NFA(syntaxTree2.getRoot());
        DFA dfa2 = new DFA(nfa2.asBitmapStateManager());

        return dfa.isEquivalentTo(dfa2);

    }
}
