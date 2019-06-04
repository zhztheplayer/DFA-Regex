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
        final long pre = System.currentTimeMillis();
        String regex = "(a*b|ab*)";
        SyntaxTree tree = new SyntaxTree(regex);
        Node root = tree.getRoot();
        System.out.println("For regex: " + regex);
        System.out.println("Syntax tree: ");
        TreePrinter.getInstance().printTree(root);
        NFA nfa = new NFA(root);
        System.out.println("NFA has " + nfa.getStateList().size() + " states");
        DFA dfa = new DFA(nfa.getStateList());
        System.out.println("DFA has " + dfa.getTransitionTable().length + " states");
        System.out.println("Cost " + (System.currentTimeMillis() - pre) + " ms to compile");
    }

    @Test
    public void testUUID() {
        String regex = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";
        String str = UUID.randomUUID().toString();

        testFor(regex, str);
    }

    @Test
    public void testAddress() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        String str = "192.168.0.255";

        testFor(regex, str);
    }

    @Test
    public void testReDoS() {
        String regex = "(a*)*";
        String str1 = "aaaaaaaaaaaaaaaaab";
        testFor(regex, str1);

    }

    @Test
    public void testLog() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3} - - \\[[^\\]]+\\] \"[^\"]+\" \\d+ \\d+ \"[^\"]+\" \"[^\"]+\"";
        String str = "11.11.11.11 - - [25/Jan/2000:14:00:01 +0100] \"GET /1986.js HTTP/1.1\" 200 932 \"http://domain.com/index.html\" \"Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7 GTB6\"";

        testFor(regex, str);
    }

    private static String repeat(String str, int count) {
        StringBuilder result = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            result.append(str);
        }
        return result.toString;
    }
    
    @Test
    public void testRepetition() {
        String regex = repeat("a?", 30) + repeat("a", 30);
        String str = repeat("a", 30);
        
        testFor(regex, str);
    }

    public void testFor(String regex, String str) {
        long prev;
        prev = System.currentTimeMillis();
        final boolean expected = Pattern.compile(regex).matcher(str).matches();
        System.out.println(System.currentTimeMillis() - prev);
        prev = System.currentTimeMillis();
        boolean actual = new RegexMatcher(regex).match(str);
        System.out.println(System.currentTimeMillis() - prev);
        Assert.assertEquals(expected, actual);
    }

}
