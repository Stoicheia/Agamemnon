package comp1140.ass2.gui;

import comp1140.ass2.Agamemnon;
import comp1140.ass2.Loom;
import comp1140.ass2.components.Node;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.HashSet;

import static comp1140.ass2.gui.Board.*;
import static comp1140.ass2.gui.Board.agamemnon;
import static comp1140.ass2.gui.GameBoard.*;
import static comp1140.ass2.gui.GameEdge.*;

/**
 *  This class deals with the draggable tiles on the game board and the warp edge swap feature.
 *  Authored by Dillon Chen and Alexander Cox
 */
class GameTile extends ImageView {

    private String tileEncoding;        // length 2 encoding of tile
    private char faction;               // faction of tile
    private char tileType;              // type of tile
    private String action;              // string encoding of tile placement
    private double homeX, homeY;        // starting or resting position of tile
    private double mouseX, mouseY;      // last known mouse positions (used when dragging)
    private boolean set;                // checks if the tile has been placed on the board

    // construct a particular playing tile with the usual length 2 string input.
    GameTile(String tileEncoding, int homeX, int homeY) {
        this.tileEncoding = tileEncoding;
        this.faction = tileEncoding.charAt(0);
        this.tileType = tileEncoding.charAt(1);
        this.homeX = homeX;
        this.homeY = homeY;

        setImage(new Image(TILE_URI_BASE + faction + tileType + ".png"));
        setFitHeight(SQUARE_SIZE);
        setFitWidth(SQUARE_SIZE);
        this.setLayoutX(homeX);
        this.setLayoutY(homeY);

        tiles.getChildren().add(this);

            /* event handlers for human player*/
            setOnMousePressed(event -> {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            });
            setOnMouseDragged(event -> {
                if (!set && !paused && !warping) {
                    toFront();
                    double movementX = event.getSceneX() - mouseX;
                    double movementY = event.getSceneY() - mouseY;
                    setLayoutX(getLayoutX() + movementX);
                    setLayoutY(getLayoutY() + movementY);
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                    event.consume();
                }
            });
            setOnMouseReleased(event -> snapToNode());

    }

    GameTile(String tileEncoding, String node) {
        this.tileEncoding = tileEncoding;
        this.faction = tileEncoding.charAt(0);
        this.tileType = tileEncoding.charAt(1);
        this.homeX = getCoordinates(node, agamemnon)[0];
        this.homeY = getCoordinates(node, agamemnon)[1];
        this.set = true;

        setImage(new Image(TILE_URI_BASE + faction + tileType + ".png"));
        setFitHeight(SQUARE_SIZE);
        setFitWidth(SQUARE_SIZE);
        this.setLayoutX(homeX);
        this.setLayoutY(homeY+(agamemnon?55:35));

        if (agamemnon)
            ((Agamemnon) game).occupiedAgamemnonNodes[Integer.parseInt(node)] = true;
        else
            ((Loom) game).occupiedLoomNodes[Integer.parseInt(node)] = true;

        tiles.getChildren().add(this);
    }

    // snaps tile to nearest node and progresses the game
    private void snapToNode() {
        if (set) { return; }
        for (int i = 0; i <= (agamemnon?31:32); i++) {
            if ((agamemnon?((Agamemnon) game).occupiedAgamemnonNodes[i]:((Loom) game).occupiedLoomNodes[i]))
                continue;
            String nodeID = Node.idTo2String(i);
            double nodeX = getCoordinates(nodeID, agamemnon)[0]+SQUARE_SIZE/2;
            double nodeY = getCoordinates(nodeID, agamemnon)[1]+(agamemnon?55:35)+SQUARE_SIZE/2;
            if (Math.abs(mouseX-nodeX)<2*SQUARE_SIZE/3 && Math.abs(mouseY-nodeY)<2*SQUARE_SIZE/3) {
                playTilePlacedSound();
                homeX = nodeX-SQUARE_SIZE/2;
                homeY = nodeY-SQUARE_SIZE/2;
                set = true;
                action = tileEncoding+nodeID;
                if (tileType == 'j') { playWarp(nodeID, agamemnon); }
                else {
                    game.placeTile(action);
                    playedTiles++;
                    GameBoard.updateScores();
                    if (agamemnon)
                        ((Agamemnon) game).occupiedAgamemnonNodes[i] = true;
                    else
                        ((Loom) game).occupiedLoomNodes[i] = true;
                    if (playedTiles % 2 == 1 || turn == 16) { turn++; nextTurn(); }
                }
            }
        }
        setLayoutX(homeX);
        setLayoutY(homeY);
    }

    // ai
    public static void autoPlay(String action) {
        String[] actions = Agamemnon.actionToSubActions(action);
        String action1 = actions[0];
        String action2 = actions[1];
        autoPlaySingle(action1);
        if (action2.length()!=0)
            autoPlaySingle(action2);
        displayGameState();
        GameBoard.updateScores();
        turn++;
        nextTurn();
    }

    private static void autoPlaySingle(String action) {
        game.placeTile(action);
        playedTiles++;
        new GameTile(action.substring(0,2), action.substring(2,4));
    }

    /* The following methods deal with UI regarding Warp tiles */

    private int edgesSelected;
    private HashSet<String> adjacentEdges;

    private void playWarp(String nodeID, boolean agamemnon) {
        warp.getChildren().clear();

        warping = true;

        adjacentEdges = new HashSet<>();
        edgesSelected = 0;
        int[] nodes = agamemnon ? AGAMEMNON_NODE_CONNECTIONS[Integer.valueOf(nodeID)] : LOOM_NODE_CONNECTIONS[Integer.valueOf(nodeID)];

        for (var node : nodes) {
            String edge = Node.reorderString(nodeID + Node.idTo2String((int) node));
            adjacentEdges.add(edge+Node.idTo2String((int) node));

            double x = findCoordinates(edge, agamemnon)[0];
            double y = findCoordinates(edge, agamemnon)[1]+(agamemnon?55:35);
            Rectangle rectangle = new Rectangle(x-6, y-6, EDGE_SIZE+12, EDGE_HEIGHT+12);
            rectangle.setFill(Color.MEDIUMPURPLE);
            rectangle.setOpacity(0.75);
            rectangle.setRotate(-findRotation(edge, agamemnon));

            warp.getChildren().add(rectangle);
        }

        // for temporarily highlighting which edge is clickable depending on mouse position
        scene.setOnMouseMoved(event -> {
            warpTemp.getChildren().clear();
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            for (var edge : (HashSet<String>) adjacentEdges.clone()) {
                String edgeEncoding = edge.substring(0,4);
                double x = Math.abs(mouseX-(findCoordinates(edge, agamemnon)[0]+EDGE_SIZE/2));
                double y = Math.abs(mouseY-(findCoordinates(edge, agamemnon)[1]+(agamemnon?55:35)+EDGE_HEIGHT/2));
                int rotate = findRotation(edgeEncoding, agamemnon);
                switch (Math.abs(rotate)) {
                    case 0:
                        if (x<EDGE_SIZE/2 && y<EDGE_HEIGHT/2) {
                            warpEdgeHighlight(edgeEncoding, rotate, agamemnon);
                            return;
                        }
                        break;
                    case 30:
                        if (x<EDGE_SIZE/2 && y<EDGE_SIZE/3) {
                            warpEdgeHighlight(edgeEncoding, rotate, agamemnon);
                            return;
                        }
                        break;
                    case 60:
                        if (x<EDGE_SIZE/3 && y<EDGE_SIZE/2) {
                            warpEdgeHighlight(edgeEncoding, rotate, agamemnon);
                            return;
                        }
                        break;
                    case 90:
                        if (x<EDGE_HEIGHT/2 && y<EDGE_SIZE/2) {
                            warpEdgeHighlight(edgeEncoding, rotate, agamemnon);
                            return;
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("invalid rotation: "+rotate);
                }
            }
        });

        // for selecting which edges get swapped using mouse presses
        scene.setOnMousePressed(event -> {
            double xClick = event.getSceneX();
            double yClick = event.getSceneY();
            for (var edge : (HashSet<String>) adjacentEdges.clone()) {
                String edgeEncoding = edge.substring(0,4);
                String adjacentNode = edge.substring(4,6);
                double x = Math.abs(xClick-(findCoordinates(edge, agamemnon)[0]+EDGE_SIZE/2));
                double y = Math.abs(yClick-(findCoordinates(edge, agamemnon)[1]+(agamemnon?55:35)+EDGE_HEIGHT/2));
                int rotate = findRotation(edgeEncoding, agamemnon);
                switch (Math.abs(rotate)) {
                    case 0:
                        if (x<EDGE_SIZE/2 && y<EDGE_HEIGHT/2)
                            warpEdgeSelect(edgeEncoding, adjacentNode, rotate, agamemnon);
                        break;
                    case 30:
                        if (x<EDGE_SIZE/2 && y<EDGE_SIZE/3)
                            warpEdgeSelect(edgeEncoding, adjacentNode, rotate, agamemnon);
                        break;
                    case 60:
                        if (x<EDGE_SIZE/3 && y<EDGE_SIZE/2)
                            warpEdgeSelect(edgeEncoding, adjacentNode, rotate, agamemnon);
                        break;
                    case 90:
                        if (x<EDGE_HEIGHT/2 && y<EDGE_SIZE/2)
                            warpEdgeSelect(edgeEncoding, adjacentNode, rotate, agamemnon);
                        break;
                    default:
                        throw new IllegalArgumentException("invalid rotation: "+rotate);
                }
            }
        });
    }

    private void warpEdgeHighlight(String edgeEncoding, int rotate, boolean agamemnon) {
        double X = findCoordinates(edgeEncoding, agamemnon)[0];
        double Y = findCoordinates(edgeEncoding, agamemnon)[1]+(agamemnon?55:35);
        Rectangle rectangle = new Rectangle(X-6, Y-6, EDGE_SIZE+12, EDGE_HEIGHT+12);
        rectangle.setFill(Color.CYAN);
        rectangle.setOpacity(0.75);
        rectangle.setRotate(-rotate);
        warpTemp.getChildren().add(rectangle);
    }

    private void warpEdgeSelect(String edgeEncoding, String adjacentNode, int rotate, boolean agamemnon) {
        edgesSelected++;
        action += adjacentNode;
        double X = findCoordinates(edgeEncoding, agamemnon)[0];
        double Y = findCoordinates(edgeEncoding, agamemnon)[1]+(agamemnon?55:35);
        Rectangle rectangle = new Rectangle(X-6, Y-6, EDGE_SIZE+12, EDGE_HEIGHT+12);
        rectangle.setFill(Color.CYAN);
        rectangle.setOpacity(0.75);
        rectangle.setRotate(-rotate);
        warp.getChildren().add(rectangle);
        adjacentEdges.remove(edgeEncoding+adjacentNode);
        if (edgesSelected==2) {
            playTilePlacedSound();
            warping = false;
            playedTiles++;
            scene.setOnMouseMoved(e -> {});
            scene.setOnMousePressed(e -> {});
            warp.getChildren().clear();
            warpTemp.getChildren().clear();
            game.placeTile(action);
            GameBoard.updateScores();
            displayGameState();
            if (agamemnon)
                ((Agamemnon) game).occupiedAgamemnonNodes[Integer.valueOf(action.substring(2,4))] = true;
            else
                ((Loom) game).occupiedLoomNodes[Integer.valueOf(action.substring(2,4))] = true;
            if (playedTiles % 2 == 1 || turn == 16) { turn++; nextTurn(); }
        }
    }

    // plays sound effects when tile is placed
    private void playTilePlacedSound() {
        switch (tileType) {
            case 'a':
                playSound("assets/sounds/biglead.wav");
                break;
            case 'f': case 'g': case 'h':
                playSound("assets/sounds/smallstr.wav");
                break;
            case 'i':
                playSound("assets/sounds/weft.wav");
                break;
            case 'j':
                playSound("assets/sounds/warp.wav");
                break;
            default:
                playSound("assets/sounds/smallead.wav");
        }
    }

    /* Node locations */

    // shift the coordinates of nodes given an input bearing
    private static double[] shiftCoordinates(double angle, double[] pos) {
        double x = pos[0] + (Math.cos(Math.toRadians(angle)) * NODE_DIFF);
        double y = pos[1] + (Math.sin(-Math.toRadians(angle)) * NODE_DIFF);
        return new double[]{x, y};
    }

    // returns the coordinate of required edge for required game
    static double[] getCoordinates(String nodeID, boolean agamemnon) {
        return agamemnon?agamemnonCoordinates.get(nodeID):loomCoordinates.get(nodeID);
    }

    // holds the coordinates of Agamemnon nodes on the screen
    static final HashMap<String, double[]> agamemnonCoordinates = new HashMap<>();
    static {
        agamemnonCoordinates.put("00", new double[]{85,200});
        agamemnonCoordinates.put("01", shiftCoordinates(60, agamemnonCoordinates.get("00")));
        agamemnonCoordinates.put("02", shiftCoordinates(-60, agamemnonCoordinates.get("00")));
        agamemnonCoordinates.put("03", shiftCoordinates(-90, agamemnonCoordinates.get("02")));
        agamemnonCoordinates.put("04", shiftCoordinates(0, agamemnonCoordinates.get("00")));
        agamemnonCoordinates.put("05", shiftCoordinates(30, agamemnonCoordinates.get("01")));
        agamemnonCoordinates.put("06", shiftCoordinates(-30, agamemnonCoordinates.get("02")));
        agamemnonCoordinates.put("07", shiftCoordinates(-30, agamemnonCoordinates.get("03")));
        agamemnonCoordinates.put("08", shiftCoordinates(30, agamemnonCoordinates.get("04")));
        agamemnonCoordinates.put("09", shiftCoordinates(-30, agamemnonCoordinates.get("04")));
        agamemnonCoordinates.put("10", shiftCoordinates(0, agamemnonCoordinates.get("05")));
        agamemnonCoordinates.put("11", shiftCoordinates(-60, agamemnonCoordinates.get("09")));
        agamemnonCoordinates.put("12", shiftCoordinates(-90, agamemnonCoordinates.get("11")));
        agamemnonCoordinates.put("13", shiftCoordinates(0, agamemnonCoordinates.get("08")));
        agamemnonCoordinates.put("14", shiftCoordinates(-90, agamemnonCoordinates.get("13")));
        agamemnonCoordinates.put("15", shiftCoordinates(60, agamemnonCoordinates.get("13")));
        agamemnonCoordinates.put("16", shiftCoordinates(30, agamemnonCoordinates.get("12")));
        agamemnonCoordinates.put("17", shiftCoordinates(-30, agamemnonCoordinates.get("12")));
        agamemnonCoordinates.put("18", shiftCoordinates(30, agamemnonCoordinates.get("14")));
        agamemnonCoordinates.put("19", shiftCoordinates(-30, agamemnonCoordinates.get("14")));
        agamemnonCoordinates.put("20", shiftCoordinates(-30, agamemnonCoordinates.get("15")));
        agamemnonCoordinates.put("21", shiftCoordinates(-60, agamemnonCoordinates.get("19")));
        agamemnonCoordinates.put("22", shiftCoordinates(-90, agamemnonCoordinates.get("21")));
        agamemnonCoordinates.put("23", shiftCoordinates(0, agamemnonCoordinates.get("18")));
        agamemnonCoordinates.put("24", shiftCoordinates(-90, agamemnonCoordinates.get("23")));
        agamemnonCoordinates.put("25", shiftCoordinates(60, agamemnonCoordinates.get("23")));
        agamemnonCoordinates.put("26", shiftCoordinates(30, agamemnonCoordinates.get("22")));
        agamemnonCoordinates.put("27", shiftCoordinates(30, agamemnonCoordinates.get("24")));
        agamemnonCoordinates.put("28", shiftCoordinates(-30, agamemnonCoordinates.get("24")));
        agamemnonCoordinates.put("29", shiftCoordinates(60, agamemnonCoordinates.get("27")));
        agamemnonCoordinates.put("30", shiftCoordinates(0, agamemnonCoordinates.get("27")));
        agamemnonCoordinates.put("31", shiftCoordinates(0, agamemnonCoordinates.get("28")));
    }

    // holds the coordinates of Loom nodes on the screen
    static final HashMap<String, double[]> loomCoordinates = new HashMap<>();
    static {
        loomCoordinates.put("00", new double[]{85,200});
        loomCoordinates.put("01", shiftCoordinates(60, loomCoordinates.get("00")));
        loomCoordinates.put("02", shiftCoordinates(-60, loomCoordinates.get("00")));
        loomCoordinates.put("03", shiftCoordinates(-90, loomCoordinates.get("02")));
        loomCoordinates.put("04", shiftCoordinates(0, loomCoordinates.get("00")));
        loomCoordinates.put("05", shiftCoordinates(30, loomCoordinates.get("01")));
        loomCoordinates.put("06", shiftCoordinates(-30, loomCoordinates.get("02")));
        loomCoordinates.put("07", shiftCoordinates(-30, loomCoordinates.get("03")));
        loomCoordinates.put("08", shiftCoordinates(30, loomCoordinates.get("04")));
        loomCoordinates.put("09", shiftCoordinates(-30, loomCoordinates.get("04")));
        loomCoordinates.put("10", shiftCoordinates(0, loomCoordinates.get("05")));
        loomCoordinates.put("11", shiftCoordinates(-60, loomCoordinates.get("09")));
        loomCoordinates.put("12", shiftCoordinates(-90, loomCoordinates.get("11")));
        loomCoordinates.put("13", shiftCoordinates(0, loomCoordinates.get("08")));
        loomCoordinates.put("14", shiftCoordinates(-90, loomCoordinates.get("13")));
        loomCoordinates.put("15", shiftCoordinates(60, loomCoordinates.get("13")));
        loomCoordinates.put("16", shiftCoordinates(30, loomCoordinates.get("12")));
        loomCoordinates.put("17", shiftCoordinates(-30, loomCoordinates.get("12")));
        loomCoordinates.put("18", shiftCoordinates(30, loomCoordinates.get("14")));
        loomCoordinates.put("19", shiftCoordinates(-30, loomCoordinates.get("14")));
        loomCoordinates.put("20", shiftCoordinates(-30, loomCoordinates.get("15")));
        loomCoordinates.put("21", shiftCoordinates(-60, loomCoordinates.get("19")));
        loomCoordinates.put("22", shiftCoordinates(-90, loomCoordinates.get("21")));
        loomCoordinates.put("23", shiftCoordinates(0, loomCoordinates.get("18")));
        loomCoordinates.put("24", shiftCoordinates(-90, loomCoordinates.get("23")));
        loomCoordinates.put("25", shiftCoordinates(60, loomCoordinates.get("23")));
        loomCoordinates.put("26", shiftCoordinates(0, loomCoordinates.get("21")));
        loomCoordinates.put("27", shiftCoordinates(0, loomCoordinates.get("22")));
        loomCoordinates.put("28", shiftCoordinates(-30, loomCoordinates.get("23")));
        loomCoordinates.put("29", shiftCoordinates(-30, loomCoordinates.get("25")));
        loomCoordinates.put("30", shiftCoordinates(-60, loomCoordinates.get("28")));
        loomCoordinates.put("31", shiftCoordinates(-90, loomCoordinates.get("30")));
        loomCoordinates.put("32", shiftCoordinates(0, loomCoordinates.get("28")));
    }
}