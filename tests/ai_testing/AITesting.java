package ai_testing;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.players.greedy.Greedy;
import comp1140.ass2.players.minimax.Minimax;
import comp1140.ass2.players.montecarlo.MonteCarlo;

import java.util.Scanner;

// authored by Dillon Chen
public class AITesting {
    public static void main(String[] args) {
        System.out.println("Input p1 and p2 on separate lines (0 for greedy; 1 for minimax; 2 for monteCarlo)");
        Scanner in = new Scanner(System.in);
        int player1 = in.nextInt();
        int player2 = in.nextInt();

        String p1, p2;
        switch (player1) {
            case 0: p1 = "greedy"; break;
            case 1: p1 = "minimax"; break;
            case 2: p1 = "monteCarlo"; break;
            default: p1 = "";
        }
        switch (player2) {
            case 0: p2 = "greedy"; break;
            case 1: p2 = "minimax"; break;
            case 2: p2 = "monteCarlo"; break;
            default: p2 = "";
        }

        System.out.println();
        System.out.println(p1 + " vs " + p2);
        System.out.println();

        int p1win = 0;
        int p2win = 0;
        int draws = 0;

        for (int i = 0; i < 10; i++) {
            char result = playGame(player1, player2, 1, Agamemnon.AGAMEMNON_INITIAL_STATE);
            switch (result) {
                case '1': p1win++; break;
                case '2': p2win++; break;
                case 'T': draws++; break;
            }
            System.out.print(result);
        }

        System.out.println("\n");
        System.out.println("P1 wins: "+p1win);
        System.out.println("P2 wins: "+p2win);
        System.out.println("  Draws: "+draws);
    }

    private static char playGame(int p1, int p2, int turn, String[] state) {
        assert (Math.abs(p1) < 3 && Math.abs(p2) < 3);
        if (MonteCarlo.isFinished(state)) {
            int[] scores = Agamemnon.getTotalScore(state);
            int scoreDiff = scores[0] - scores[1];
            if (scoreDiff == 0)
                return 'T';
            else
                return scoreDiff > 0 ? '1' : '2';
        }

        String tiles = Agamemnon.selectTiles(state[0]);
        if (turn <= 4) {
            while (tiles.contains("i") || tiles.contains("j")) {
                tiles = Agamemnon.selectTiles(state[0]);
            }
        }

        String action;
        if (turn % 2 == 1) {    // player 1
            switch (p1) {
                case 0: action = Greedy.greedyMove(state, tiles); break;
                case 1: action = Minimax.minimaxAction(state, tiles, 3); break;
                case 2: action = MonteCarlo.monteMove(state, tiles); break;
                default: action = "";
            }
        } else {                // player 2
            switch (p2) {
                case 0: action = Greedy.greedyMove(state, tiles); break;
                case 1: action = Minimax.minimaxAction(state, tiles, 3); break;
                case 2: action = MonteCarlo.monteMove(state, tiles); break;
                default: action = "";
            }
        }
        String[] newState = Agamemnon.applyAction(state, action);

        return playGame(p1, p2, turn+1, newState);
    }
}
