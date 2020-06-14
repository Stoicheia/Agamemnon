package comp1140.ass2;

import comp1140.ass2.components.Node;
import comp1140.ass2.players.Player;

import java.util.List;
import java.util.Random;

// authored by Olin Gao
public abstract class Game {

    protected String tiles;
    protected String edges;
    protected Player player1;
    protected Player player2;

    protected static List<Node> nodes;

    public Game(Player player1, Player player2, String edges) {
        this.player1 = player1;
        this.player2 = player2;
        this.tiles = "";
        this.edges = edges;
    }

    public String getState() { return tiles; }
    public String getEdges() { return edges; }
    public String[] getMacroState() {return new String[] {tiles, edges};}

    private void updateScores(){
        int[] scores = Agamemnon.getTotalScore(getMacroState());
        player1.setScore(scores[0]);
        player2.setScore(scores[1]);
    }
    public int[] getScores(){return new int[]{player1.getScore(), player2.getScore()};}

    public void placeTile(String placement){
        tiles = Agamemnon.applySubAction(new String[]{tiles,edges}, placement)[0];
        edges = Agamemnon.applySubAction(new String[]{tiles,edges}, placement)[1];
        updateScores();
    }

    public Player getPlayer(char player) {
        if (player=='O')
            return player1;
        else
            return player2;
    }
}
