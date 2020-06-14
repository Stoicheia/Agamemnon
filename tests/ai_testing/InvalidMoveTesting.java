package ai_testing;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.players.montecarlo.MonteCarlo;

// authored by Dillon Chen
public class InvalidMoveTesting {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            playGame(1, Agamemnon.AGAMEMNON_INITIAL_STATE);
        }
    }

    private static void playGame(int turn, String[] state) {
        if (MonteCarlo.isFinished(state)) {
            System.out.println("Valid game with end state ");
            System.out.println(state[0]);
            return;
        }

        String tiles = Agamemnon.selectTiles(state[0]);

        // hack fix for first four turns not playing Weaver tiles
        if (turn <= 4) {
            while (tiles.contains("i") || tiles.contains("j")) {
                tiles = Agamemnon.selectTiles(state[0]);
            }
        }

        String action = MonteCarlo.monteMove(state, tiles);

        // print error if action invalid
        if (!Agamemnon.isActionValid(state, action)) {
            System.out.println("Invalid action "+action+" for state ");
            System.out.println(state[0]);
        } else {
            String[] newState = Agamemnon.applyAction(state, action);
            playGame(turn + 1, newState);
        }
    }
}
