package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class GenerateActionTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(5000*10);

    @Test
    public void testFirst() {
        String[] state = {
                "",
                TestData.SAMPLE_EDGE_MAP
        };

        for (int i = 0; i < 10; i++) {
            String tile = "O" + (char) ('a' + i);
            String action = Agamemnon.generateAction(state, tile);
            assertTrue("unexpected action of length " + action.length() + " returned from generateAction({" + state[0] + "," + state[1] + "}, " + tile + ")", action.length() == 4 || action.length() == 8);
            assertEquals("action " + action + " including unexpected tile returned from generateAction({" + state[0] + "," + state[1] + "}, " + tile + ")", action.substring(0, 2), tile);
        }
    }


    @Test
    public void testLast() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            String lastTile = TestData.SAMPLE_FINISH_STATE[i].substring(TestData.SAMPLE_FINISH_STATE[i].length() - 4, TestData.SAMPLE_FINISH_STATE[i].length() - 2);
            String tilePlacement = TestData.SAMPLE_FINISH_STATE[i].substring(0, TestData.SAMPLE_FINISH_STATE[i].length() - 4);

            String[] state = {
                    tilePlacement,
                    TestData.SAMPLE_EDGE_MAP
            };

            String action = Agamemnon.generateAction(state, lastTile);
            assertTrue("unexpected action of length " + action.length() + " returned from generateAction({" + state[0] + "," + state[1] + "}, " + lastTile + ")", action.length() == 4 || action.length() == 8);
            assertEquals("action " + action + " including unexpected tile returned from generateAction({" + state[0] + "," + state[1] + "}, " + lastTile + ")", action.substring(0, 2), lastTile);
            Integer node = Integer.valueOf(action.substring(2, 4));
            assertTrue("action " + action + " attempts placement on an already occupied node: " + node, Arrays.asList(LAST_EMPTY[i]).contains(node));
        }
    }

    private static final Integer[][] LAST_EMPTY = {
            {13, 20, 29},
            {3, 23, 30},
            {0, 20, 24},
            {7, 12, 30},
            {6, 18, 25},
            {4, 8, 19},
            {0, 13, 18},
            {3, 8, 29},
            {1, 8, 17},
            {2, 23, 30}
    };
}
