package comp1140.ass2;

import comp1140.ass2.components.Node;
import comp1140.ass2.players.Player;

import java.util.HashMap;
import java.util.Random;

// authored by Dillon Chen
public class Loom extends Game {

    public boolean[] occupiedLoomNodes;

    public Loom(Player player1, Player player2) { // for generating random edge mapping
        super(player1, player2, keyToEdgeMapping((int) Math.abs(System.nanoTime() % 24)));
        nodes = Node.createNodes(new String[]{"", edges});
        occupiedLoomNodes = new boolean[33];
    }

    public Loom(Player player1, Player player2, int edgeMappingKey) { // for defined edge mapping
        super(player1, player2, keyToEdgeMapping(edgeMappingKey));
        nodes = Node.createNodes(new String[]{"", edges});
        occupiedLoomNodes = new boolean[33];
    }

    public final static String LOOM_CONNECTIVITY =
            "A0001B0002C0004D0104B0105B0203A0204A0206" +
            "B0306B0307D0408C0409D0508A0510A0607A0609" +
            "D0611C0712B0809B0810A0813C0911B0914C1013" +
            "A1015D1112C1116C1216D1217D1314A1315C1318" +
            "B1418D1419A1520A1617D1619A1621C1722A1819" +
            "B1820A1823B1921D1924D2023B2025A2122A2124" +
            "B2126D2227D2324A2325C2328B2426B2428C2529" +
            "B2627D2630C2631C2731A2829C2830D2832D2932C3031D3032";
    private final static String[] EDGE_PERMUTATIONS =
            { "ABCD", "ABDC", "ACBD", "ACDB", "ADBC", "ADCB"
            , "BACD", "BADC", "BCAD", "BCDA", "BDAC", "BDCA"
            , "CABD", "CADB", "CBAD", "CBDA", "CDAB", "CDBA"
            , "DABC", "DABC", "DBAC", "DBCA", "DCAB", "DCBA"};
    private final static char[] EDGE_TYPES = {'S', 'L', 'F', 'E'};

    private static String permutationToMapping(String permutation) {
        HashMap edgeTypeMap = new HashMap<Character, Integer>();
        for (int i = 0; i < permutation.length(); i++) {
            edgeTypeMap.put(permutation.charAt(i), i);
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < LOOM_CONNECTIVITY.length(); i += 5) {
            out.append(EDGE_TYPES[(int) edgeTypeMap.get(LOOM_CONNECTIVITY.charAt(i))]);
            out.append(LOOM_CONNECTIVITY.substring(i + 1, i + 5));
        }
        return out.toString();
    }

    private static String keyToEdgeMapping(int edgeMappingKey) {
        return permutationToMapping(EDGE_PERMUTATIONS[edgeMappingKey]);
    }

    public static int edgePermutationToKey(String edgePermutation) {
        for (int i = 0; i < EDGE_PERMUTATIONS.length; i++) {
            if (edgePermutation.equals(EDGE_PERMUTATIONS[i])) { return i; }
        }
        throw new IllegalArgumentException("(edgePermutationToKey) Unexpected edgePermutation: " + edgePermutation);
    }
}