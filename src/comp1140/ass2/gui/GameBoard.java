package comp1140.ass2.gui;

import comp1140.ass2.*;
import comp1140.ass2.components.EdgeType;
import comp1140.ass2.components.Node;
import comp1140.ass2.players.Agent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

import static comp1140.ass2.gui.Board.*;
import static comp1140.ass2.gui.GameEdge.*;
import static comp1140.ass2.gui.GameTile.*;

/**
 * Methods for creating the board and dealing with other visual features.
 *  methods displaying board state authored by Dillon Chen
 *  methods displaying messages and updating score authored by Olin Gao
 */
class GameBoard {

    static final int SQUARE_SIZE = 70;
    static final int NODE_DIFF = 120;
    static final int EDGE_SIZE = 100;
    static final int EDGE_HEIGHT = EDGE_SIZE * 2 / 5;

    static final int[] P1_TILE1_HOMEPOS = {20, WINDOW_HEIGHT-110-SQUARE_SIZE/2};
    static final int[] P1_TILE2_HOMEPOS = {35+SQUARE_SIZE, P1_TILE1_HOMEPOS[1]};
    static final int[] P2_TILE1_HOMEPOS = {WINDOW_WIDTH-P1_TILE1_HOMEPOS[0]-SQUARE_SIZE, 110-SQUARE_SIZE/2};
    static final int[] P2_TILE2_HOMEPOS = {WINDOW_WIDTH-P1_TILE2_HOMEPOS[0]-SQUARE_SIZE, P2_TILE1_HOMEPOS[1]};

    static final String TILE_URI_BASE = "comp1140/ass2/gui/tileAssets/";
    static final String EDGE_URI_BASE = "file:assets/";

    static final String CHRONICLE_FONT = "file:assets/Chronicle.ttf";
    static final String ROLAND_FONT = "file:assets/sorryroland.ttf";
    static final String VAN_FONT = "file:assets/DIOGENES.ttf";
    static final String CONSTANI_FONT = "file:assets/constani.ttf";

    private static Text player1Score;
    private static Text player2Score;
    private static Text message;

    static Button mute = new Button("", new ImageView(new Image("file:assets/notmute.png")));
    private static Random random = new Random();
    private static Timeline timeline;
    private static Glow glow = new Glow(0);


    // creates the board background when starting a new game
    static void createBoard() {
        root.getChildren().add(board);
        board.getChildren().add(edges);

        Text player1Name;
        Text player2Name;

        displayGameState();

        player1Name = new Text(player1.getName());
        if (player1 instanceof Agent)
            player1Name.setFont(Font.loadFont(ROLAND_FONT, 44));
        else
            player1Name.setFont(Font.loadFont(CONSTANI_FONT, 35));
        player1Name.setLayoutX(20);
        player1Name.setLayoutY(WINDOW_HEIGHT-25);

        player1Score = new Text("00");
        player1Score.setFont(Font.loadFont(CHRONICLE_FONT, 55));
        player1Score.setLayoutX(player1Name.getLayoutX()+player1Name.getLayoutBounds().getWidth()+20);
        player1Score.setLayoutY(WINDOW_HEIGHT-19);

        player2Name = new Text(player2.getName());
        if(player2 instanceof Agent)
            player2Name.setFont(Font.loadFont(ROLAND_FONT, 44));
        else
            player2Name.setFont(Font.loadFont(CONSTANI_FONT, 35));
        player2Name.setLayoutX(WINDOW_WIDTH-20-player2Name.getLayoutBounds().getWidth());
        player2Name.setLayoutY(15+player2Name.getLayoutBounds().getHeight());

        player2Score = new Text("00");
        player2Score.setFont(Font.loadFont(CHRONICLE_FONT, 55));
        player2Score.setLayoutX(player2Name.getLayoutX()-player2Score.getLayoutBounds().getWidth()-20);
        player2Score.setLayoutY(player2Score.getLayoutBounds().getHeight());

        message = new Text(MESSAGES[random.nextInt(MESSAGES.length)]);
        message.setFont(Font.loadFont(VAN_FONT, 15));
        message.setX(WINDOW_WIDTH-15-message.getLayoutBounds().getWidth());
        message.setY(WINDOW_HEIGHT-35);
        message.setFill(Color.INDIANRED);

        mute.setLayoutX(10);
        mute.setLayoutY(10);
        mute.setOnAction(e -> toggleMute());
        mute.setFocusTraversable(false);

        Text greece = new Text("Ελλάς");
        Text troy = new Text("Τροία");
        greece.setFont(Font.loadFont(CONSTANI_FONT, 35));
        greece.setLayoutX(player1Score.getLayoutX()+player1Score.getLayoutBounds().getWidth()+20);
        greece.setLayoutY(player1Name.getLayoutY());
        troy.setFont(Font.loadFont(CONSTANI_FONT, 35));
        troy.setLayoutX(player2Score.getLayoutX()-troy.getLayoutBounds().getWidth()-20);
        troy.setLayoutY(player2Name.getLayoutY());

        board.getChildren().addAll(player1Name, player2Name,
                player1Score, player2Score, message, greece, troy, mute);
    }

    // displays the game state on the board
    static void displayGameState() {
        boolean agamemnon = game instanceof Agamemnon;
        displayState(new String[]{"", game.getEdges()}, board, edges, agamemnon, agamemnon?55:35, true);
    }

    static void displayState(String[] state, Group board, Group edges, boolean agamemnon, int yShift, boolean glowing) {
        board.getChildren().remove(edges);
        board.getChildren().add(edges);
        edges.getChildren().clear();

        // placing the edges first so that they appear under nodes
        String edgeState = state[1];
        for (int i = 0; i < edgeState.length(); i+=5) {
            String edgeImage = EdgeType.charToEdgeName(edgeState.charAt(i));
            String edgePos = edgeState.substring(i+1,i+5);

            ImageView edge = new ImageView();
            edge.setImage(new Image(EDGE_URI_BASE+edgeImage+".png"));
            edge.setLayoutX(findCoordinates(edgePos, agamemnon)[0]);
            edge.setLayoutY(findCoordinates(edgePos, agamemnon)[1] + yShift);
            edge.setFitWidth(EDGE_SIZE);
            edge.setPreserveRatio(true);
            edge.setRotate(-findRotation(edgePos, agamemnon));

            edges.getChildren().add(edge);
        }

        // setting empty nodes above edges
        for (int i = 0; i < (agamemnon?32:33); i++) {
            Circle circle = new Circle();
            circle.setFill(Color.SILVER);
            circle.setRadius(SQUARE_SIZE/2);
            circle.setLayoutX(getCoordinates(Node.idTo2String(i), agamemnon)[0] + (SQUARE_SIZE/2));
            circle.setLayoutY(getCoordinates(Node.idTo2String(i), agamemnon)[1] + (SQUARE_SIZE/2 + yShift));
            if (glowing) circle.setEffect(glow);

            board.getChildren().add(circle);
        }

        // placing the tiles after so that they appear above nodes
        String nodeState = state[0];
        for (int i = 0; i < nodeState.length(); i+=4) {
            String tileImage = nodeState.substring(i,i+2);
            String nodeID = nodeState.substring(i+2,i+4);

            ImageView tile = new ImageView();
            tile.setImage(new Image(TILE_URI_BASE+tileImage+".png"));
            tile.setLayoutX(getCoordinates(nodeID, agamemnon)[0]);
            tile.setLayoutY(getCoordinates(nodeID, agamemnon)[1] + yShift);
            tile.setFitHeight(SQUARE_SIZE);
            tile.setFitWidth(SQUARE_SIZE);

            board.getChildren().add(tile);
        }

        for (var circle : tileHome.getChildren()) { circle.setEffect(glow); }
        if (timeline != null) timeline.pause();
        timeline = new Timeline(new KeyFrame(Duration.millis(100),
                    ae -> {
                        if (increase) {
                            glowFactor += 0.05;
                            if (glowFactor >= 0.8) increase = false;
                        } else {
                            glowFactor -= 0.05;
                            if (glowFactor <= 0) increase = true;
                        }
                        glow.setLevel(glowFactor);
                    }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    }
    private static double glowFactor = 0;
    private static boolean increase = true;

    // placing golden circles for tile home
    static {
        Circle p1TileHome1 = new Circle();
        Circle p1TileHome2 = new Circle();
        p1TileHome1.setRadius(SQUARE_SIZE/2);
        p1TileHome1.setFill(Color.DARKGOLDENROD);
        p1TileHome1.setLayoutX(P1_TILE1_HOMEPOS[0]+SQUARE_SIZE/2);
        p1TileHome1.setLayoutY(P1_TILE1_HOMEPOS[1]+SQUARE_SIZE/2);
        p1TileHome2.setRadius(SQUARE_SIZE/2);
        p1TileHome2.setFill(Color.DARKGOLDENROD);
        p1TileHome2.setLayoutX(P1_TILE2_HOMEPOS[0]+SQUARE_SIZE/2);
        p1TileHome2.setLayoutY(P1_TILE2_HOMEPOS[1]+SQUARE_SIZE/2);

        Circle p2TileHome1 = new Circle();
        Circle p2TileHome2 = new Circle();
        p2TileHome1.setRadius(SQUARE_SIZE/2);
        p2TileHome1.setFill(Color.DARKGOLDENROD);
        p2TileHome1.setLayoutX(P2_TILE1_HOMEPOS[0]+SQUARE_SIZE/2);
        p2TileHome1.setLayoutY(P2_TILE1_HOMEPOS[1]+SQUARE_SIZE/2);
        p2TileHome2.setRadius(SQUARE_SIZE/2);
        p2TileHome2.setFill(Color.DARKGOLDENROD);
        p2TileHome2.setLayoutX(P2_TILE2_HOMEPOS[0]+SQUARE_SIZE/2);
        p2TileHome2.setLayoutY(P2_TILE2_HOMEPOS[1]+SQUARE_SIZE/2);

        tileHome.getChildren().addAll(p1TileHome1, p1TileHome2, p2TileHome1, p2TileHome2);
    }

    // update scores
    static void updateScores(){
        int[] scores = game.getScores();
        player1Score.setText(Node.idTo2String(scores[0]));
        player2Score.setText(Node.idTo2String(scores[1]));
    }

    // toggles message displaying feature
    static void toggleMessage(boolean toggle){
        if (toggle) {
            board.getChildren().removeAll(message);
            message.setText(MESSAGES[random.nextInt(MESSAGES.length)]);
            message.setX(WINDOW_WIDTH-15-message.getLayoutBounds().getWidth());
            board.getChildren().addAll(message);
        }
        else
            board.getChildren().removeAll(message);
    }

    static final String[] MESSAGES = 
            {"All fell silent and intently held their gaze"
            ,"Thence father Aeneas rose upon the high couch"
            ,"Oh Queen, you bid me to renew an unspeakable pain"
            ,"Of when the Greeks overthrew Troy's sorrowful empire and its riches"
            ,"Who, even among the Greeks, can hold back tears upon hearing such things?"
            ,"And now a damp night descends from the sky and invites sleep"
            ,"But if you so desire to know of our labours, I shall begin"
            ,"Although my mind shudders to recall such tragedy and recoils in grief, I shall begin"
            ,"Broken by war and driven back by the fates"
            ,"The Greek leaders, with so many years slipping past, built a horse the size of a mountain"
            ,"With the divine craft of Pallas, they interweaved its belly with planks of pine"
            ,"They told us it was an offering for their return - this rumour spreads"
            ,"Drawing lots, they secretly hid men inside the belly, filling it with an armed militia"
            ,"In the distance was Tenedos, an island most renowned for its riches in Priam's time"
            ,"We thought they had left for Mycenas by fair winds"
            ,"Thus all Troy frees itself from an unceasing period of lamentation"
            ,"Some were astonished at the sheer size of maiden Minerva's fatal gift"
            ,"Thyometes first encouraged us to haul it into the gates and place it atop the citadel"
            ,"But the wise Capys ordered us to hurl it into the sea, or burn its wooden belly upon a flame"
            ,"Do you truly believe that any gift is free from Greek trickery? Let alone that of Ulysses?"
                    ,"I fear Greeks, even when they bring gifts"
                    ,"I fear Greeks, even when they bring gifts"
                    ,"I fear Greeks bearing gifts"
                    ,"I fear Greeks bearing gifts"
            ,"After saying this, Laocoon hurled a spear into the horse's wooden belly"
            ,"And if the will of the Gods had not been so malign, he would have torn apart the horse's wooden belly"
            ,"Confused and unarmed, he said \"Alas, what lands, what seas may take me now?\""
            ,"He continued \"I have no place among the Greeks, and even the Trojans shall demand my blood\""
            ,"With this lamentation, our violence was suppressed. But what trust should we have in him as a captive?"
            ,"King, I will tell you everything of the wooden horse, and I do not deny that I am indeed of Greek stock"
            ,"I was strolling off-road when suddenly my father Anchises appeared, gazing out from the shadows"
            ,"Son, leave this city. They are coming."
            ,"I see burning weapons and horrible fires across the city"
            ,"Stop indulging in lament. You were not fated to save me, else Olympus would decree so"
            ,"You will traverse a vast sea in your search for a new abode"
            ,"You will arrive at a Hesperian land, where the Tiber flows between fertile fields"
            ,"There you will be granted eternal prosperity and a regal wife - Mourn no more of your beloved Creusa"
            ,"The morning star rose above Mount Ida as the Greeks continued to siege the great city"
            ,"With no hope left for us, I departed for the mountains, carrying my father Anchises upon my back."
            ,"I sing of arms and the man, who first from the shores of Troy..."
                    ,"Across lands and seas he was battered beneath the violence of Gods"
                    ,"Just as bees in early summer, busy beneath the sunlight through flowered meadows"
                    ,"For full three hundred years, the capital and rule of Hector's race shall be at Alba"
                    ,"Already in Latium there is a new Achilles"
                    ,"Here are four altars raised to Neptune; the God himself gives us the will, the torches"
                    ,"And here Anchises accompanies Sibyl and his son, and sends them through the gate of ivory"
                    ,"If I cannot move heaven, I will raise hell"
                    ,"We Trojans are at an end, Ilium has ended with the vast glory of the Teucrians."
                    ,"I see wars, horrible wars, and the Tiber foaming with much blood"
                    ,"And dying, he remembers his sweet Argos"
                    ,"I feel again the spark of an ancient flame"
                    ,"And Sinon, protected by the Gods' unjust doom, sets free the Greeks from the horse's belly"
                    ,"And Sinon, protected by the Gods' unjust doom, sets free the Greeks from the horse's belly"
                    ,"And Sinon, protected by the Gods' unjust doom, sets free the Greeks from the horse's belly"
                    ,"And Sinon the conquerer exultantly stirs the flames"
                    ,"Through the trickery of the perjured Sinon, the indomitable Trojan people were trapped by false tears"
                    ,"The serpents wreathed Laocoon in massive coils, devouring his limbs"
                    ,"They say Laocoon justly paid for his crime of tarnishing the sacred gift"
                    ,"The engine of fate is dragged within our walls, filled with armed men"
                    ,"The engine of fate is dragged within our walls, filled with armed men"
                    ,"The engine of fate is dragged within our walls, filled with armed men"
                    ,"Four times the structure stumbled and roared at the gates, but we pressed on, blind with frenzy"
                    ,"We unfortunates, for whom that day would be our last, decorated the city's temples with festive branches"
                    ,"And now the Greek phalanx sailed from Tenedos in the benign stillness of the silent moon"
    };
}
