package comp1140.ass2.gui;

import org.junit.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import static comp1140.ass2.gui.GameBoard.*;
import static comp1140.ass2.gui.GameEdge.*;
import static comp1140.ass2.gui.GameTile.*;
import static org.junit.Assert.*;

/**
 * Tests for GUI, specifically for displaying state
 * Authored by Dillon Chen
 */
public class GUITests {

    private HashMap<String, Integer> agamemnonEdgeRotationMap = new HashMap<>();
    private HashMap<String, Integer> loomEdgeRotationMap = new HashMap<>();

    {
        try {
            StringTokenizer s;
            String sString;

            BufferedReader agamemnonEdgeRotations = new BufferedReader(new FileReader("assets/test_data/agam_edge_rot.txt"));
            while (true) {
                sString = agamemnonEdgeRotations.readLine();
                if (sString == null) break;
                s = new StringTokenizer(sString);
                String edgeEncoding = s.nextToken();
                Integer rotation = Integer.parseInt(s.nextToken());
                agamemnonEdgeRotationMap.put(edgeEncoding, rotation);
            }

            BufferedReader loomEdgeRotations = new BufferedReader(new FileReader("assets/test_data/loom_edge_rot.txt"));
            while (true) {
                sString = loomEdgeRotations.readLine();
                if (sString == null) break;
                s = new StringTokenizer(sString);
                String edgeEncoding = s.nextToken();
                Integer rotation = Integer.parseInt(s.nextToken());
                loomEdgeRotationMap.put(edgeEncoding, rotation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEdgeNumber() {
        assertEquals(49, agamemnonEdgeRotationMap.size());
        assertEquals(66, loomEdgeRotationMap.size());
    }

    @Test
    public void testAgamemnonFindRotation() {
        for (var edgeEncoding : agamemnonEdgeRotationMap.keySet()) {
            int test = findRotation(edgeEncoding, true);
            int expected = agamemnonEdgeRotationMap.get(edgeEncoding);
            assertEquals("expected "+expected+" but got "+test+" for agamemnon edge encoding "+edgeEncoding, test, expected);
        }
    }

    @Test
    public void testLoomFindRotation() {
        for (var edgeEncoding : loomEdgeRotationMap.keySet()) {
            int test = findRotation(edgeEncoding, false);
            int expected = loomEdgeRotationMap.get(edgeEncoding);
            assertEquals("expected "+expected+" but got "+test+" for loom edge encoding "+edgeEncoding, test, expected);
        }
    }

    @Test
    public void testEdgeLength() {
        for (var edgeEncoding : agamemnonEdgeRotationMap.keySet()) {
            String node1 = edgeEncoding.substring(0,2);
            String node2 = edgeEncoding.substring(2,4);
            double x1 = getCoordinates(node1, true)[0]; double y1 = getCoordinates(node1, true)[1];
            double x2 = getCoordinates(node2, true)[0]; double y2 = getCoordinates(node2, true)[1];
            double length = Math.abs(Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)));
            assertTrue("expected length of "+edgeEncoding+" to be approximately "+NODE_DIFF+" but got "+length, Math.abs(length-NODE_DIFF)<5);
        }
    }

    @Test
    public void testAgamemnonAdjacentNodeNumber() {
        int[] adjacentNodeNumbers =
                {
                        2, 2, 3, 3, 4,
                        3, 3, 2, 4, 3,
                        2, 4, 4, 3, 4,
                        3, 3, 3, 3, 4,
                        3, 3, 3, 3, 4,
                        3, 3, 3, 4, 3,
                        2, 2
                };
        for (int i = 0; i < AGAMEMNON_NODE_CONNECTIONS.length; i++) {
            int test = AGAMEMNON_NODE_CONNECTIONS[i].length;
            int expected = adjacentNodeNumbers[i];
            assertEquals("expected node "+i+" to have "+expected+" adjacent nodes but got "+test, expected, test);
        }
    }

    @Test
    public void testLoomAdjacentNodeNumber() {
        int[] adjacentNodeNumbers =
                {
                        3, 3, 4, 3, 5,
                        3, 5, 3, 5, 5,
                        4, 4, 4, 5, 4,
                        3, 5, 3, 5, 5,
                        4, 5, 3, 5, 5,
                        3, 5, 3, 5, 3,
                        4, 3, 3
                };
        for (int i = 0; i < LOOM_NODE_CONNECTIONS.length; i++) {
            int test = LOOM_NODE_CONNECTIONS[i].length;
            int expected = adjacentNodeNumbers[i];
            assertEquals("expected node "+i+" to have "+expected+" adjacent nodes but got "+test, expected, test);
        }
    }
}
