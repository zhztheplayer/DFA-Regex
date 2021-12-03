import org.junit.Assert;
import org.junit.Test;
import top.yatt.dfargx.RegexMatcher;
import top.yatt.dfargx.automata.DFA;
import top.yatt.dfargx.automata.NFA;
import top.yatt.dfargx.tree.SyntaxTree;
import top.yatt.dfargx.tree.node.Node;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created on 5/6/15.
 */
public class RegexTest {

    //"([ab]([^cd]*\\w+(abc|abcd){2,5})+)?.*"
    @Test
    public void testProcessing() {
        final long prev = System.currentTimeMillis();
        String regex = "(a*b|ab*)";
        SyntaxTree tree = new SyntaxTree(regex);
        Node root = tree.getRoot();
        System.out.println("For RegEx: " + regex);
        System.out.println("Syntax tree: ");
        TreePrinter.getInstance().printTree(root);
        NFA nfa = new NFA(root);
        System.out.println("NFA has " + nfa.asBitmapStateManager().stateCount() + " states");
        DFA dfa = new DFA(nfa.asBitmapStateManager());
        System.out.println("DFA has " + dfa.getTransitionTable().length + " states");
        System.out.println("Costed " + (System.currentTimeMillis() - prev) + " ms to compile");
    }

    @Test
    public void testUUID() {
        String regex = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";
        String str = UUID.randomUUID().toString();

        assertMatches(regex, str);
    }

    @Test
    public void testAddress() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        String str = "192.168.0.255";

        assertMatches(regex, str);
    }

    @Test
    public void testReDoS() {
        String regex = "(a*)*";
        String str1 = "aaaaaaaaaaaaaaaaab";
        assertMatches(regex, str1);

    }

    @Test
    public void testLog() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3} - - \\[[^\\]]+\\] \"[^\"]+\" \\d+ \\d+ \"[^\"]+\" \"[^\"]+\"";
        String str = "11.11.11.11 - - [25/Jan/2000:14:00:01 +0100] \"GET /1986.js HTTP/1.1\" 200 932 \"http://domain.com/index.html\" \"Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7 GTB6\"";

        assertMatches(regex, str);
    }

    @Test
    public void testNULWithCaret() {
        String regex = "[^x]";
        String str = "\u0000";

        assertMatches(regex, str);
    }

    public void assertMatches(String regex, String str) {
        long prev;
        prev = System.currentTimeMillis();
        final boolean expected = Pattern.compile(regex).matcher(str).matches();
        System.out.println("JVM RegEx costs " + (System.currentTimeMillis() - prev) + "ms. ");
        prev = System.currentTimeMillis();
        boolean actual = new RegexMatcher(regex).match(str);
        System.out.println("DFA RegEx costs " + (System.currentTimeMillis() - prev) + "ms. ");
        Assert.assertEquals(expected, actual);
    }

}