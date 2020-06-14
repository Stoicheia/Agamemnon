package comp1140.ass2.components;

import comp1140.ass2.players.Faction;

// authored by Dillon Chen
public class Tile { // playing tiles

    private TileType tileType;        // one of four tile types
    private char encoding;            // char encoding provided by spec
    private char rank;                // rank of Leaders
    private int strength;             // strength of fighter tiles

    private Faction faction;          // also known as player turn (see Faction class)
    private String swapEdge1;         // location of an edge being swapped
    private String swapEdge2;         // location of other edge being swapped
    private String originalSubAction;

    public Tile(String subAction) {
        this.originalSubAction = subAction;
        this.faction = Faction.charToFaction(subAction.charAt(0));
        this.encoding = subAction.charAt(1);
        this.tileType = TileType.charToTileType(subAction.charAt(1));
        this.rank = charToRank(subAction.charAt(1));
        this.strength = charToStrength(subAction.charAt(1));

        // stores information for Warp sub-actions
        if (subAction.length() == 8) {
            swapEdge1 = Node.reorderString(subAction.substring(2,4)+subAction.substring(4,6));
            swapEdge2 = Node.reorderString(subAction.substring(2,4)+subAction.substring(6,8));
        }
    }

    public static char charToRank(char letter) {
        return ('a' <= letter && letter <= 'e') ? Character.toUpperCase(letter) : 'f';
    }

    public static int charToStrength(char letter) { return STRENGTH_MAP[(int) letter - 97]; }

    private static int[] STRENGTH_MAP = {1, 3, 4, 3, 2, 1, 2, 3, 0, 0};

    public TileType getTileType() { return tileType; }

    public Faction getFaction() { return faction; }
    public char getRank() { return rank; }
    public char getEncoding() { return encoding; }
    public int getStrength() { return strength; }

    public String getOriginalSubAction() {
        return originalSubAction;
    }

    public String getSwapEdge1(){ return swapEdge1; }
    public String getSwapEdge2(){ return swapEdge2; }
}
