package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class IsActionValidTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] in, String action, boolean expected) {
        boolean out = Agamemnon.isActionValid(in, action);
        assertTrue("For input state: '" + Arrays.toString(in)
                        + ", and input action: `" + action + "', expected " + expected + " but got " + out,
                out == expected);
    }

    @Test
    public void testFirst() {
        String[] state = {
                "",
                TestData.SAMPLE_EDGE_MAP
        };
        for (int i = 0; i < 9; i++) {
            test(state, "O" + (char) ('a' + i) + "00", true);
            test(state, "B" + (char) ('a' + i) + "00", false);
            test(state, "O" + (char) ('a' + i) + "30", true);
            test(state, "B" + (char) ('a' + i) + "30", false);
        }
        for (int i = 0; i < 9; i++) {
            test(state, "O" + (char) ('a' + i) + "000001", false);
        }

        test(state, "O" + "j" + "00", false);
        test(state, "O" + "j" + "00001", false);
        test(state, "O" + "j" + "000010", false);
        test(state, "O" + "j" + "000104", true);
        test(state, "B" + "j" + "000010", false);
    }

    @Test
    public void testLast() {
        String[] state_ai = {
                TestData.SAMPLE_FINISH_STATE[8].substring(0, 36),
                TestData.SAMPLE_EDGE_MAP
        };
        String[] state_j = {
                TestData.SAMPLE_FINISH_STATE[9].substring(0, 44),
                TestData.SAMPLE_EDGE_MAP
        };

        test(state_ai, TestData.SAMPLE_FINISH_STATE[8].substring(36, 44), true);
        test(state_ai, TestData.SAMPLE_FINISH_STATE[8].substring(36, 38) + TestData.SAMPLE_FINISH_STATE[8].substring(98, 100), false);
        test(state_ai, TestData.SAMPLE_FINISH_STATE[8].substring(36, 40) + TestData.SAMPLE_FINISH_STATE[8].substring(96, 100), false);
        test(state_j, TestData.SAMPLE_FINISH_STATE[9].substring(44, 52), false);
        test(state_j, TestData.SAMPLE_FINISH_STATE[9].substring(48, 52) + "0004", false);
        test(state_j, TestData.SAMPLE_FINISH_STATE[9].substring(44, 52) + "2126", true);
        test(state_j, TestData.SAMPLE_FINISH_STATE[9].substring(44, 52) + "1726", true);
        test(state_j, TestData.SAMPLE_FINISH_STATE[9].substring(44, 52) + "1721", true);
    }

    @Test
    public void testMiddle() {
        String[] state = {
                "Of00",
                TestData.SAMPLE_LOOM_MAP
        };

        test(state, "Bf01Bf02", true);
        test(state, "Bf01Bf00", false);
        test(state, "Bf02Of01", false);
        test(state, "Bj01Bf02", false);
        test(state, "Bj010005", false);
        test(state, "Bj010005Bf02", true);
        test(state, "Bj020005Bf02", false);
        test(state, "Bj010405Bf02", true);
        test(state, "Bj010805Bf02", false);
        test(state, "Bj010505Bf02", true);
        test(state, "Bj010005Bj02", false);
        test(state, "Bj010005Bj0204040", false);
        test(state, "Bj010005Bj020404", true);
        test(state, "Bj010005Bj020401", false);
        test(state, "Bj010005Bj020403", true);
        test(state, "Bj010005Bj020400", true);
    }

    @Test
    public void testDuplicateNode() {
        String[] state = {"Oa31Bc02Bb04Of11Og12Ba06Bg10Oc19Oj21Bf13Bf15Oj23Od24Bi18Bf22Oe25Of26Be29Bh30", TestData.SAMPLE_EDGE_MAP};
        test(state, "Oi01Oi01", false);
        test(state, "Of00Oh00", false);

        // trivial test to avoid type II error
        test(new String[]{"Oa14", TestData.SAMPLE_EDGE_MAP}, "Bf15Bf29", true);
    }
}
