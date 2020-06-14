package comp1140.ass2;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsStateWellFormedTest {
    private static final char FIRST_ASCII = ' ';
    private static final char LAST_ASCII = '~';
    private static final int CHAR_RANGE = LAST_ASCII - FIRST_ASCII;
    private static final int NUM_RANDOM = 20;

    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] in, boolean expected) {
        boolean out = Agamemnon.isStateWellFormed(in);
        assertTrue("For input state: '" + Arrays.toString(in)
                        + "', expected " + expected + " but got " + out,
                out == expected);
    }

    @Test
    public void testSimple() {
        String[][] states = {
                {""},        // false
                {"", ""},    // true
                {"", "", ""},    // false
                {"", "S0001", ""},    // false
                {"", "S0001"},        // true
                {"Ob00", "S0001"},        // true
                {"OB00", "S0001"},        // false
                {"Bb00", "S0001"},        // true
                {"Bj00", "S0001"},        // true
                {"Ba00", "S0001"},        // true
                {"ba00", "S0001"},        // false
                {"Oba0", "S0001"},        // false
                {"Ob0a", "S0001"},        // false
                {"Ob00Of01", "S0001"},        // true
                {"Ob00Of010", "S0001"},        // false
                {"Ob00Of01", "S001"},        // false
                {"Ob00Of01", "S00010"},        // false
                {"Ob00Of01", "S0001E0002"},        // true
                {"Ob00Of010", "S0001E0002"},        // false
        };
        boolean[] expected = {
                false,
                true,
                false,
                false,
                true,
                true,
                false,
                true,
                true,
                true,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                true,
                false,
        };

        for (int i = 0; i < states.length; i++) {
            test(states[i], expected[i]);
        }
    }

    @Test
    public void testValidArrayElements() {
        testTrivialGood();
        test(new String[]{}, false);
        test(new String[]{null}, false);
        test(new String[]{TestData.SAMPLE_EDGE_MAP}, false);
        test(new String[]{"Bx31"}, false);
        test(new String[]{"Bx31", ""}, false);
        test(new String[]{null, TestData.SAMPLE_EDGE_MAP}, false);
        test(new String[]{"Bx31", null}, false);
    }

    @Test
    public void testGoodEndStates() {
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            test(new String[]{finalPlacement, TestData.SAMPLE_EDGE_MAP}, true);
        }
        testTrivialBad();
    }

    @Test
    public void testBadPlayer() {
        Random rand = new Random();
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            int randomIndex = rand.nextInt(finalPlacement.length() / 4) * 4;
            char randomPlayer = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
            while (randomPlayer == 'O' || randomPlayer == 'B') randomPlayer++;
            String modifiedPlacement = finalPlacement.substring(0, randomIndex) + randomPlayer + finalPlacement.substring(randomIndex + 1);
            test(new String[]{modifiedPlacement, TestData.SAMPLE_EDGE_MAP}, false);
        }
        testTrivialGood();
    }

    @Test
    public void testBadTile() {
        Random rand = new Random();
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            int randomIndex = rand.nextInt(finalPlacement.length() / 4) * 4 + 1;
            char randomTile = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
            while (randomTile >= 'a' && randomTile <= 'j') randomTile++;
            String modifiedPlacement = finalPlacement.substring(0, randomIndex) + randomTile + finalPlacement.substring(randomIndex + 1);
            test(new String[]{modifiedPlacement, TestData.SAMPLE_EDGE_MAP}, false);
        }
        testTrivialGood();
    }

    @Test
    public void testBadPlacementNode() {
        Random rand = new Random();
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            int randomIndex = rand.nextInt(finalPlacement.length() / 4) * 4 + 2;
            char randomNode1 = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
            while (randomNode1 >= '0' && randomNode1 <= '9') randomNode1++;
            String modifiedPlacement = finalPlacement.substring(0, randomIndex) + randomNode1 + finalPlacement.substring(randomIndex + 1);
            test(new String[]{modifiedPlacement, TestData.SAMPLE_EDGE_MAP}, false);
            randomIndex = rand.nextInt(finalPlacement.length() / 4) * 4 + 3;
            char randomNode2 = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
            while (randomNode2 >= '0' && randomNode2 <= '9') randomNode2++;
            modifiedPlacement = finalPlacement.substring(0, randomIndex) + randomNode2 + finalPlacement.substring(randomIndex + 1);
            test(new String[]{modifiedPlacement, TestData.SAMPLE_EDGE_MAP}, false);
        }
        testTrivialGood();
    }

    @Test
    public void testBadEdgeType() {
        Random rand = new Random();
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            int randomIndex = rand.nextInt(TestData.SAMPLE_EDGE_MAP.length() / 5) * 5;
            char randomEdgeType = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
            while (randomEdgeType == 'S' || randomEdgeType == 'L' || randomEdgeType == 'F' || randomEdgeType == 'E')
                randomEdgeType++;
            String modifiedEdges = TestData.SAMPLE_EDGE_MAP.substring(0, randomIndex)
                    + randomEdgeType
                    + TestData.SAMPLE_EDGE_MAP.substring(randomIndex + 1);
            test(new String[]{finalPlacement, modifiedEdges}, false);
        }
        testTrivialGood();
    }

    @Test
    public void testBadEndpoint() {
        Random rand = new Random();
        for (String finalPlacement : TestData.SAMPLE_FINISH_STATE) {
            for (int j = 1; j <= 4; j++) {
                int randomIndex = rand.nextInt(TestData.SAMPLE_EDGE_MAP.length() / 5) * 5 + j;
                char randomChar = (char) (rand.nextInt(CHAR_RANGE) + FIRST_ASCII);
                while (randomChar >= '0' && randomChar <= '9')
                    randomChar++;
                String modifiedEdges = TestData.SAMPLE_EDGE_MAP.substring(0, randomIndex)
                        + randomChar
                        + TestData.SAMPLE_EDGE_MAP.substring(randomIndex + 1);
                test(new String[]{finalPlacement, modifiedEdges}, false);
            }
        }
        testTrivialGood();
    }

    private void testTrivialGood() {
        test(new String[]{"Of31", TestData.SAMPLE_EDGE_MAP}, true);
    }

    private void testTrivialBad() {
        test(new String[]{"Bx31", TestData.SAMPLE_EDGE_MAP}, false);
    }
}