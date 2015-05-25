package org.zhz.dfargx;

import org.zhz.dfargx.automata.DFA;
import org.zhz.dfargx.automata.NFA;
import org.zhz.dfargx.tree.SyntaxTree;

import java.util.Iterator;

/**
 * Created on 5/25/15.
 */
public class RegexSearcher implements Iterable<MatchedText>{
    private int[][] transitionTable;
    private int is;
    private int rs;
    private boolean[] fs;
    private String str;

    public RegexSearcher(String regex) {
        compile(regex);
        str = null;
    }

    private void compile(String regex) {
        SyntaxTree syntaxTree = new SyntaxTree(regex);
        NFA nfa = new NFA(syntaxTree.getRoot());
        DFA dfa = new DFA(nfa.getStateList());
        transitionTable = dfa.getTransitionTable();
        is = dfa.getInitState();
        fs = dfa.getFinalStates();
        rs = dfa.getRejectedState();
    }

    public void search(String str) {
        this.str = str;
    }

    @Override
    public Iterator<MatchedText> iterator() {
        if (str == null) {
            throw new UnsupportedOperationException();
        }
        return new Itr();
    }

    private class Itr implements Iterator<MatchedText> {
        private int startPos;
        private MatchedText text;

        public Itr() {
            startPos = 0;
            text = null;
        }

        @Override
        public boolean hasNext() {
            while (startPos < str.length()) {
                int s = is;
                for (int i = startPos; i < str.length(); i++) {
                    char ch = str.charAt(i);
                    int ls = s; // last state
                    s = transitionTable[s][ch];
                    if (s == rs) {
                        if (fs[ls]) {
                            text = new MatchedText(str.substring(startPos, i), startPos);
                            startPos = i;
                            return true;
                        } else break;
                    } else if (fs[s] && i == str.length() - 1 ) {
                        text = new MatchedText(str.substring(startPos, str.length()), startPos);
                        startPos = str.length();
                        return true;
                    }
                }
                startPos++;
            }
            return false;
        }

        @Override
        public MatchedText next() {
            return text;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
