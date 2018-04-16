import org.junit.Test;
import top.yatt.dfargx.RegexComparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexComparatorTest {

    @Test
    public void basicComparisons() {
        // /A/**/* vs /A/B/C
        assertTrue(RegexComparator.contains("/A/(.)+", "/A/B/C"));

        // /A/**/D vs /A/**/B/D
        assertTrue(RegexComparator.contains("/A/(.)+/D", "/A/(.)+/B/D"));

        // /A/**/D vs /A/**/B/C
        assertFalse(RegexComparator.contains("/A/(.)+/D", "/A/(.)+/B/C"));

        // /A/B/C vs /A/B/C/D
        assertFalse(RegexComparator.contains("/A/B/C$", "/A/B/C/D$"));

        // /A/B/C/* vs /A/B/C/D
        assertTrue(RegexComparator.contains("/A/B/C/[^/]+", "/A/B/C/D"));

        // /A/B/C/* vs /A/B/C/D/*
        assertFalse(RegexComparator.contains("/A/B/C/[^/]+$", "/A/B/C/D/(.)+$"));

        // /**/* vs /A/B/C/D/E
        assertTrue(RegexComparator.contains("/(.)*", "/A/B/C/D/E"));

        // /**/C/* vs /**/*
        assertFalse(RegexComparator.contains("/(.)+/C/[^/]+", "/(.)*"));

        // **/B/* vs /A/B/*
        assertTrue(RegexComparator.contains("/(.)+/B/[^/]+", "/A/B/[^/]+"));

        // **/* vs A/B/*
        assertTrue(RegexComparator.contains("/(.)*", "/A/B/[^/]+"));
    }
}
