package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class SelectTilesTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(2000000);

    private void test(String tilePlacements, String expected) {// TODO one character
        String result = Agamemnon.selectTiles(tilePlacements);
        assertTrue("For tile placement \"" + tilePlacements + "\" received unexpected result \"" + result + "\"", result.length() == 2 || result.length() == 4);
        checkTile(tilePlacements, expected, result.substring(0, 2));
        if (expected.length() == 4) {
            checkTile(tilePlacements, expected, result.substring(2, 4));
        }
    }

    private void checkTile(String tilePlacements, String expected, String firstTile) {
        assertTrue("For tile placement \"" + tilePlacements
                        + "\" received unexpected tile \"" + firstTile + "\"; should have been one of: " + expected,
                expected.indexOf(firstTile) >= 0);
    }

    @Test
    public void testFirstTile() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            for (int j = 0; j < TestData.SAMPLE_FINISH_STATE[i].length();
                 j += 4) {
                String result = Agamemnon.selectTiles("");
                assertTrue("For empty tile placement, received unexpected result \"" + result + "\"", result.length() == 2);
                checkTile("", "OaObOcOdOeOfOgOhOiOj", result.substring(0, 2));
            }
        }
    }

    @Test
    public void testTurnSimple() {
        String[] tilePlacements = {
                "Oi19",
                "Oi19Bf21Ba22",
                "Oi19Bf21Bi26Oj27Of11",
                "Oi19Bf21Bi26Oj27Oc11Bf30Bf22",
                "Oi19Bf21Bi26Og27Of11Bf30Bf22Og03Ob04",
        };

        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE[0].length() / 2;
             i += 4) {
            test(tilePlacements[0], "BaBbBcBdBeBfBgBhBiBj");
            test(tilePlacements[1], "OaObOcOdOeOfOgOhOiOj");
            test(tilePlacements[2], "BaBbBcBdBeBfBgBhBiBj");
            test(tilePlacements[3], "OaObOdOeOfOgOhOiOj");
            test(tilePlacements[4], "BaBbBcBdBeBgBhBiBj");
        }
    }

    @Test
    public void testLastTile() {
        for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
            final String tilePlacements = TestData.SAMPLE_FINISH_STATE[i];
            String tile = tilePlacements
                    .substring(tilePlacements.length() - 4, tilePlacements.length() - 2);
            test(tilePlacements
                    .substring(0, tilePlacements.length() - 4), tile);
        }
    }
}
