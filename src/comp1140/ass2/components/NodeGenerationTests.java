package comp1140.ass2.components;

import comp1140.ass2.players.Faction;
import jdk.dynalink.NoSuchDynamicMethodException;
import org.junit.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import comp1140.ass2.components.Node;
import comp1140.ass2.components.Tile;

import static org.junit.Assert.*;

// authored by Olin Gao
public class NodeGenerationTests {
    private Random random = new Random();
    private static final int MAX_NODE_COUNT = 200;

    @Test
    public void testEmpty(){
        List<Node> nodes = new ArrayList<>();
        for(int i=0; i<50; i++) {
            int size = random.nextInt(MAX_NODE_COUNT);
            nodes = Node.generateEmptyNodes(size);
            for (Node n : nodes) {
                assertTrue("List of nodes exceeds expected size (" + size + ").", n.getId() < size);
                assertNull("Node in list of empty nodes has assigned tile.", n.getTile());
                assertEquals("Node in list of empty nodes has assigned connections.", n.getConnectedNodes().size(), 0);
            }
        }
    }

    private int valueOf(String s){
        if(s.charAt(0)=='0')
            return Integer.valueOf(""+s.charAt(1));
        else
            return Integer.valueOf(s);
    }

    @Test
    public void testEdges(){
        String edges = "S0001S0004F0105L0204F0206" +
                "L0203L0306S0307L0408S0409" +
                "S0510F0508F0611S0712F0813" +
                "S0809S0911S1015F1114L1112" +
                "S1216F1217S1315F1314L1418" +
                "L1419F1520L1619S1617F1722" +
                "L1820L1823S1924F1921F2025" +
                "L2126F2122L2226F2325F2324" +
                "F2427S2428L2529L2628L2729" +
                "L2728S2831S2930S3031";
        List<Node> nodes = Node.generateEmptyNodes(32);
        Node.assignEdges(nodes, edges);

        for(Node n : nodes){
            int len = edges.length()/5;
            for(int i=0; i<len; i++){
                if(valueOf(edges.substring(i*5+1,i*5+3))==n.getId())
                    assertTrue("Node " + n.getId() + " is not connected to a " + edges.charAt(i*5) + " edge.",
                            n.getConnectedNodes().containsValue(EdgeType.charToEdgeType(edges.charAt(i*5))));
            } //Note: we do not have to check the nodes which each node is connected to, since this loops through all nodes in the list.
        }

    }

    @Test
    public void testTiles(){
        String[] placements = new String[]{
                "Oi19",
                "Oi19Bf21Ba22",
                "Oi19Bf21Bi26Oj27Of11",
                "Oi19Bf21Bi26Oj27Oc11Bf30Bf22",
                "Oi19Bf21Bi26Og27Of11Bf30Bf22Og03Ob04",
                "Oc00Ba01Bb02Oa03Ob05Bj04Bg06Oe07Oj08",
                "Oh08Be24Bg00Og14Ob03Bd04Bi30Oi17",
                "Oc00Ba01Bb02Oa03Ob05Bj04Bg06Oe07Oj08Bh09Bi10Oh12Og17Bf11Bd14Oi19Oi22Bc15Bg16Of23Og24Bj18Bf21Od25Oj27Bi26Bf28Of30Of31Be29",
                "Od26Bc11Bb21Oc27Oj10Bj25Ba22Oa20Of29Bf08Bh19Oi09Og24Bf00Bi15Og06Oe16Bf18Bg14Of28Oh01Bg13Bj31Oj12Of02Bd07Bi17Oi05Ob04Be03",
                "Of31Bd11Bg19Od15Oc21Bc18Bf03Oe25Of05Bi01Bi28Oj12Oa06Bj08Bf07Oi23Oj17Be29Bg22Ob16Oh02Bb10Bh14Og13Oi09Ba26Bj30Of27Og04Bf24",
                "Ob25Bj17Be15Of01Oj18Bf22Bg31Of03Oi19Bh29Bi23Oh09Of10Bj24Bf05Oa16Oj00Bg02Bb20Og08Og27Bi14Ba21Od06Oe28Bd11Bc04Oi13Oc26Bf30",
                "Oh08Be24Bg00Og14Ob03Bd04Bi30Oi17Oc12Bf16Bh28Of09Oj31Bb21Bi01Of10Of07Bf29Bj27Oj26Oe15Bf20Ba19Od02Oi11Bj05Bg23Oa22Og13Bc18",
                "Oh10Ba25Bf28Of05Od24Bj23Bf03Og20Of11Bd12Bb22Oa31Ob14Be15Bg18Of07Oj00Bg09Bf17Og01Oi30Bc06Bj21Oj02Oc27Bi13Bh26Oe29Oi16Bi08",
                "Oi17Bh08Bg06Oa14Oe23Bg24Be12Od16Oj26Bi31Bb01Og28Ob20Bc10Bf25Oc11Of15Bj05Bi21Og02Of19Bd07Bf04Oj27Oh03Ba22Bf30Oi29Of09Bj13",
                "Ob31Be30Bg12Od14Of05Bf28Bh20Of23Oa09Bj18Ba27Oj10Oi19Bj26Bc04Og02Oi00Bb25Bd07Oc24Oj13Bf22Bi16Oh11Of21Bg01Bf06Og17Oe15Bi08",
                "Og30Bh11Bf23Oh14Oa04Bb21Ba20Of02Od27Bi13Bc05Of06Og28Bg29Be24Oe00Oj19Bg22Bf07Oi26Oj09Bj15Bd10Oc03Oi18Bj25Bi31Of16Ob12Bf01",
                "Ob24Bf19Bc07Og00Of05Ba10Bh18Oi25Oe17Bb21Bf26Oa29Oj22Bf13Bg01Of28Oc09Bj03Bg11Od31Oj27Bd15Bi04Og08Oi12Be20Bi16Oh14Of06Bj23"
        };
        for(String p : placements) {
            List<Node> nodes = Node.generateEmptyNodes(32);
            Node.placeTiles(nodes, p);
            int len = p.length()/4;
            for(int i=0; i<len; i++) {
                assertEquals("For placement " + p + ", expected node " + p.substring(i*4+2,i*4+4) + " to have tile type " + p.charAt(i*4+1),
                        nodes.get(valueOf(p.substring(i*4+2,i*4+4))).getTile().getTileType(), TileType.charToTileType(p.charAt(i*4+1)));
                assertEquals("For placement " + p + ", expected node " + p.substring(i*4+2,i*4+4) + " to have faction " + p.charAt(i*4)
                        , nodes.get(valueOf(p.substring(i*4+2,i*4+4))).getTile().getFaction(), Faction.charToFaction(p.charAt(i*4)));
            }
        }
    }

}
