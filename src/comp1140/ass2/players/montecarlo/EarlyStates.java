package comp1140.ass2.players.montecarlo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static comp1140.ass2.Agamemnon.*;

public class EarlyStates {
    public static void main(String[] args) throws IOException {
        String time = DateTimeFormatter.ofPattern("dd_MM_yyyy_HHmmss").format(LocalDateTime.now());
        PrintWriter results = new PrintWriter(new BufferedWriter(new FileWriter("learning_results/all_first_two_turn_states_"+time+".txt")));

        long t= System.currentTimeMillis();
        long minute = 60 * 1000;
        long minutes = 3;
        long cycle = t + (minutes*minute);

        HashSet<String> tilesOut = new HashSet<>();
        while (System.currentTimeMillis() < cycle) {
            String tileState = "";
            String randFirstTile = selectTiles(tileState);
            while (randFirstTile.contains("i") || randFirstTile.contains("j")) {
                randFirstTile = selectTiles(tileState);
            }
            String action1 = generateRandomAction(new String[]{tileState, AGAMEMNON_CONNECTIVITY}, randFirstTile);
            tileState = applyAction(new String[]{tileState, AGAMEMNON_CONNECTIVITY}, action1)[0];
            String randSecondTiles = selectTiles(tileState);
            while (randSecondTiles.contains("i") || randSecondTiles.contains("j")) {
                randSecondTiles = selectTiles(tileState);
            }
            String action2 = generateRandomAction(new String[]{tileState, AGAMEMNON_CONNECTIVITY}, randSecondTiles);
            tileState = applyAction(new String[]{tileState, AGAMEMNON_CONNECTIVITY}, action2)[0];
            tilesOut.add(tileState);
        }

        System.out.println(tilesOut.size());
        for (String move : tilesOut) {
            results.println(move);
        }

        results.close();
    }

}
