package comp1140.ass2.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// authored by Olin Gao
public class Node { // nodes on the board on which tiles are played

    public static int totalNodeCount = 32;

    private int id;         // location of the node
    private Tile tile;      // the tile on this node, if present

    // HashMap of nodes connected to this, and value for each key representing the edge type
    private HashMap<Node, EdgeType> connections = new HashMap<>();

    public Node(int id, HashMap<Node, EdgeType> connections) {
        this.id = id;
        this.connections = connections;
    }

    public Node(int id) {
        this.id = id;
    }

    public void placeTile(Tile tile) {
        this.tile = tile;
    }

    public int getId() {
        return this.id;
    }

    public Tile getTile(){
        return this.tile;
    }

    public HashMap<Node, EdgeType> getConnectedNodes() { return connections; } // Returns set of connected nodes.

    // assigns HashMap<Node, Edge> connections to each node - doesn't consider tiles yet
    public static void assignEdges(List<Node> nodes, String edges) {
        int edgeCount = edges.length() / 5;

        for (int i = 0; i < edgeCount; i++) {
            String sequence = edges.substring(i * 5, i * 5 + 5);
            int fromId = stringToId(sequence.substring(1, 3));
            Node fromNode = getNode(nodes, fromId);

            int toId = stringToId(sequence.substring(3, 5));
            Node toNode = getNode(nodes, toId);

            EdgeType type = EdgeType.charToEdgeType(sequence.charAt(0));

            //we pick a list of nodes with max id of list < max id of connections to avoid nullPointerException
            if(fromNode!=null&&toNode!=null) {
                fromNode.connections.put(toNode, type);
                toNode.connections.put(fromNode, type);
            }
        }
    }

    // returns an updated list of nodes after tiles have been placed
    public static void placeTiles(List<Node> nodes, String state0) {
        for (int i = 0; i < state0.length() / 4; i++) {
            String sequence = state0.substring(i * 4, i * 4 + 4);
            int placeId = stringToId(sequence.substring(2, 4));
            Tile tileToPlace = new Tile(sequence);
            Node thisNode = getNode(nodes, placeId);
            if(thisNode!=null)
                getNode(nodes, placeId).placeTile(tileToPlace);
           // System.out.println("At: " + thisNode.toString());
           // System.out.println();
        }
    }

    // creates nodes
    public static List<Node> createNodes(String[] state){
        String placements = state[0];
        String edges = state[1];
        List<Node> nodes = generateEmptyNodes(totalNodeCount);
        assignEdges(nodes, edges);
        placeTiles(nodes, placements);

        return nodes;
    }

    // reorders a length 4 string such that the first two digits are less than the remaining two
    public static String reorderString(String string) {
        if (Integer.valueOf(string.substring(0,2)) > Integer.valueOf(string.substring(2,4)))
            return string.substring(2,4) + string.substring(0,2);
        else
            return string.substring(0,2) + string.substring(2,4);
    }

    // converts an integer representation of Node location to a string of length 2
    public static String idTo2String(int myId) {
        if (myId < 10)
            return "0" + myId;
        else
            return String.valueOf(myId);
    }

    // converts a string representation of Node location to an integer
    private static int stringToId(String s) {
        try {
            return Integer.valueOf(s);
        }
        catch(Exception e){
            return -1;
        }
    }

    // generates a list of n nodes
    public static List<Node> generateEmptyNodes(int n) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new Node(i));
        }
        return nodes;
    }

    // get node
    private static Node getNode(List<Node> nodes, int myId) {
        for (Node node : nodes) {
            if (node.getId() == myId)
                return node;
        }
        return null;
    }

    @Override
    public String toString() {
        String connectString = "";
        for (Node node : connections.keySet()){
            connectString+= "Node " + node.getId() + ": " + connections.get(node)+", ";
        }
        String tileString = "None";
        if(tile!=null)
            tileString = tile.getTileType().toString();

        connectString+="||";
        return "Node{" +
                "id= " + id +
                ", tile= " + tileString + " connections= " + connectString + "}";
    }
}
