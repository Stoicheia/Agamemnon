package comp1140.ass2.gui;

import comp1140.ass2.Loom;
import comp1140.ass2.components.Node;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static comp1140.ass2.Agamemnon.*;
import static comp1140.ass2.gui.GameBoard.*;
import static comp1140.ass2.gui.GameTile.*;

/** TASK 4
 * A very simple viewer for board states in the Agamemnon game.
 * <p>
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various board states.
 *
 * Authored by Dillon Chen and Alexander Cox
 */
public class Viewer extends Application {

    /* board layout */
    private static final int VIEWER_WIDTH = 1024;
    private static final int VIEWER_HEIGHT = 720;

    private final Group root = new Group();
    private final Group controls = new Group();
    private final Group board = new Group();
    private final Group edges = new Group();
    private TextField tilesTextField;
    private TextField edgesTextField;


    // Label node locations to view better
    private void labelNodes(boolean agamemnon) {
        for (int i = 0; i < (agamemnon?32:33); i++) {
            String nodeID = Node.idTo2String(i);

            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.SILVER);
            rectangle.setHeight(SQUARE_SIZE / 4);
            rectangle.setWidth(SQUARE_SIZE / 3);
            rectangle.setX(getCoordinates(nodeID, agamemnon)[0] + (SQUARE_SIZE / 3));
            rectangle.setY(getCoordinates(nodeID, agamemnon)[1] + SQUARE_SIZE);

            Text nodeIDLabel = new Text(nodeID);
            nodeIDLabel.setFill(Color.DARKCYAN);
            nodeIDLabel.setFont(Font.font("Tahoma", FontWeight.BLACK, 15));
            nodeIDLabel.setX(getCoordinates(nodeID, agamemnon)[0] + (SQUARE_SIZE / 3));
            nodeIDLabel.setY(getCoordinates(nodeID, agamemnon)[1] + SQUARE_SIZE * (1.2));

            board.getChildren().add(rectangle);
            board.getChildren().add(nodeIDLabel);
        }
    }

    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label pieceLabel = new Label("Tiles:");
        tilesTextField = new TextField();
        tilesTextField.setPrefWidth(850);
        Label edgesLabel = new Label("Edges:");
        edgesTextField = new TextField();
        edgesTextField.setPrefWidth(930);
        Button button = new Button("Refresh");
        button.setPrefWidth(61);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                board.getChildren().clear();
                if (edgesTextField.getText().length() == Loom.LOOM_CONNECTIVITY.length()) {
                    displayState(new String[]{tilesTextField.getText(), edgesTextField.getText()}, board, edges, false,0, false);
                    labelNodes(false);
                } else {
                    displayState(new String[]{tilesTextField.getText(), edgesTextField.getText()}, board, edges, true, 0, false);
                    labelNodes(true);
                }
            }
        });

        HBox tileshb = new HBox();
        tileshb.getChildren().addAll(pieceLabel, tilesTextField, button);
        tileshb.setSpacing(18);
        tileshb.setLayoutX(20);
        tileshb.setLayoutY(VIEWER_HEIGHT - 40);
        HBox edgeshb = new HBox();
        edgeshb.getChildren().addAll(edgesLabel, edgesTextField);
        edgeshb.setSpacing(10);
        edgeshb.setLayoutX(20);
        edgeshb.setLayoutY(VIEWER_HEIGHT - 80);
        controls.getChildren().addAll(tileshb, edgeshb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Agamemnon Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(controls);
        root.getChildren().add(board);

        makeControls();
        displayState(new String[]{"", AGAMEMNON_CONNECTIVITY}, board, edges, true,0, false);
        labelNodes(true);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
