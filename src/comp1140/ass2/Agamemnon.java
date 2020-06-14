package comp1140.ass2;

import comp1140.ass2.components.Node;
import comp1140.ass2.components.Scorer;
import comp1140.ass2.players.Faction;
import comp1140.ass2.players.montecarlo.MonteCarlo;
import comp1140.ass2.players.Player;

import java.util.*;

import static comp1140.ass2.gui.GameEdge.*;

/**
 * At any time, the current state of the game is encoded as two strings:
 * 1. state[0] is a string representing the states of the playing tiles.
 * It consists of a number of 4-character tile placement strings,
 * each encoding a single tile placement as follows:
 * - 1st character is the color of the playing tile: {'O'=orange or 'B'=black },
 * - 2nd character is the type of the playing tile:
 * * 'a' = Leader, rank = A and strength = 1
 * * 'b' = Leader, rank = B and strength = 3
 * * 'c' = Leader, rank = C and strength = 4
 * * 'd' = Leader, rank = D and strength = 3
 * * 'e' = Leader, rank = E and strength = 2
 * * 'f' = Warrior and strength = 1
 * * 'g' = Warrior and strength = 2
 * * 'h' = Warrior and strength = 3
 * * 'i' = Weft weaver
 * * 'j' = Warp weaver
 * - 3rd character is the first digit of the destination node's id
 * - 4th character is the second digit of the destination node's id
 * <p>
 * Examples:
 * - a strength-one Warrior of the first player on node 6 is encoded as "Of06";
 * - a Weft tile of the second player on node 12 is encoded as "Bi12";
 * - the highest-ranked Leader of the first player on node 23 is encoded as "Oa23".
 * <p>
 * The number of these tile placement strings in state[0] is equal to the number of playing tiles already
 * played, e.g. if `n` tiles have been played so far, the total state[0] length will be `4*n`.
 * <p>
 * 2. state[1] is the current state of edges on the board.
 * <p>
 * The game board consists of a set of nodes indexed from 0 to n-1, and a set of edges indexed from 0 to e-1.
 * For the standard Agamemnon board, n=32 and e=49;
 * for the Loom board, n=33 and e=66.
 * <p>
 * Each edge has two nodes as its endpoints and is encoded as a 5-character string:
 * - 1st character is the edge type: {`L`=Leadership, `S`=Strength, `F`=Force},
 * * NOTE: the Loom variant adds {'E'=Empty} to the edge types.
 * * When playing a Warp tile, 'E' type edges maybe swapped with a neighbor edge of type 'L', 'S' or 'F'.
 * * Edges of type 'E' are not counted in the final game points.
 * - 2nd character is the first digit of endpoint_1's id
 * - 3rd character is the second digit of endpoint_1's id
 * - 4th character is the first digit of endpoint_2's id
 * - 5th character is the second digit of endpoint_2's id
 * <p>
 * Examples:
 * - a Strength edge connecting node 0 to node 1 is encoded as "S0001"
 * - a Leadership edge connecting node 8 to node 13 is encoded as "L0813".
 * - an empty edge (not connected) between nodes 25 and 29 is encoded as "E2529".
 * <p>
 * state[1] is a concatenation of the 5-character encodings of all of edges on the board.
 * During a game, some of these edges may change, e.g. by playing a Warp tile.
 */
// authored by all: see comments below for specific method authorship
public class Agamemnon extends Game {

    public boolean[] occupiedAgamemnonNodes;

    public Agamemnon(Player player1, Player player2) {
        super(player1, player2, AGAMEMNON_CONNECTIVITY);
        nodes = Node.createNodes(new String[]{"", AGAMEMNON_CONNECTIVITY});
        occupiedAgamemnonNodes = new boolean[32];
    }

    private final static int[] TILEMAP = {1, 1, 1, 1, 1, 3, 2, 1, 2, 2};
    public final static String AGAMEMNON_CONNECTIVITY =
            "S0001S0004F0105L0204F0206" +
            "L0203L0306S0307L0408S0409" +
            "S0510F0508F0611S0712F0813" +
            "S0809S0911S1015F1114L1112" +
            "S1216F1217S1315F1314L1418" +
            "L1419F1520L1619S1617F1722" +
            "L1820L1823S1924F1921F2025" +
            "L2126F2122L2226F2325F2324" +
            "F2427S2428L2529L2628L2729" +
            "L2728S2831S2930S3031";

    public final static String[] AGAMEMNON_INITIAL_STATE = {"", AGAMEMNON_CONNECTIVITY};

    /** TASK 2 - authored by Dillon Chen
     * Check whether the input state is well formed or not.
     * <p>
     * To be well-formed:
     * 1- input `state` must consist of two strings,
     * 2- correct string length: for state[0] a multiple of 4 and for state[1] a multiple of 5,
     * 3- each character in the two strings must be in its acceptable range, as explained in the
     * class documentation at the top of this class,
     * e.g. in state[0], 1st, 5th, ... characters must be either 'B' or 'O',
     * and 2nd, 6th, ... characters must be from 'a' to 'j', etc.
     *
     * @param state an array of strings representing the current game state
     * @return true if the input state is well-formed, otherwise false
     */
    public static boolean isStateWellFormed(String[] state) {
        if (state.length != 2 || state[0] == null || state[1] == null
                || state[0].length() % 4 != 0 || state[1].length() % 5 != 0) { return false; }

        // checking if tile state is well formed
        for (int i = 0; i < state[0].length(); i++) {
            char letter = state[0].charAt(i);
            switch (i % 4) {
                case 0:
                    if (!(letter == 'B' || letter == 'O'))
                        return false;
                    break;
                case 1:
                    if (!('a' <= letter && letter <= 'j'))
                        return false;
                    break;
                default:
                    if (!('0' <= letter && letter <= '9'))
                        return false;
            }
        }

        // checking if edge state is well formed
        for (int i = 0; i < state[1].length(); i++) {
            char letter = state[1].charAt(i);
            if (i % 5 == 0) {
                if (!(letter == 'S' || letter == 'L' || letter == 'F' || letter == 'E'))
                    return false;
            } else {
                if (!('0' <= letter && letter <= '9'))
                    return false;
            }
        }
        return true;
    }

    /** TASK 3 - authored by Olin Gao
     * Check whether the input state is valid or not.
     * <p>
     * To be valid:
     * 1- there must be at most one playing tile on each board node,
     * 2- destination nodes' ids must be in range [0 to n-1] inclusive, where `n` is the number of
     * nodes
     * 3- for each type of playing tiles, only the available number of it may be played
     * (e.g. at most one orange rank-A Leader can be on the board)
     * 4- the correct number of pieces must have been played by each player at each turn
     * (one on the first turn, then two on each subsequent turn until the final turn)
     * 5- each pair of nodes must be connected by at most one edge
     *
     * @param state an array of two strings, representing the current game state
     * @return true if the input state is valid and false otherwise.
     */
    public static boolean isStateValid(String[] state) {
        List<String> usedNodes = new ArrayList<>();
        List<String> usedConnections = new ArrayList<>();
        HashMap<String, Integer> piecesUsed = new HashMap<>();

        String placement = state[0];
        String edges = state[1];

        char lastTurn = placement.charAt(0);

        // checking validity of tile state
        for (int i = 0; i < placement.length() / 4; i++) {
            String thisNode = placement.substring(i * 4 + 2, i * 4 + 4);
            String thisPiece = placement.substring(i * 4, i * 4 + 2);
            char thisTurn = placement.charAt(i * 4);

            if (usedNodes.contains(thisNode) || Integer.parseInt(thisNode) >= 32
                    || i % 2 == 1 ^ thisTurn != lastTurn)
                return false;

            if (!piecesUsed.containsKey(thisPiece))
                piecesUsed.put(thisPiece, 0);
            piecesUsed.replace(thisPiece, piecesUsed.get(thisPiece) + 1);

            if (piecesUsed.get(thisPiece) > TILEMAP[thisPiece.charAt(1) - 'a'])
                return false;

            usedNodes.add(thisNode);
            lastTurn = thisTurn;
        }

        // checking validity of edges
        for (int i = 0; i < edges.length() / 5; i++) {
            String thisConnection = edges.substring(i * 5 + 1, i * 5 + 5);
            if (usedConnections.contains(thisConnection))
                return false;
            usedConnections.add(thisConnection);
            usedConnections.add(thisConnection.substring(2,4)+thisConnection.substring(0,2));
        }

        return true;
    }

    /** TASK 5 - authored by Olin Gao
     * Randomly select one or two tiles for the current player.
     * On the first turn (before any pieces have been placed),
     * this method will return a two-character String representing a randomly selected tile.
     * A tile is encoded as 2 characters:
     * - 1st character is the color of the flipped tile, which is 'O' or 'B',
     * - 2nd character is the type of the flipped tile, which is between 'a' and 'j'.
     * On subsequent turns (except the final turn), this method will return a four-character string,
     * representing two tiles for the current player that have not already been placed.
     *
     * @param //tilePlacements a String representing the previously placed tiles,
     *                         equivalent to the first string of the game state
     * @return a String of either two or four characters, representing randomly selected tile(s)
     * that are available to be placed for this turn
     */
    public static String selectTiles(String tilePlacements) {
        Random random = new Random();
        String selection = "";
        int tilesPlaced = tilePlacements.length() / 4;
        Character[][] validTiles = getValidTiles(tilePlacements);
        List<Character> validTilesO = Arrays.asList(validTiles[0]);
        List<Character> validTilesB = Arrays.asList(validTiles[1]);

        if (tilesPlaced % 4 == 1) {
            selection += "B" + validTilesB.get(random.nextInt(validTilesB.size()));
            if (tilesPlaced != 29)
                selection += "B" + validTilesB.get(random.nextInt(validTilesB.size()));
        } else {
            selection += "O" + validTilesO.get(random.nextInt(validTilesO.size()));
            if (tilesPlaced != 0)
                selection += "O" + validTilesO.get(random.nextInt(validTilesO.size()));
        }

        return selection;
    }

    public static Character[][] getValidTiles(String tilePlacements){
        int tilesPlaced = tilePlacements.length() / 4;
        HashMap<Character, Integer> tilesO = new HashMap<>();
        HashMap<Character, Integer> tilesB = new HashMap<>();
        List<Character> validTilesO = new ArrayList<>();
        List<Character> validTilesB = new ArrayList<>();

        for (int i = 0; i < tilesPlaced; i++) {
        char tile = tilePlacements.charAt(i * 4 + 1);
        if (tilePlacements.charAt(i * 4) == 'O')
            if (tilesO.containsKey(tile))
                tilesO.replace(tile, tilesO.get(tile) + 1);
            else
                tilesO.put(tile, 1);
        else if (tilesB.containsKey(tile))
            tilesB.replace(tile, tilesB.get(tile) + 1);
        else
            tilesB.put(tile, 1);
    }

        for (int i = 'a'; i <= 'j'; i++) {
            char tile = (char) i;
            if (!tilesO.containsKey(tile))
                validTilesO.add(tile);
            else if (tilesO.get(tile) < TILEMAP[tile - 'a'])
                validTilesO.add(tile);

            if (!tilesB.containsKey(tile))
                validTilesB.add(tile);
            else if (tilesB.get(tile) < TILEMAP[tile - 'a'])
                validTilesB.add(tile);
        }
        return new Character[][]{validTilesO.toArray(new Character[0]), validTilesB.toArray(new Character[0])};
    }

    /** TASK 6 - authored by Dillon Chen
     * Check whether a playing action is valid or not.
     * <p>
     * A playing action is a variable length string, consisting of one or two sub-actions.
     * A sub-action represents playing a single tile of any type:
     * 1- if the tile is not a Warp weaver, the sub-action is a 4-character string
     * representing the placement of a playing tile on a board cell.
     * It follows exactly the same encoding as state[0] (available above this file).
     * 2- if the tile is a Warp weaver, the sub-action is a 8-character string,
     * in which the first 4 characters represent the placement of the Warp tile,
     * and the next 4 characters represent the swapping of two edges,
     * and is encoded as:
     * - 1st character is the left digit of endpoint_1's id + '0'
     * - 2nd character is the right digit of endpoint_1's id + '0'
     * - 3rd character is the left digit of endpoint_2's id + '0'
     * - 4th character is the right digit of endpoint_2's id + '0'
     * , where endpoint_1 and endpoint_2 are the other endpoints of the two edges
     * to be swapped (one end will be the node in action[0],
     * where the Warp is being placed).
     * NOTE_1: endpoint_1 and endpoint_2 can be equal. In that case, no edges will be swapped.
     * <p>
     * NOTE_2: All playing actions include two sub-actions, except the first and the last actions of
     * player_1 (the player who starts the game), which consist of only one sub-action.
     * <p>
     * To be valid, an action must:
     * 0- target tiles which have not already been played
     * 1- include a valid number of sub-actions (see NOTE_2),
     * 2- each sub-action must:
     * * 2.1- must have the correct string length (8 for Warp and 4 otherwise),
     * * 2.2- target an empty node,
     * * 2.3- (for Warp tiles) swap two valid edges. To be valid,
     * endpoint_1 and endpoint_2 must be neighbours of the target node.
     *
     * @param state  an array of strings, representing the current game state
     * @param action a string, representing a playing action
     * @return true  is 'action` is a valid playing action, and false otherwise
     */
    public static boolean isActionValid(String[] state, String action) {
        if (state[0].length() == 0 && action.charAt(0) == 'B') { return false; }
        String[] subActions = actionToSubActions(action);
        String action1 = subActions[0];
        String action2 = subActions[1];
        if (action1 == null || action2 == null) { return false; }
        if (action2.length() == 0)
            return action1.charAt(0) == 'O' ?
                    state[0].length() == 0 && isSubActionValid(state, action1) :
                    state[0].length() == 120 && isSubActionValid(state, action1);
        else
            return isSubActionValid(state, action1) && isSubActionValid(state, action2) &&
                    (!action1.substring(2,4).equals(action2.substring(2,4)))
                    && action1.charAt(0) == action2.charAt(0);
    }

    // divides an action of two tiles into an String array of two sub actions
    public static String[] actionToSubActions(String action) {
        String[] out = new String[2];
        if ('a' <= action.charAt(1) && action.charAt(1) <= 'i') {
            out[0] = action.substring(0, 4);
            out[1] = action.substring(4);
        }
        if (action.charAt(1) == 'j' && action.length() >= 8) {
            out[0] = action.substring(0, 8);
            out[1] = action.substring(8);
        }
        return out;
    }

    // checks if a single tile placement is valid
    private static boolean isSubActionValid(String[] state, String subAction) {
        List<String> occupiedNodes = stateToOccupiedNodes(state);
        int[][] tilesUsed = stateToTilesUsed(state);
        List<String> edgeLocations = stateToEdges(state);

        char player = subAction.charAt(0);
        char tileType = subAction.charAt(1);
        String node = subAction.substring(2, 4);

        if ('a' <= tileType && tileType <= 'i') {
            if (player == 'O')
                return !occupiedNodes.contains(node)
                        && tilesUsed[0][tileType - 'a'] < TILEMAP[tileType - 'a'];
            if (player == 'B')
                return !occupiedNodes.contains(node)
                        && tilesUsed[1][tileType - 'a'] < TILEMAP[tileType - 'a'];
        }
        if (tileType == 'j' && subAction.length() == 8) {
            String warp1 = orderConnection(subAction.substring(2,4) + subAction.substring(4,6));
            String warp2 = orderConnection(subAction.substring(2,4) + subAction.substring(6,8));
            if (player == 'O')
                return !occupiedNodes.contains(node)
                        && tilesUsed[0][tileType - 'a'] < TILEMAP[tileType - 'a']
                        && edgeLocations.contains(warp1)
                        && edgeLocations.contains(warp2);
            if (player == 'B')
                return !occupiedNodes.contains(node)
                        && tilesUsed[1][tileType - 'a'] < TILEMAP[tileType - 'a']
                        && edgeLocations.contains(warp1)
                        && edgeLocations.contains(warp2);
        }
        return false;
    }

    // reorders a String of two consecutive integers such that the first number is smaller
    public static String orderConnection(String s) {
        if (Integer.parseInt(s.substring(0,2)) < Integer.parseInt(s.substring(2,4)))
            return s;
        else
            return s.substring(2,4) + s.substring(0,2);
    }

    // returns a list of integer representation of nodes that are occupied
    public static List<String> stateToOccupiedNodes(String[] state) {
        List<String> occupiedNodes = new ArrayList<>();
        String state0 = state[0];
        for (int i = 0; i < state0.length(); i += 4) {
            String node = state0.substring(i + 2, i + 4);
            occupiedNodes.add(node);
        }
        return occupiedNodes;
    }

    // returns a hashmap where the value for each array index represents number of that tile used
    public static int[][] stateToTilesUsed(String[] state) {
        int[] outO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] outB = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        String state0 = state[0];
        for (int i = 0; i < state0.length(); i += 4) {
            char player = state0.charAt(i);
            char letter = state0.charAt(i + 1);
            if (player == 'O')
                outO[letter - 'a']++;
            if (player == 'B')
                outB[letter - 'a']++;
        }
        return new int[][]{outO, outB};
    }

    // returns a list of length 4 string representation of edge locations
    public static List<String> stateToEdges(String[] state) {
        List<String> out = new ArrayList<>();
        String state1 = state[1];
        for (int i = 0; i < state1.length(); i += 5) {
            String edgeLocation = state1.substring(i + 1, i + 5);
            out.add(edgeLocation);
        }
        return out;
    }

    /** TASK 7 - authored by Dillon Chen
     * Given the current game state and a playing action, calculate the updated game state.
     *
     * @param state  an array of two strings, representing the current game state
     * @param action a string, representing a playing action
     * @return an array of two strings, representing the game state after applying `action`
     */
    public static String[] applyAction(String[] state, String action) {
        String subAction1 = actionToSubActions(action)[0];
        String subAction2 = actionToSubActions(action)[1];
        return (subAction2.length()==0?applySubAction(state, subAction1):applySubAction(applySubAction(state, subAction1), subAction2));
    }

    public static String[] applySubAction(String[] state, String subAction) {
        String nodeState = state[0];
        String edgeState = state[1];

        // put tile in front of node state string
        nodeState += subAction.substring(0, 4);

        if (subAction.charAt(1) == 'j') { // case where the tile type is Warp
            String connection1 = orderConnection(subAction.substring(2,4) + subAction.substring(4,6));
            String connection2 = orderConnection(subAction.substring(2,4) + subAction.substring(6,8));

            // edgeStateArray = fixed edges + swapped 1 + fixed edges + swapped 2 + fixed edges
            String[] edgeStateArray = new String[5];
            char[] swapChar = new char[2];
            String[] swap = new String[2];

            int counter = 0;
            int edgeStateArray3Index = 0;
            for (int i = 0; i < edgeState.length(); i += 5) {
                char currentEdgeType = edgeState.charAt(i);
                String currentEdgeLocation = edgeState.substring(i + 1, i + 5);
                if (currentEdgeLocation.equals(connection1)
                        || currentEdgeLocation.equals(connection2)) {
                    if (counter == 0) {
                        edgeStateArray[0] = edgeState.substring(0, i);
                        edgeStateArray3Index = i + 5;
                    }
                    if (counter == 1) {
                        edgeStateArray[2] = edgeState.substring(edgeStateArray3Index, i);
                        edgeStateArray[4] = edgeState.substring(i + 5);
                    }
                    swapChar[counter] = currentEdgeType;
                    swap[counter] = currentEdgeLocation;
                    counter++;
                }
                if (counter == 2) {
                    edgeStateArray[1] = swapChar[1] + swap[0];
                    edgeStateArray[3] = swapChar[0] + swap[1];
                    String out = "";
                    for (String s : edgeStateArray) out += s;
                    edgeState = out;
                    break;
                }
            }
        }
        String[] out = {nodeState, edgeState};
        return out;
    }


    /** TASK 8 - authored by Olin Gao
     * Given a game state, calculate the total number of edges won by each player.
     *
     * @param state an array of two strings, representing the current game state
     * @return an array of two integers, where:
     * * result[0] includes the points earned by the Orange player (player_1)
     * * result[1] includes the points earned by the Black player (player_2)
     */
    public static int[] getTotalScore(String[] state) {
        Scorer.clearTraversed();
        nodes = Node.createNodes(state);
        int greeceScore = 0;
        int troyScore = 0;

        for(Node node : nodes){
            if(!Scorer.getTraversed().contains(node.getId())&&node.getTile()!=null){
                int[] leaderStats = Scorer.scoreLeaders(node.getId(), nodes);
                int score = leaderStats[0];
                int greek = leaderStats[1];
                int trojan = leaderStats[2];

                for(int p : Scorer.PRIMES){
                    if(greek%p==0&&trojan%p!=0) {
                        greeceScore += score;
                        break;
                    }
                    else if(greek%p!=0&&trojan%p==0) {
                        troyScore += score;
                        break;
                    }
                }
            }
        }
        Scorer.clearTraversed();
        for(Node node : nodes){
            if(!Scorer.getTraversed().contains(node.getId())&&node.getTile()!=null){
                int[] strStats = Scorer.scoreStrength(node.getId(), nodes);
                int score = strStats[0];
                int greek = strStats[1];
                int trojan = strStats[2];

                if(greek>trojan)
                    greeceScore+=score;
                else if(trojan>greek)
                    troyScore+=score;
            }
        }
        Scorer.clearTraversed();
        for(Node node : nodes){
            if(!Scorer.getTraversed().contains(node.getId())&&node.getTile()!=null){
                int[] forceStats = Scorer.scoreForce(node.getId(), nodes);
                int score = forceStats[0];
                int greek = forceStats[1];
                int trojan = forceStats[2];

                if(greek>trojan)
                    greeceScore+=score;
                else if(trojan>greek)
                    troyScore+=score;
            }
        }
        Scorer.clearTraversed();
        int[] specialWeftScores = Scorer.scoreDisconnectedWefts(nodes);
        greeceScore += specialWeftScores[0];
        troyScore += specialWeftScores[1];

        int[] finalScore = {greeceScore, troyScore};
        return finalScore;
    }



    /** TASK 10 - authored by Dillon Chen
     * Given the current game state, and one or two flipped playing tiles,
     * generate a valid playing action.
     * <p>
     * A playing action is variable length string, consisting of one or two sub-actions.
     * NOTE: The choice between one or two is explained in {@link #isActionValid(String[], String)}
     * <p>
     * To be valid, the playing action must:
     * 1- include all target playing tiles,
     * 2- have all of the conditions explained in {@link #isActionValid(String[], String)}
     *
     * @param state an array of two strings, representing the current game state
     * @param tiles a string representing one or two flipped playing tiles, as described in
     *              {@link #selectTiles(String)}
     * @return a string representing a playing action on the target tile(s)
     */
    public static String generateAction(String[] state, String tiles) {
        return MonteCarlo.monteMove(state, tiles);
    }

    /* The following methods are authored by Olin Gao */

    public static String generateRandomAction(String[] state, String tiles) {
        boolean agamemnon = state[1].length() != 330;
        String action1;
        String action2;

        List<String> unoccupiedNodes = stateToUnoccupiedNodes(state);
        int nodeCount = unoccupiedNodes.size();
        int[] xy = distinctRandom(nodeCount);
        int x = xy[0];
        int y = xy[1];

        if (tiles.length()==4) {
            action1 = tiles.substring(0,2)+unoccupiedNodes.get(x);
            action2 = tiles.substring(2)  +unoccupiedNodes.get(y);

            int[] connectionsX = getNodeConnections(agamemnon)[Integer.parseInt(unoccupiedNodes.get(x))];
            int[] connectionsY = getNodeConnections(agamemnon)[Integer.parseInt(unoccupiedNodes.get(y))];


            if (action1.charAt(1) == 'j') {
                int[] cs = distinctRandom(connectionsX.length);
                action1 +=
                        Node.idTo2String((int) connectionsX[cs[0]])
                                + Node.idTo2String((int) connectionsX[cs[1]]);
            }
            if (action2.charAt(1) == 'j') {
                int[] cs = distinctRandom(connectionsY.length);
                action2 +=
                        Node.idTo2String((int) connectionsY[cs[0]])
                                + Node.idTo2String((int) connectionsY[cs[1]]);
            }
            return action1+action2;
        } else {

            int[] connectionsX = getNodeConnections(agamemnon)[Integer.parseInt(unoccupiedNodes.get(x))];

            action1 = tiles+unoccupiedNodes.get(x);
            if (action1.charAt(1) == 'j') {
                int[] cs = distinctRandom(connectionsX.length);
                action1 +=
                        Node.idTo2String((int) connectionsX[cs[0]])
                                + Node.idTo2String((int) connectionsX[cs[1]]);
            }
            return action1;
        }
    }

    private static int[] distinctRandom(int high){
        int x;
        int y;
        Random random = new Random();
        x = random.nextInt(high);
        int preY = random.nextInt(high-1);
        y = preY+boolToInt(preY>=x);
        return new int[]{x,y};
    }

    private static int[] distinctRandom(int low, int high){
        int x;
        int y;
        Random random = new Random();
        x = random.nextInt(high-low)+low;
        int preY = random.nextInt(high-low-1);
        y = preY+boolToInt(preY>=x)+low;
        return new int[]{x,y};
    }

    private static int boolToInt(boolean b){
        return b?1:0;
    }

    public static ArrayList<String> stateToUnoccupiedNodes(String[] state) {
        ArrayList<String> unoccupiedNodes = new ArrayList<>();
        for (int i = 0; i < (state[1].length()==245?32:33); i++) {
            String nodeID = Node.idTo2String(i);
            if (!state[0].contains(nodeID))
                unoccupiedNodes.add(nodeID);
        }
        return unoccupiedNodes;
    }

    public static Faction turn(String tiles){
        return (tiles.length()/4%4>1)?Faction.Greek:Faction.Trojan;
    }
}

