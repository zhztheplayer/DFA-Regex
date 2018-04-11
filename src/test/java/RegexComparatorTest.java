import org.junit.Test;
import top.yatt.dfargx.RegexComparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexComparatorTest {

    @Test
    public void basicComparisons() {
        // /A/**/* vs /A/B/C
        assertTrue(RegexComparator.isContained("/A/(.)+", "/A/B/C"));

        // /A/**/D vs /A/**/B/D
        assertTrue(RegexComparator.isContained("/A/(.)+/D", "/A/(.)+/B/D"));

        // /A/**/D vs /A/**/B/C
        assertFalse(RegexComparator.isContained("/A/(.)+/D", "/A/(.)+/B/C"));

        // /A/B/C vs /A/B/C/D
        assertFalse(RegexComparator.isContained("/A/B/C", "/A/B/C/D"));

        // /A/B/C/* vs /A/B/C/D
        assertTrue(RegexComparator.isContained("/A/B/C/[^/]+", "/A/B/C/D"));

        // /A/B/C/* vs /A/B/C/D/E
        assertFalse(RegexComparator.isContained("/A/B/C/[^/]+", "/A/B/C/D/E"));

        // /**/* vs /A/B/C/D/E
        assertTrue(RegexComparator.isContained("/(.)*", "/A/B/C/D/E"));

        // /**/C/* vs /**/*
        assertFalse(RegexComparator.isContained("/(.)+/C/[^/]+", "/(.)*"));
    }
}
