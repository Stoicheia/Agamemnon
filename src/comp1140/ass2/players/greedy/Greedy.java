package comp1140.ass2.players.greedy;

import comp1140.ass2.components.Node;

import java.util.List;

import static comp1140.ass2.Agamemnon.*;
import static comp1140.ass2.gui.GameEdge.getNodeConnections;

/* Greedy bot written for testing performance of other bots.
 * authored by Dillon Chen
 */
public class Greedy {
    public static String greedyMove(String[] state, String tiles) { // lines 21 onward can be changed to function which returns list of possible moves from a certain state given certain tiles
        boolean agamemnon = state[1].length()==245;
        boolean greek = tiles.charAt(0)=='O';
        String tile1 = tiles.substring(0,2);
        String tile2 = tiles.substring(2);
        List<String> unoccupiedNodes = stateToUnoccupiedNodes(state);
        int[][] nodeConnections = getNodeConnections(agamemnon);
        String action = "";
        String tempAction;
        int heuristic = -999;
        if (tile2.length()!=0) {
            for (String node1 : unoccupiedNodes) {
                for (String node2 : unoccupiedNodes) {
                    if (Integer.parseInt(node1)>=Integer.parseInt(node2)) continue; // pruning
                    String action1 = tile1 + node1;
                    String action2 = tile2 + node2;
                    int[] warp1Connections = nodeConnections[Integer.parseInt(node1)];
                    int[] warp2Connections = nodeConnections[Integer.parseInt(node2)];
                    if (action1.charAt(1)=='j' && action2.charAt(1)=='j') {
                        for (var w1a : warp1Connections) {
                            for (var w1b : warp1Connections) {
                                if ((int) w1a >= (int) w1b) continue; // pruning
                                for (var w2a : warp2Connections) {
                                    for (var w2b : warp2Connections) {
                                        if ((int) w2a >= (int) w2b) continue; // pruning
                                        tempAction = action1+Node.idTo2String((int) w1a)+Node.idTo2String((int) w1b)+action2+Node.idTo2String((int) w2a)+Node.idTo2String((int) w2b);
                                        int[] scores = getTotalScore(applyAction(state, tempAction));
                                        int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                                        if (currentHeuristic > heuristic) {
                                            action = tempAction;
                                            heuristic = currentHeuristic;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (action1.charAt(1)=='j') {
                        for (var w1a : warp1Connections) {
                            for (var w1b : warp1Connections) {
                                if ((int) w1a >= (int) w1b) continue;
                                tempAction = action1+Node.idTo2String((int) w1a)+Node.idTo2String((int) w1b)+action2;
                                int[] scores = getTotalScore(applyAction(state, tempAction));
                                int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                                if (currentHeuristic > heuristic) {
                                    action = tempAction;
                                    heuristic = currentHeuristic;
                                }
                            }
                        }
                    } else if (action2.charAt(1)=='j') {
                        for (var w2a : warp2Connections) {
                            for (var w2b : warp2Connections) {
                                if ((int) w2a >= (int) w2b) continue;
                                tempAction = action1+action2+Node.idTo2String((int) w2a)+Node.idTo2String((int) w2b);
                                int[] scores = getTotalScore(applyAction(state, tempAction));
                                int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                                if (currentHeuristic > heuristic) {
                                    action = tempAction;
                                    heuristic = currentHeuristic;
                                }
                            }
                        }
                    } else {
                        tempAction = action1+action2;
                        int[] scores = getTotalScore(applyAction(state, tempAction));
                        int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                        if (currentHeuristic > heuristic) {
                            action = tempAction;
                            heuristic = currentHeuristic;
                        }
                    }
                }
            }
        } else {
            for (String node : unoccupiedNodes) {
                String action1 = tile1 + node;
                int[] warpConnections = nodeConnections[Integer.parseInt(node)];
                if (action1.charAt(1)=='j') {
                    for (var wa : warpConnections) {
                        for (var wb : warpConnections) {
                            if ((int) wa >= (int) wb) continue;
                            tempAction = action1+Node.idTo2String((int) wa)+Node.idTo2String((int) wb);
                            int[] scores = getTotalScore(applySubAction(state, tempAction));
                            int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                            if (currentHeuristic > heuristic) {
                                action = tempAction;
                                heuristic = currentHeuristic;
                            }
                        }
                    }
                } else {
                    tempAction = action1;
                    int[] scores = getTotalScore(applySubAction(state, tempAction));
                    int currentHeuristic = greek?scores[0]-scores[1]:scores[1]-scores[0];
                    if (currentHeuristic > heuristic) {
                        action = tempAction;
                        heuristic = currentHeuristic;
                    }
                }
            }
        }
        return action;
    }
}
