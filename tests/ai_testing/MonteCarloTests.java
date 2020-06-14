package ai_testing;

import org.junit.Test;

import static comp1140.ass2.Agamemnon.AGAMEMNON_CONNECTIVITY;
import static comp1140.ass2.Agamemnon.applyAction;
import static comp1140.ass2.players.montecarlo.MonteCarlo.generatePossibleMoves;
import static comp1140.ass2.players.montecarlo.MonteCarlo.monteMove;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// authored by Dillon Chen
public class MonteCarloTests {

    @Test
    public void testGeneratePossibleMoves() {
        String[] initialState = {"",AGAMEMNON_CONNECTIVITY};
        String tiles = "Oa";
        assertEquals(32, generatePossibleMoves(initialState,tiles).size());

        String[] state2 = applyAction(initialState, "Oa00");
        String tiles2 = "BaBb";
        assertEquals(465, generatePossibleMoves(state2,tiles2).size()); // expect 31 choose 2 possible moves

        String tilesWarp = "BaBj";
        assertTrue(generatePossibleMoves(state2, tilesWarp).size() > 465);
    }

    @Test
    public void testMonteeMoveInitialState() {
        String[] initialState = {"",AGAMEMNON_CONNECTIVITY};
        String tiles = "Oa";
        assertEquals("Oa", monteMove(initialState,tiles).substring(0,2));
    }

    @Test
    public void testMonteeMoveSecondMove() {
        String[] state2 = applyAction(new String[]{"",AGAMEMNON_CONNECTIVITY}, "Oa00");
        String tiles2 = "BaBb";
        assertEquals("Ba", monteMove(state2,tiles2).substring(0,2));
    }
}
