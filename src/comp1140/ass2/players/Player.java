package comp1140.ass2.players;

import java.util.Random;

/**
 *  Object representing a player
 *  authored by Dillon Chen and Olin Gao
 */
public class Player {

    private String name;            // players can input name before playing game
    private Faction faction;        // see Faction class - Greek for player 1, Trojan for 2
    private int score;              // score calculated by total number of edges held
    private int[] heldTiles;        // number of tiles a-j respectively
    private int tilesRemaining;     // number of tile remaining

    private Random rand;

    public Player(String name, Faction faction) {
        this.name = name;
        this.faction = faction;
        this.score = 0;
        this.heldTiles = new int[]{1, 1, 1, 1, 1, 3, 2, 1, 2, 2};
        this.tilesRemaining = 15;
        rand = new Random();
    }

    public String generateTile() {
        char tileType = 'a';
        int roll = rand.nextInt(tilesRemaining)+1;
        for (int i = 0; i < heldTiles.length; i++) {
            int tile = heldTiles[i];
            while (tile > 0) {
                tile--;
                roll--;
                if (roll == 0)
                    tileType = (char) ('a' + i);
            }
        }
        this.removeTile(tileType);
        tilesRemaining--;
        return Character.toString(faction.toChar()) + tileType;
    }

    public void removeTile(char tile){ heldTiles[tile - 'a']--; }

    public void changeName(String newName) { this.name = newName; }



    public int getScore() { return score; }
    public void setScore(int s) {score = s;}
    public Faction getFaction() { return faction; }
    public String getName() { return name; }

    @Override
    public String toString() { return faction +": "+name; }
}
