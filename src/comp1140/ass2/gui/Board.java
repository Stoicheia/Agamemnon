package comp1140.ass2.gui;

import comp1140.ass2.*;
import comp1140.ass2.players.Agent;
import comp1140.ass2.players.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

import static comp1140.ass2.gui.GameTile.*;
import static comp1140.ass2.players.Faction.*;
import static comp1140.ass2.gui.GameBoard.*;
import static comp1140.ass2.gui.GameMenu.*;

/** TASK 9/13
 * This class plays the Agamemnon/Loom game
 * Authored by Dillon Chen and Olin Gao
 */
public class Board extends Application {

    static final int WINDOW_WIDTH = 1024;
    static final int WINDOW_HEIGHT = 768;

    static final Group root = new Group();

    static final Group home = new Group();
    static final Group settings = new Group();
    static final Group gameOver = new Group();
    static final Group pause = new Group();

    static final Group board = new Group();
    static final Group edges = new Group();
    static final Group tiles = new Group();
    static final Group tileHome = new Group();

    static final Group warp = new Group();
    static final Group warpTemp = new Group();

    static final Group clouds = new Group();
    static final Group grass = new Group();

    private static MediaPlayer musicPlayer;
    private static MediaPlayer sound;

    static final Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

    static Player player1 = new Player("Player 1", Greek);
    static Player player2 = new Player("Player 2", Trojan);

    static Game game;
    static boolean agamemnon;
    static int gameSeed;
    static int turn;
    static int playedTiles;
    static boolean paused;
    static boolean muted;
    static boolean warping;

    /**
     * Primary method which gets activated for each new game that starts.
     * @param agamemnon, true for agamemnon variant, false for loom variant
     */
    static void playGame(boolean agamemnon) {
        root.getChildren().clear();

        warp.getChildren().clear();
        board.getChildren().clear();
        tiles.getChildren().clear();

        Board.agamemnon = agamemnon;
        turn = 1;
        playedTiles = 0;
        paused = false;
        muted = false;
        warping = false;

        musicPlayer.stop();
        if (agamemnon) {
            playMusic("assets/mus2.wav");
            game = new Agamemnon(player1, player2);
        } else {
            playMusic("assets/mus3.wav");
            if (gameSeed == -1)
                game = new Loom(player1, player2);
            else
                game = new Loom(player1, player2, gameSeed);
        }

        root.getChildren().add(warp);
        root.getChildren().add(warpTemp);
        root.getChildren().add(tileHome);
        GameBoard.createBoard();
        root.getChildren().add(tiles);

        nextTurn();

        scene.setOnKeyPressed(event -> {
            KeyCode e = event.getCode();
            switch (e) {
                case SPACE:
                    playSound("assets/son.wav");
                    break;
                case M:
                    toggleMute();
                    break;
                case ESCAPE:
                    if (!paused && turn <= 16) {
                        paused = true;
                        pauseMenu();
                        if(!muted)
                            musicPlayer.setVolume(0.2);
                        toggleMessage(false);
                    } else if (paused) {
                        paused = false;
                        root.getChildren().remove(pause);
                        if(!muted)
                            musicPlayer.setVolume(1);
                        toggleMessage(true);
                    }
            }
        });
    }

    /**
     * Method which deals with everything that happens once a player completes their turn.
     * The game itself progresses when a tile is placed (see snapToNode in GameTile class),
     * which calls the nextTurn function again, so this is a bit recursive.
     */
    static void nextTurn() {
        if (turn > 16){
            openGameOver();
        } else if (turn%2==1){
            nextTiles(player1, 2-1/turn);
        } else if (turn%2==0){
            nextTiles(player2, 2-turn/16);
        }
        if (turn <= 16)
            GameBoard.toggleMessage(true);
    }

    private static void giveTiles(Player p, int n){
        if(p.equals(player1)){
            new GameTile(p.generateTile(), P1_TILE1_HOMEPOS[0], P1_TILE1_HOMEPOS[1]);
            if(n>1)
                new GameTile(p.generateTile(), P1_TILE2_HOMEPOS[0], P1_TILE2_HOMEPOS[1]);
        }
        else {
            new GameTile(p.generateTile(), P2_TILE1_HOMEPOS[0], P2_TILE1_HOMEPOS[1]);
            if (n>1)
                new GameTile(p.generateTile(), P2_TILE2_HOMEPOS[0], P2_TILE2_HOMEPOS[1]);
        }
    }

    private static void compMoveH(Player p, int n){
        if(n>1)
            autoPlay(Agamemnon.generateAction(game.getMacroState(), p.generateTile()+p.generateTile()));
        else
            autoPlay(Agamemnon.generateAction(game.getMacroState(), p.generateTile()));
    }

    private static void compMove(Player p, int n){
        Random pos = new Random();
        Group thinking = new Group();
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                ImageView thinker = new ImageView("file:assets/thinking.png");
                thinker.setX(pos.nextInt(WINDOW_WIDTH-100)-200);
                thinker.setY(pos.nextInt(WINDOW_HEIGHT-100)-200);
                thinker.setScaleX(0.3);
                thinker.setScaleY(0.3);

                thinking.getChildren().add(thinker);
                root.getChildren().add(thinking);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            root.getChildren().remove(thinking);
            compMoveH(p, n);
        });
        task.setOnFailed(event -> {
            System.out.println("Something bad happened!");
            root.getChildren().remove(thinking);
            compMoveH(p, n);
        });

        new Thread(task).run();
    }

    private static void nextTiles(Player p, int n){
        if (p instanceof Agent) {
            compMove(p,n);
        } else {
            giveTiles(p, n);
        }
    }

    // music player
    static void playMusic(String fileName) {
        if (musicPlayer != null)
            musicPlayer.stop();
        File file = new File(fileName);
        Media media = new Media(file.toURI().toString());
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                musicPlayer.seek(Duration.ZERO);
            }
        });
        musicPlayer.play();
    }

    // sound effects player
    static void playSound(String fileName){
        File file=new File(fileName);
        Media media=new Media(file.toURI().toString());
        sound = new MediaPlayer(media);
        sound.play();
    }

    // toggling mute button
    static void toggleMute(){
        if(muted&&!paused) {
            musicPlayer.setVolume(1);
            muted = false;
        }
        else if(muted) {
            musicPlayer.setVolume(0.2);
            muted = false;
        }
        else {
            musicPlayer.setVolume(0);
            muted = true;
        }
        mute.setGraphic(new ImageView(new Image(muted?"file:assets/mute.png":"file:assets/notmute.png")));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Agamemnon");

        openHome();
        scene.setFill(Color.BLANCHEDALMOND);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


