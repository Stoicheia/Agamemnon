package comp1140.ass2.players.montecarlo;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.components.Node;

import java.util.ArrayList;

import static comp1140.ass2.gui.GameEdge.getNodeConnections;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static comp1140.ass2.Agamemnon.*;

/**
 * This class implements a pseudo Monte Carlo simulation algorithm to play Agamemnon as best as possible.
 * Authored by Dillon Chen
 */
public class MonteCarlo {

    /**
     * The main monteCarlo function which also implements an opening strategy.
     * @param state
     * @param tiles
     * @return the best possible moves using a pseudo monte carlo simulation algorithm
     */
    public static String monteMove(String[] state, String tiles) {
        if (state[0].length() <= 20) try {
            return openingStrategy(state, tiles);
        } catch (Exception e) {}

        boolean greek = tiles.charAt(0)=='O';
        String out = "";
        int score = Integer.MIN_VALUE;
        ArrayList<String> candidateMoves = generatePossibleMoves(state, tiles);

        for (String candidateMove : candidateMoves) {
            long t = System.currentTimeMillis();
            long cycleMove = t+(4800/candidateMoves.size());
            int simulations = 0;
            int tempScore = 0;
            while (System.currentTimeMillis() < cycleMove) {
                simulations++;
                String[] updateState = Agamemnon.applyAction(state, candidateMove);
                int[] scoreForThisSimulation;
                if (isFinished(updateState))
                    scoreForThisSimulation = Agamemnon.getTotalScore(updateState);
                else
                    scoreForThisSimulation = playRandAndGetScore(updateState, Agamemnon.selectTiles(updateState[0]));
                tempScore += (greek?1:-1)*1000*(scoreForThisSimulation[0] - scoreForThisSimulation[1]);
            }
            tempScore /= simulations;
            if (tempScore > score) {
                score = tempScore;
                out = candidateMove;
            }
        }
        return out;
    }

    /**
     * Opening move strategy hardcoded using reinforced learning
     * Note players are not given Weaver tiles for first four turns (first two turns
     * for each player)
     */
    private final static int[][] STRONG_MOVES =
            {
                    {14, 27, 19, 28, 22, 21, 23,  8, 20, 29, 25, 26,  2, 18,  6,  4,  3, 16, 11, 12, 24, 13,  5, 17,  1, 15,  9, 30, 31,  7,  0, 10},
                    { 8, 19, 28,  4, 11, 29, 14, 27, 16,  3, 12, 23, 20, 22, 24, 21,  2, 25,  6, 13, 26,  1, 17,  5, 18, 15,  9, 31, 30,  0,  7, 10},
                    { 8, 19, 11, 28,  4, 12, 24, 29, 16,  3, 14,  1, 13, 17,  5, 27, 20, 15, 23, 22,  9,  2, 21, 25,  6, 31, 30,  0,  7, 26, 18, 10},
                    { 8, 11, 19, 12, 28, 24,  4, 29, 13, 16,  3, 14,  1, 17,  5, 15, 23, 20, 27, 22,  6,  2, 25, 21,  9, 31,  0, 30,  7, 10, 26, 18},
                    { 8, 11, 19, 12, 24, 13, 14,  4, 17,  5, 28,  1,  6, 23, 15, 20, 29, 25, 22,  2, 16,  3, 21, 27,  9, 30,  0, 31,  7, 10, 18, 26},
                    { 8, 11, 13,  5, 24,  1, 17, 12, 19, 15, 14,  6, 23, 25, 20, 22, 21,  2, 27,  9,  4, 29, 28,  3, 16, 30,  0, 31, 10,  7, 18, 26},
                    { 8, 11, 24, 13,  5,  1, 12, 17, 19, 15, 14,  9,  4, 28, 23,  6, 20,  0, 31, 25, 16, 22, 29, 30, 21, 27,  3,  7,  2, 10, 18, 26},
                    { 8, 11, 24, 13,  1, 12,  5, 17, 19, 15,  4,  9, 28, 31,  0, 30, 16, 29,  7,  3, 10, 14, 25, 23, 20,  6, 22,  2, 27, 21, 26, 18}
            };
    private static String openingStrategy(String[] state, String tiles) {
        if (state[0].length() == 0)
                return tiles+Node.idTo2String(STRONG_MOVES[tiles.charAt(1)-'a'][0]);
        String tile1 = tiles.substring(0,2);
        String tile2 = tiles.substring(2);
        int tile1Encode = tile1.charAt(1)-'a';
        int tile2Encode = tile2.charAt(1)-'a';
        int counter1 = 0;
        int counter2 = 0;
        String node1 = Node.idTo2String(STRONG_MOVES[tile1Encode][counter1]);
        String node2 = Node.idTo2String(STRONG_MOVES[tile2Encode][counter2]);
        ArrayList<String> unoccupiedNodes = Agamemnon.stateToUnoccupiedNodes(state);
        while (!unoccupiedNodes.contains(node1)) {
            counter1++;
            node1 = Node.idTo2String(STRONG_MOVES[tile1Encode][counter1]);
        }
        unoccupiedNodes.remove(node1);
        while (!unoccupiedNodes.contains(node2)) {
            counter2++;
            node2 = Node.idTo2String(STRONG_MOVES[tile2Encode][counter2]);
        }
        return tile1+node1+tile2+node2;
    }

    /**
     * Generates all possible moves for the given state and tiles
     * @param state
     * @param tiles
     * @return all possible moves without duplication
     */
    public static ArrayList<String> generatePossibleMoves(String[] state, String tiles) {
        boolean agamemnon = state[1].length()==245;
        String tile1 = tiles.substring(0,2);
        String tile2 = tiles.substring(2);
        ArrayList<String> unoccupiedNodesList = stateToUnoccupiedNodes(state);
        String[] unoccupiedNodes = new String[unoccupiedNodesList.size()];
        for (int i = 0; i < unoccupiedNodesList.size(); i++) unoccupiedNodes[i] = unoccupiedNodesList.get(i);
        int[][] nodeConnections = getNodeConnections(agamemnon);

        ArrayList<String> possibleMoves = new ArrayList<>();

        if (tile2.length()>0) {
            for (int n1 = 0; n1 < unoccupiedNodes.length-1; n1++) {
                for (int n2 = n1+1; n2 < unoccupiedNodes.length; n2++) {
                    String node1 = unoccupiedNodes[n1];
                    String node2 = unoccupiedNodes[n2];
                    String action1 = tile1 + node1;
                    String action2 = tile2 + node2;
                    int[] warp1Connections = nodeConnections[Integer.parseInt(node1)];
                    int[] warp2Connections = nodeConnections[Integer.parseInt(node2)];
                    if (action1.charAt(1)=='j' && action2.charAt(1)=='j') {
                        for (int w1a = 0; w1a < warp1Connections.length-1; w1a++) {
                            for (int w1b = w1a+1; w1b < warp1Connections.length; w1b++) {
                                for (int w2a = 0; w2a < warp2Connections.length-1; w2a++) {
                                    for (int w2b = w2a+1; w2b < warp2Connections.length; w2b++) {
                                        String move = action1+Node.idTo2String(warp1Connections[w1a])+Node.idTo2String(warp1Connections[w1b])+action2+Node.idTo2String(warp2Connections[w2a])+Node.idTo2String(warp2Connections[w2b]);
                                        possibleMoves.add(move);
                                    }
                                }
                            }
                        }
                    } else if (action1.charAt(1)=='j') {
                        for (int w1a = 0; w1a < warp1Connections.length-1; w1a++) {
                            for (int w1b = w1a+1; w1b < warp1Connections.length; w1b++) {
                                String move = action1+Node.idTo2String(warp1Connections[w1a])+Node.idTo2String(warp1Connections[w1b])+action2;
                                possibleMoves.add(move);
                            }
                        }
                    } else if (action2.charAt(1)=='j') {
                        for (int w2a = 0; w2a < warp2Connections.length-1; w2a++) {
                            for (int w2b = w2a+1; w2b < warp2Connections.length; w2b++) {
                                String move = action1+action2+Node.idTo2String(warp2Connections[w2a])+Node.idTo2String(warp2Connections[w2b]);
                                possibleMoves.add(move);
                            }
                        }
                    } else {
                        possibleMoves.add(action1+action2);
                    }
                }
            }
        } else {
            for (String node1 : unoccupiedNodes) {
                String action1 = tile1 + node1;
                int[] warp1Connections = nodeConnections[Integer.parseInt(node1)];
                if (action1.charAt(1)=='j') {
                    for (int w1a = 0; w1a < warp1Connections.length-1; w1a++) {
                        for (int w1b = w1a+1; w1b < warp1Connections.length; w1b++) {
                            String move = action1+Node.idTo2String(warp1Connections[w1a])+Node.idTo2String(warp1Connections[w1b]);
                            possibleMoves.add(move);
                        }
                    }
                } else {
                    possibleMoves.add(action1);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Plays the game randomly from a given state and tiles and returns the end score
     * @param state
     * @param tiles
     * @return a tuple of player scores after playing the game randomly to the end
     */
    static int[] playRandAndGetScore(String[] state, String tiles) {
        String randomMove = Agamemnon.generateRandomAction(state, tiles);
        String[] updateState = Agamemnon.applyAction(state, randomMove);
        if (isFinished(updateState))
            return getTotalScore(updateState);
        return playRandAndGetScore(updateState, Agamemnon.selectTiles(updateState[0]));
    }

    private static final int FINISHED_GAME_SIZE = 120;
    public static boolean isFinished(String[] state){
        return state[0].length()>=FINISHED_GAME_SIZE;
    }
}
