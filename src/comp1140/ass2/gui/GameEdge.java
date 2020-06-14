package comp1140.ass2.gui;

import static comp1140.ass2.gui.GameBoard.*;
import static comp1140.ass2.gui.GameTile.*;

/**
 * Methods for finding coordinates and rotations of game edges for display.
 * Authored by Dillon Chen
 */
public class GameEdge {

    // find the setLayout coordinates of edges given node locations
    static double[] findCoordinates(String edgeEncoding, boolean agamemnon) {
        String node1 = edgeEncoding.substring(0,2);
        String node2 = edgeEncoding.substring(2,4);
        double x = (getCoordinates(node1, agamemnon)[0]+getCoordinates(node2, agamemnon)[0])/2 - (SQUARE_SIZE/4);
        double y = (getCoordinates(node1, agamemnon)[1]+getCoordinates(node2, agamemnon)[1])/2 + (SQUARE_SIZE/4);
        return new double[]{x, y};
    }

    // finds the rotation of an edge using its encoding
    static int findRotation(String edgeEncoding, boolean agamemnon) {
        double[] p1 = getCoordinates(edgeEncoding.substring(0,2), agamemnon);
        double[] p2 = getCoordinates(edgeEncoding.substring(2,4), agamemnon);

        if (Math.abs(p1[0] - p2[0]) < 5)
            return p1[1] > p2[1] ? 90 : -90;
        if (Math.abs(p1[1] - p2[1]) < 5)
            return 0;

        int rotation = (int) Math.toDegrees(Math.atan((p2[1]-p1[1])/(p2[0]-p1[0])));
        if (Math.abs(Math.abs(rotation) - 30) < 5)
            return (int) -Math.signum(rotation)*30;
        else
            return (int) -Math.signum(rotation)*60;
    }

    public static int[][] getNodeConnections(boolean agamemnon) {
        return agamemnon?AGAMEMNON_NODE_CONNECTIONS:LOOM_NODE_CONNECTIONS;
    }

    public static final int[][] AGAMEMNON_NODE_CONNECTIONS = {
                    {1,4},            // node 0
                    {0,5},
                    {3,4,6},
                    {2,6,7},
                    {0,2,8,9},
                    {1,8,10},         // node 5
                    {2,3,11},
                    {3,12},
                    {4,5,9,13},
                    {4,8,11},
                    {5,15},           // node 10
                    {6,9,12,14},
                    {7,11,16,17},
                    {8,14,15},
                    {11,13,18,19},
                    {10,13,20},       // node 15
                    {12,17,19},
                    {12,16,22},
                    {14,20,23},
                    {14,16,21,24},
                    {15,18,25},       // node 20
                    {19,22,26},
                    {17,21,26},
                    {18,24,25},
                    {19,23,27,28},
                    {20,23,29},       // node 25
                    {21,22,28},
                    {24,28,29},
                    {24,26,27,31},
                    {25,27,30},
                    {29,31},          // node 30
                    {28,30}
            };

    public static final int[][] LOOM_NODE_CONNECTIONS = new int[][]
            {
                    {1,2,4},          // node 0
                    {0,4,5},
                    {0,3,4,6},
                    {2,6,7},
                    {0,1,2,8,9},
                    {1,8,10},         // node 5
                    {2,3,7,9,11},
                    {3,6,12},
                    {4,5,9,10,13},
                    {4,6,8,11,14},
                    {5,8,13,15},      // node 10
                    {6,9,12,16},
                    {7,11,16,17},
                    {8,10,14,15,18},
                    {9,13,18,19},
                    {10,13,20},       // node 15
                    {11,12,17,19,21},
                    {12,16,22},
                    {13,14,19,20,23},
                    {14,16,18,21,24},
                    {15,18,23,25},    // node 20
                    {16,19,22,24,26},
                    {17,21,27},
                    {18,20,24,25,28},
                    {19,21,23,26,28},
                    {20,23,29},       // node 25
                    {21,24,27,30,31},
                    {22,26,31},
                    {23,24,29,30,32},
                    {25,28,32},
                    {26,28,31,32},    // node 30
                    {26,27,30},
                    {28,29,30}
            };
}
