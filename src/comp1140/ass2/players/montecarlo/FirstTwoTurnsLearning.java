package comp1140.ass2.players.montecarlo;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.players.montecarlo.MonteCarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class uses the Monte Carlo simulation algorithm with a much longer time limit to calculate best opening moves.
 * Authored by Dillon Chen
 */
public class FirstTwoTurnsLearning {

    private final static String[] AGAMEMNON_INITIAL_STATE = { "", Agamemnon.AGAMEMNON_CONNECTIVITY };

    // main method for learning
    public static void main(String[] args) throws IOException {
        String time = DateTimeFormatter.ofPattern("dd_MM_yyyy_HHmmss").format(LocalDateTime.now());
        PrintWriter results = new PrintWriter(new BufferedWriter(new FileWriter("learning_results/learning_"+time+".txt")));

        int counter = 0;

        long t= System.currentTimeMillis();
        long hour = 3600 * 1000;
        long hours = 4;
        long end = t+(hours*hour);

        long minute = 60 * 1000;
        long minutes = 10;
        long cycle = t + (minutes*minute);

        HashMap<String, Integer[]> firstMoveMap = new HashMap<>(); // Integer[] = {avg score * 1000, simulations}
        while (System.currentTimeMillis() < end) {
            counter++;
            String tile = "O" + ((char)(counter%8+'a'));
            StringTokenizer s = new StringTokenizer(agamemnonOpeningLearning(tile));
            String action = s.nextToken();
            int score = Integer.parseInt(s.nextToken());
            if (!firstMoveMap.keySet().contains(action)) {
                firstMoveMap.put(action, new Integer[]{1000*score, 1});
            } else {
                int simulations = firstMoveMap.get(action)[1] + 1;
                int scorePlus = (firstMoveMap.get(action)[0] + 1000*score);
                firstMoveMap.replace(action, new Integer[]{scorePlus, simulations});
            }
        }

        for (String move : firstMoveMap.keySet()) {
            results.println(move+" "+(firstMoveMap.get(move)[0]/firstMoveMap.get(move)[1]));
        }

        results.close();
    }

    // run this method many times
    static String agamemnonOpeningLearning(String tile) {
        String action = Agamemnon.generateRandomAction(AGAMEMNON_INITIAL_STATE, tile);
        String[] updateState = Agamemnon.applyAction(AGAMEMNON_INITIAL_STATE,action);
        String[] endState = playRandAgamemnon(updateState, Agamemnon.selectTiles(updateState[0]));
        int[] endScore = Agamemnon.getTotalScore(endState);
        int score = endScore[0]-endScore[1];
        return action+" "+score;
    }

    // both players play randomly until end
    static String[] playRandAgamemnon(String[] state, String tiles) {
        String randomMove = Agamemnon.generateRandomAction(state, tiles);
        String[] updateState = Agamemnon.applyAction(state, randomMove);
        if (MonteCarlo.isFinished(updateState))
            return updateState;
        return playRandAgamemnon(updateState, Agamemnon.selectTiles(updateState[0]));
    }
}
