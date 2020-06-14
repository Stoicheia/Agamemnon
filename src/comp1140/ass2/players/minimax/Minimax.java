package comp1140.ass2.players.minimax;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.players.Faction;

import java.util.ArrayList;
import java.util.List;

import static comp1140.ass2.Agamemnon.*;
import static comp1140.ass2.gui.GameEdge.AGAMEMNON_NODE_CONNECTIONS;

/**
 * This class authored by Alexander Cox
 **/

public class Minimax {
    static class Action {
        int score;
        public String action;

        Action(int value, String action) {
            this.score = value;
            this.action = action;
        }

        @Override
        public String toString() {
            return String.format("Action: %s with expected utility: %d",action,score);
        }
    }

    private static final int MAX = Integer.MAX_VALUE; // in place if infinite
    private static final int MIN = Integer.MIN_VALUE; // in place of -infinity

    /**
     * A minimax search with alpha/beta pruning. Very slow, not viable for tournament.
     * @param state current state of tiles/edges
     * @param tiles one or two tiles
     * @param depth depth to search
     * @return an action.
     */
    public static String minimaxAction(String[] state, String tiles, int depth) {
        assert (depth > 0);
        String tile1, tile2;
        String best;
        Faction player = Faction.charToFaction(tiles.charAt(0));
        boolean greek = player == Faction.Greek;
        if (tiles.length()==4) {
            tile1 = tiles.substring(0, 2);
            tile2 = tiles.substring(2);
            Action action1 = maximize(state,tile1,greek,depth,MIN,MAX);
            Action action2 = maximize(state,tile2,greek,depth,MIN,MAX);
            best = action1.action + action2.action;
        } else best = maximize(state, tiles,greek, depth,MIN,MAX).action;

        assert (best.charAt(0) == (greek ? 'O' : 'B'));
        return best;
    }

    /**
     * Maximize takes a tile, and finds the placment resulting in the maximum score for player.
     * @param state
     * @param tile
     * @param greek is the player greek or not (i.e. trojan)?
     * @param depth search depth remaining
     * @param alpha alpha for pruning
     * @param beta beta for pruning
     * @return an Action (score,action), which is the best move according to search.
     */
    private static Action maximize(String[] state, String tile, boolean greek, int depth, int alpha, int beta) {
        List<String> unoccupiedNodes = Agamemnon.stateToUnoccupiedNodes(state);
        int[] scores= getTotalScore(state);
        int score = scores[greek ? 0 : 1];
        if (depth <= 0 || unoccupiedNodes.isEmpty())
            return new Action(score,null);

        ArrayList<String> possibleMoves = new ArrayList<String>();
        if (tile.charAt(1)!='j')
            for (String node : unoccupiedNodes) possibleMoves.add(tile+node);
        else
            for (String node: unoccupiedNodes) {
                int[] connections = AGAMEMNON_NODE_CONNECTIONS[Integer.parseInt(node)];
                for (int connection1 : connections)
                    for (int connection2: connections)
                        if (connection1 != connection2)
                            possibleMoves.add(String.format("%s%s%02d%02d",tile, node, connection1, connection2));
            }

        int bestScore = MIN;
        String bestMove = possibleMoves.get(0);
        if (depth == 1)
            for (String nextMove : possibleMoves) {
                int[] nextScores = getTotalScore(applySubAction(state,nextMove));
                int nextScore = nextScores[greek ? 0 : 1];
                if (bestScore < nextScore) {bestScore = nextScore; bestMove = nextMove;}
                if (bestScore >= beta) return new Action(bestScore,bestMove);
                if (alpha < bestScore) alpha = bestScore;
            }
        else
            for (String nextMove : possibleMoves) {
                Character[][] validTiles = getValidTiles(state[0]);
                Character[] myTiles = validTiles[greek ? 0 : 1];
                for (Character newTile : myTiles) {
                    String nextTile = (greek ? "O" : "B") + newTile;
                    Action minimize = minimize(applySubAction(state, nextMove), nextTile, !greek, depth - 1, alpha, beta);
                    int nextScore = minimize.score;
                    if (bestScore < nextScore) {
                        bestScore = nextScore;
                        bestMove = nextMove;
                    }
                    if (bestScore >= beta) return new Action(bestScore, bestMove);
                    if (alpha < bestScore) alpha = bestScore;
                }
            }

        return new Action(bestScore,bestMove);
    }

    /**
     * Minimize takes a tile, and finds the placment resulting in the maximum score for player.
     * @param state
     * @param tile
     ** @param greek is the player greek or not (i.e. trojan)?
     * @param depth search depth remaining
     * @param alpha alpha for pruning
     * @param beta beta for pruning
     * @return an Action (score,action), which is the best move according to search.
     * */
    private static Action minimize(String[] state, String tile, boolean greek, int depth, int alpha, int beta) {
        List<String> unoccupiedNodes = Agamemnon.stateToUnoccupiedNodes(state);
        int[] scores= getTotalScore(state);
        int score = scores[greek ? 0 : 1];
        if (depth <= 0 || unoccupiedNodes.isEmpty())
            return new Action(score,null);

        ArrayList<String> possibleMoves = new ArrayList<String>();
        if (tile.charAt(1)!='j')
            for (String node : unoccupiedNodes) possibleMoves.add(tile+node);
        else
            for (String node: unoccupiedNodes) {
                int[] connections = AGAMEMNON_NODE_CONNECTIONS[Integer.parseInt(node)];
                for (int connection1 : connections)
                    for (int connection2: connections)
                        if (connection1 != connection2) {
                            possibleMoves.add(String.format("%s%s%02d%02d",tile, node, connection1, connection2));
                        }

            }

        int bestScore = MAX;
        String bestMove = possibleMoves.get(0);
        if (depth == 1)
            for (String nextMove : possibleMoves) {
                int[] nextScores = getTotalScore(applySubAction(state,nextMove));
                int nextScore = nextScores[greek ? 0 : 1];
                if (bestScore > nextScore) {bestScore = nextScore; bestMove = nextMove;}
                if (bestScore <= alpha) return new Action(bestScore,bestMove);
                if (beta > bestScore) beta = bestScore;
            }
        else
            for (String nextMove : possibleMoves) {
                Character[][] validTiles = getValidTiles(state[0]);
                Character[] myTiles = validTiles[greek ? 0 : 1];
                for (Character newTile : myTiles) {
                    String nextTile = (greek ? "O" : "B") + newTile;
                    Action maximize = maximize(applySubAction(state, nextMove), nextTile, !greek, depth - 1, alpha, beta);
                    int nextScore = maximize.score;
                    if (bestScore > nextScore) {
                        bestScore = nextScore;
                        bestMove = nextMove;
                    }
                    if (bestScore <= alpha) return new Action(bestScore, bestMove);
                    if (beta > bestScore) beta = bestScore;
                }
            }

        return new Action(bestScore,bestMove);
    }
}
