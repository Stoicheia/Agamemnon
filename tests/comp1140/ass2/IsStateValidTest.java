package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class IsStateValidTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] in, boolean expected) {
        boolean out = Agamemnon.isStateValid(in);
        assertTrue("For input state: '" + Arrays.toString(in)
                        + "', expected " + expected + " but got " + out,
                out == expected);
    }

    @Test
    public void testOverOccupied_1() {
        String[][] testStates = {
                {"Oj00", TestData.SAMPLE_EDGE_MAP},
                {"Oj00Bj01Bf02", TestData.SAMPLE_EDGE_MAP},
                {"Oj00Bf00Bj01", TestData.SAMPLE_EDGE_MAP},
                {"Oj01Bj01Bf02", TestData.SAMPLE_EDGE_MAP},
        };
        boolean[] testResults = {
                true,
                true,
                false,
                false,
        };

        for (int i = 0; i < testResults.length; i++) {
            test(testStates[i], testResults[i]);
        }
    }

    @Test
    public void testOverOccupied_2() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            String tileState = TestData.SAMPLE_FINISH_STATE[i];
            String[] falseState = {
                    "" + tileState.substring(0, 22 + (4 * i)) + tileState
                            .substring(4 * i + 2, 4 * i + 4) + tileState
                            .substring(24 + 4 * i),
                    TestData.SAMPLE_EDGE_MAP
            };
            String[] trueState = {
                    "" + tileState.substring(0, 4 * i + 2) + tileState
                            .substring(118 - 4 * i, 120 - 4 * i) + tileState
                            .substring(4 * i + 4, 118 - 4 * i) + tileState
                            .substring(4 * i + 2, 4 * i + 4) + tileState
                            .substring(120 - 4 * i),
                    TestData.SAMPLE_EDGE_MAP
            };

            test(trueState, true);
            test(falseState, false);
        }
    }

    @Test
    public void testOverUsedTile() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            String tileState = TestData.SAMPLE_FINISH_STATE[i];
            for (int j = 0; j < (tileState.length() / 4) - 6; j++) {
                if (tileState.charAt(9 + 4 * j) != (tileState.charAt(25 + 4 * j))) {
                    String[] duplicateTile = {
                            "" + tileState.substring(0, 9 + (4 * j)) + tileState
                                    .charAt(25 + 4 * j) + tileState
                                    .substring(10 + 4 * j),
                            TestData.SAMPLE_EDGE_MAP
                    };

                    test(duplicateTile, false);
                }
                String[] swapTwo = {
                        "" + tileState.substring(0, 4 * i + 2) + tileState
                                .substring(118 - 4 * i, 120 - 4 * i) + tileState
                                .substring(4 * i + 4, 118 - 4 * i) + tileState
                                .substring(4 * i + 2, 4 * i + 4) + tileState
                                .substring(120 - 4 * i),
                        TestData.SAMPLE_EDGE_MAP
                };
                test(swapTwo, true);
            }
        }
    }

    @Test
    public void testDestinationRange() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            String tileState = TestData.SAMPLE_FINISH_STATE[i];
            String[] falseState_1 = {
                    "" + tileState.substring(0, 42 + (4 * i)) + "4" + tileState
                            .substring(43 + 4 * i),
                    TestData.SAMPLE_EDGE_MAP
            };
            String[] trueState = {
                    "" + tileState.substring(0, 4 * i + 2) + tileState
                            .substring(118 - 4 * i, 120 - 4 * i) + tileState
                            .substring(4 * i + 4, 118 - 4 * i) + tileState
                            .substring(4 * i + 2, 4 * i + 4) + tileState
                            .substring(120 - 4 * i),
                    TestData.SAMPLE_EDGE_MAP
            };
            String[] falseState_2 = {
                    "" + tileState.substring(0, 42 + (4 * i)) + "8" + tileState
                            .substring(43 + 4 * i),
                    TestData.SAMPLE_EDGE_MAP
            };

            test(falseState_2, false);
            test(trueState, true);
            test(falseState_1, false);
        }
    }

    @Test
    public void testIncorrectNumberOfTiles() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            String tileState = TestData.SAMPLE_FINISH_STATE[i];
            for (int j = 0; j < (tileState.length() / 4) - 1; j++) {
                String missingMove = tileState.substring(0, j * 4) + tileState.substring((j + 1) * 4);
                test(new String[]{missingMove, TestData.SAMPLE_EDGE_MAP}, false);
            }
        }
        testTrivialCorrect();
    }

    @Test
    public void testMultigraph() {
        test(new String[]{"Oa00", "S0001S0001"}, false);
        test(new String[]{"Oa00", "S0001S0100"}, false);
        test(new String[]{"Oa00Bf01Bf02", "F0001L0002S0102F0200F0004"}, false);
        for (int i = 0; i < TestData.SAMPLE_EDGE_MAP.length() / 5 - 2; i++) {
            String duplicatedEdge = TestData.SAMPLE_EDGE_MAP.substring(0, i * 5 + 1)
                    + TestData.SAMPLE_EDGE_MAP.substring((i + 2) * 5 + 1, (i + 2) * 5 + 5)
                    + TestData.SAMPLE_EDGE_MAP.substring((i + 1) * 5);
            test(new String[]{TestData.SAMPLE_FINISH_STATE[1], duplicatedEdge}, false);
        }

        testTrivialCorrect();
    }

    private void testTrivialCorrect() {
        test(new String[]{TestData.SAMPLE_FINISH_STATE[0], TestData.SAMPLE_EDGE_MAP}, true);
    }
}

