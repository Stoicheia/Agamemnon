package comp1140.ass2.components;

import comp1140.ass2.players.Faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// authored by Olin Gao
public class Scorer {

    public static final int[] PRIMES = {2,3,5,7,11};

    private static List<Integer> traversed = new ArrayList<>();
    private static List<Integer> sillyWefts = new ArrayList<>();

    public static int[] scoreLeaders(int nodeId, List<Node> nodes){ // {score, greece, troy}

        Node thisNode = nodes.get(nodeId);
        HashMap<Node, EdgeType> connections = thisNode.getConnectedNodes();
        Tile thisTile = thisNode.getTile();

        int score = 0;
        int leadershipTroy = 1;
        int leadershipGreece = 1;


        boolean isWeft = false;

        if(thisTile!=null) {
            if (thisTile.getFaction() == Faction.Greek) {
                if (thisTile.getRank() != 'f') {
                    leadershipGreece *= PRIMES[thisTile.getRank()-'A'];
                }
            }
            else {
                if (thisTile.getRank() != 'f') {
                    leadershipTroy *= PRIMES[thisTile.getRank()-'A'];
                }
            }
            if (thisTile.getTileType()== TileType.Weft)
                isWeft = true;
        }

        if (!isWeft && thisTile!=null) {
            traversed.add(thisNode.getId());
            for (Node connected : connections.keySet()) {
                if (connections.get(connected) == EdgeType.Leadership
                        && !traversed.contains(connected.getId())) {
                    score++;
                    int[] nextEval = scoreLeaders(connected.getId(), nodes);
                    leadershipGreece*=nextEval[1];
                    leadershipTroy*=nextEval[2];
                    score += nextEval[0];
                }
            }
        }

        return new int[] {score, leadershipGreece, leadershipTroy};
    }

    public static int[] scoreStrength(int nodeId, List<Node> nodes){ // {score, greece, troy}

        Node thisNode = nodes.get(nodeId);
        HashMap<Node, EdgeType> connections = thisNode.getConnectedNodes();
        Tile thisTile = thisNode.getTile();

        int score = 0;
        int strengthTroy = 0;
        int strengthGreece = 0;


        boolean isWeft = false;


        if(thisTile!=null) {
            if (thisTile.getFaction() == Faction.Greek)
                strengthGreece = thisTile.getStrength();
            else
                strengthTroy = thisTile.getStrength();
            if (thisTile.getTileType()==TileType.Weft)
                isWeft = true;
        }

        if (!isWeft && thisTile!=null) {
            traversed.add(thisNode.getId());
            traversed.add(thisNode.getId());
            for (Node connected : connections.keySet()) {
                if (connections.get(connected) == EdgeType.Strength
                        && !traversed.contains(connected.getId())) {
                    score++;
                    int[] nextEval
                            = scoreStrength(connected.getId(), nodes);
                    strengthGreece+=nextEval[1];
                    strengthTroy+=nextEval[2];
                    score += nextEval[0];
                }
            }
        }

        return new int[] {score, strengthGreece, strengthTroy};
    }

    public static int[] scoreForce(int nodeId, List<Node> nodes){ // {score, greece, troy}

        Node thisNode = nodes.get(nodeId);
        HashMap<Node, EdgeType> connections = thisNode.getConnectedNodes();
        Tile thisTile = thisNode.getTile();

        int score = 0;
        int forceTroy = 0;
        int forceGreece = 0;

        boolean isWeft = false;

        if(thisTile!=null) {
            if (thisTile.getFaction() == Faction.Greek)
                forceGreece = 1;
            else
                forceTroy = 1;
            if (thisTile.getTileType()==TileType.Weft)
                isWeft = true;
        }

        if (!isWeft && thisTile!=null) {
            traversed.add(thisNode.getId());
            traversed.add(thisNode.getId());
            for (Node connected : connections.keySet()) {
                if (connections.get(connected) == EdgeType.Force
                        && !traversed.contains(connected.getId())) {
                    score++;
                    int[] nextEval
                            = scoreForce(connected.getId(), nodes);
                    forceGreece+=nextEval[1];
                    forceTroy+=nextEval[2];
                    score += nextEval[0];
                }
            }
        }

        return new int[] {score, forceGreece, forceTroy};
    }

    public static int[] scoreDisconnectedWefts(List<Node> nodes) {

        int greeceScore = 0;
        int troyScore = 0;

        for (Node node : nodes) {
            if(node.getTile()!=null) {
                Tile thisTile = node.getTile();
                HashMap<Node, EdgeType> connections = node.getConnectedNodes();
                Faction thisFaction = thisTile.getFaction();

                if (thisTile.getTileType() == TileType.Weft) {
                    for (Node connected : connections.keySet()) {
                        if (connections.get(connected) == EdgeType.Force
                                && (connected.getTile() == null ||
                                (connected.getTile().getTileType() == TileType.Weft
                                        && connected.getTile().getFaction() == thisFaction))
                                && !sillyWefts.contains(node.getId())
                                && !sillyWefts.contains(connected.getId())) {
                            if (thisFaction == Faction.Greek)
                                greeceScore++;
                            else
                                troyScore++;
                            sillyWefts.add(node.getId());
                        }

                    }
                }
            }
        }

        int[] finalScore = {greeceScore, troyScore};
        return finalScore;
    }

    public static List<Integer> getTraversed(){return traversed;}
    public static void clearTraversed(){traversed = new ArrayList<>(); sillyWefts = new ArrayList<>();}
}

