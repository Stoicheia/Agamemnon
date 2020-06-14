package ai_testing;

import comp1140.ass2.TestData;
import comp1140.ass2.players.minimax.Minimax;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

// authored by Alexander Cox
public class MinimaxTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(25); // Minimax is slow!

    @Test
    public void minimaxAction() {
        String[] state = { "", TestData.SAMPLE_EDGE_MAP };
        for (int i = 0; i < 10; i++) {
            for (String tile: new String[]{"O","B"}) {
                tile += (char) ('a' + i);
                if (tile.charAt(0) == 'B')
                //    state = Agamemnon.action(state, Agamemnon.generateRandomAction(state, tile));
                for (int j = 1; j < 4; j++) {
                    String action = Minimax.minimaxAction(state, tile, j);
                    assertNotNull(action);
                    assertTrue(
                            String.format("Minimax action is of incorrect length. Action: %s  of length %d from input (state:(%s,%s), tile:%s, depth: %d)", action, action.length(), state[0], state[1], tile, j),
                            action.length() == 4 || action.length() == 8);
                    assertEquals(
                            String.format("Action returned by minimax is incorrect tile. Expected %s but got %s. Input (state:(%s,%s), tile:%s, depth: %d) ", tile, action.substring(0, 2), state[0], state[1], tile, j),
                            action.substring(0, 2), tile);
                    /*boolean greek = tile.charAt(0) == 'O';
                    int[] myScores = Agamemnon.getTotalScore(Agamemnon.applySubAction(state, action));
                    int[] randomScores = Agamemnon.getTotalScore(Agamemnon.applySubAction(state, Agamemnon.generateRandomAction(state, tile)));
                    boolean myScoreIsBetterThanRandom = (greek ? myScores[0] : myScores[1]) >= (greek ? randomScores[0] : randomScores[1]);
                    assertTrue(
                            String.format("Minimax returns an action whose score is not better than a random action. Got %s. Input (state:(%s,%s), tile:%s, depth: %d)", action, state[0], state[1], tile, j),
                            myScoreIsBetterThanRandom);*/
                }
            }
        }
    }
}
