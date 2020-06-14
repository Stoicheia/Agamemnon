package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ApplyActionTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(1000);

    private void test(String[] state, String action, String[] expected) {
        String[] out = Agamemnon.applyAction(state, action);
        assertTrue("For input state: '" + Arrays.toString(state) + "', action: '" + action
                        + "', expected " + Arrays.toString(expected) + " but got " + Arrays.toString(out),
                areStatesEqual(expected, out));
    }

    @Test
    public void test_8() {
        String[] state = {
                "Og06Bg07Bg08",
                TestData.SAMPLE_LOOM_MAP
        };
        String[] acts = {
                "Og09Oi11",
                "Oa23Od26",
                "Of17Of22"
        };

        for (int i = 0; i < acts.length; i++) {
            String[] newState = {
                    state[0] + acts[i],
                    state[1]
            };
            test(state, acts[i], newState);
        }
    }

    @Test
    public void test_12() {
        String[] state = {
                "Og03Bf02Ba00",
                "S0001S0004F0105L0204L0203"
        };

        String[] acts = {
                "Of01Oj040002",
                "Oj040002Of01",
                "Oj040202Of01",
                "Oj040000Of01",
        };

        String[] newTileState = {
                "Oj04Of01Og03Bf02Ba00",
                "Oj04Of01Og03Bf02Ba00",
                "Oj04Of01Og03Bf02Ba00",
                "Oj04Of01Og03Bf02Ba00",
        };

        String[] newEdgeState = {
                "S0001L0004F0105S0204L0203",
                "S0001L0004F0105S0204L0203",
                "S0001S0004F0105L0204L0203",
                "S0001S0004F0105L0204L0203",
        };

        for (int i = 0; i < acts.length; i++) {
            String[] newState = {
                    newTileState[i],
                    newEdgeState[i]
            };
            test(state, acts[i], newState);
        }
    }

    @Test
    public void test_16() {
        String[] state = {
                "Oa08Bb15Bg11",
                "S1315F0813F1314F1114L1418L1419"
        };

        String[] acts = {
                "Oj130815Oj141318",
                "Oj130814Oj141318",
                "Oj131415Oj141318",
                "Oj141318Oj131415",
                "Oj131415Oj141319",
                "Oj131415Oj141113",
        };

        String[] newTileState = {
                "Oa08Bb15Bg11Oj13Oj14",
                "Oa08Bb15Bg11Oj13Oj14",
                "Oa08Bb15Bg11Oj13Oj14",
                "Oa08Bb15Bg11Oj13Oj14",
                "Oa08Bb15Bg11Oj13Oj14",
                "Oa08Bb15Bg11Oj13Oj14",
        };

        String[] newEdgeState = {
                "F1315S0813L1314F1114F1418L1419",
                "S1315F0813L1314F1114F1418L1419",
                "F1315F0813L1314F1114S1418L1419",
                "L1315F0813S1314F1114F1418L1419",
                "F1315F0813L1314F1114L1418S1419",
                "F1315F0813F1314S1114L1418L1419",
        };

        for (int i = 0; i < acts.length; i++) {
            String[] newState = {
                    newTileState[i],
                    newEdgeState[i]
            };
            test(state, acts[i], newState);
        }
    }

    private boolean areStatesEqual(String[] exp, String[] out) {
        Set<String> exp_set = new LinkedHashSet<>();
        Set<String> out_set = new LinkedHashSet<>();
        if (exp[0].length() != out[0].length() || exp[1].length() != out[1].length())
            return false;

        for (int i = 0; i < exp[0].length(); i += 4) {
            exp_set.add(exp[0].substring(i, i + 4));
            out_set.add(out[0].substring(i, i + 4));
        }

        if (!(exp_set.containsAll(out_set) && out_set.containsAll(exp_set)))
            return false;

        exp_set = new LinkedHashSet<>();
        out_set = new LinkedHashSet<>();

        for (int i = 0; i < exp[1].length(); i += 5) {
            exp_set.add(exp[1].substring(i, i + 5));
            out_set.add(out[1].substring(i, i + 5));
        }

        if (!(exp_set.containsAll(out_set) && out_set.containsAll(exp_set)))
            return false;

        return true;
    }
}
