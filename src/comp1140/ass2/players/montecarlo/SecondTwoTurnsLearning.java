package comp1140.ass2.players.montecarlo;

import comp1140.ass2.Agamemnon;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import static comp1140.ass2.Agamemnon.*;

public class SecondTwoTurnsLearning {

    // main method for learning
    public static void main(String[] args) throws IOException {
        String time = DateTimeFormatter.ofPattern("dd_MM_yyyy_HHmmss").format(LocalDateTime.now());
        PrintWriter results = new PrintWriter(new BufferedWriter(new FileWriter("learning_results/third_turn_strategy_"+time+".txt")));

        long hour = 3600 * 1000;
        long hours = 5;

        long runFor = hour*hours;

        BufferedReader thirdTurnStatesIn = new BufferedReader(new FileReader("learning_results/third_turn_states.txt"));
        ArrayList<String> thirdTurnStates = new ArrayList<>();
        while (true) {
            String state = thirdTurnStatesIn.readLine();
            if (state == null) break;
            thirdTurnStates.add(state);
        }

        // map (state and tiles) to (best action)
        HashMap<String, String> thirdMoveMap = new HashMap<>();
        for (String state : thirdTurnStates) {

            // for every state we get all possible tiles
            HashSet<String> possibleTiles = possibleTurn3Tiles(state);
            for (String tiles : possibleTiles) {
                String actionForStateAndTiles = "";
                long score = Long.MIN_VALUE;

                // for every tile we get all possible moves
                ArrayList<String> possibleMoves = MonteCarlo.generatePossibleMoves(new String[]{state, AGAMEMNON_CONNECTIVITY}, tiles);
                for (String move : possibleMoves) {
                    long simulations = 0;
                    long tempScore = 0;
                    long t = System.currentTimeMillis();
                    long cycleMove = t + (runFor/thirdTurnStates.size()*possibleTiles.size()*possibleMoves.size());

                    while (System.currentTimeMillis() < cycleMove) {
                        simulations++;
                        tempScore += agamemnonTurnLearning(state, move);
                    }

                    tempScore /= simulations;
                    if (tempScore > score) {
                        actionForStateAndTiles = move;
                        score = tempScore;
                    }
                }
                thirdMoveMap.put(state+tiles, actionForStateAndTiles);
            }
        }

        for (String stateAndTiles : thirdMoveMap.keySet()) {
            results.println(stateAndTiles+" "+thirdMoveMap.get(stateAndTiles));
        }

        results.close();
    }

    // run this method many times
    static long agamemnonTurnLearning(String tileState, String action) {
        String[] updateState = Agamemnon.applyAction(new String[]{tileState, AGAMEMNON_CONNECTIVITY},action);
        String[] endState = FirstTwoTurnsLearning.playRandAgamemnon(updateState, selectTiles(updateState[0]));
        int[] endScore = Agamemnon.getTotalScore(endState);
        return endScore[0]-endScore[1];
    }

    // returns set of possible tiles for 3rd turn given a length 12 tile state string
    static HashSet<String> possibleTurn3Tiles(String tileState) {
        HashSet<String> out = new HashSet<>();
        int[] initialTiles = {1, 1, 1, 1, 1, 3, 2, 1}; // tiles a to h only
        initialTiles[tileState.charAt(1)-'a']--;
        for (int i = 0; i < initialTiles.length; i++) {
            for (int j = 0; j < initialTiles.length; j++) {
                if (i == j && initialTiles[i] <= 1) continue;
                out.add("O"+((char) (i+'a'))+"O"+((char) (j+'a')));
            }
        }
        return out;
    }
}
