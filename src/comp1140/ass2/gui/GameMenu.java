package comp1140.ass2.gui;

import comp1140.ass2.*;
import comp1140.ass2.players.Agent;
import comp1140.ass2.players.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

import static comp1140.ass2.players.Faction.*;
import static comp1140.ass2.gui.Board.*;
import static comp1140.ass2.gui.GameBoard.*;

/**
 * Methods for creating and moving between game menus.
 * Authored by Dillon Chen
 */
class GameMenu {

    // opens menu for home screen (when game starts, or buttons redirect to it)
    static void openHome() {
        root.getChildren().clear();
        root.getChildren().add(clouds);
        root.getChildren().add(grass);
        scene.setOnKeyPressed(event -> {});
        paused = false;
        Board.playMusic("assets/mus.wav");

        int buttonWidth = 300;
        int buttonHeight = 30;

        Text agamemnon = new Text("Agamemnon");
        agamemnon.setFont(Font.loadFont(CHRONICLE_FONT,75));
        agamemnon.setLayoutX(WINDOW_WIDTH/2-agamemnon.getLayoutBounds().getWidth()/2);
        agamemnon.setLayoutY(300);

        Button start = new Button("Start");
        start.setOnAction(e -> {settings.getChildren().clear(); openSettings();});
        start.setPrefSize(buttonWidth,buttonHeight*2);
        start.setLayoutX(WINDOW_WIDTH/2-buttonWidth/2);
        start.setLayoutY(425);

        Button exit = new Button("Exit");
        exit.setOnAction(e -> Platform.exit());
        exit.setPrefSize(buttonWidth,buttonHeight*2);
        exit.setLayoutX(WINDOW_WIDTH/2-buttonWidth/2);
        exit.setLayoutY(495);

        home.getChildren().add(agamemnon);
        home.getChildren().add(start);
        home.getChildren().add(exit);
        root.getChildren().add(home);
    }

    // opens settings menu for deciding player names and game mode
    static void openSettings() {
        root.getChildren().remove(home);
        
        Text player1Text = new Text();
        Text player2Text = new Text();

        TextField player1NameField;
        TextField player2NameField;

        int buttonWidth = 150;
        int buttonHeight = 30;
        int alignFromLeft = 100;

        /* Player 1 side of settings options */

        player1Text.setText("Player 1");
        player1Text.setFont(Font.loadFont(CONSTANI_FONT,50));
        player1Text.setLayoutX(alignFromLeft+buttonWidth-(player1Text.getLayoutBounds().getWidth()/2));
        player1Text.setLayoutY(240);
        
        Text player1Name = new Text("Name:");
        player1Name.setLayoutX(alignFromLeft);
        player1Name.setLayoutY(player1Text.getLayoutY()+86);
        player1NameField = new TextField();
        player1NameField.setPrefWidth((2*buttonWidth)-player1Name.getLayoutBounds().getWidth()-5);
        player1NameField.setLayoutX(alignFromLeft+player1Name.getLayoutBounds().getWidth()+5);
        player1NameField.setLayoutY(player1Text.getLayoutY()+70);
        player1NameField.setOnKeyTyped(event -> {
            player1Text.setText(player1NameField.getText());
            player1Text.setLayoutX(alignFromLeft+buttonWidth-(player1Text.getLayoutBounds().getWidth()/2));
        });
        
        ChoiceBox player1Choice = new ChoiceBox(FXCollections.observableArrayList("Player", "Computer"));
        player1Choice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == 0) {
                    player1 = new Player("Player 1", Greek);
                    if(player1Text.getText().equals("Computer")||player1Text.getText().equals(""))
                        player1Text.setText("Player 1");
                    player1Text.setFont(Font.loadFont(CONSTANI_FONT,50));
                    player1Text.setLayoutX(alignFromLeft+buttonWidth-(player1Text.getLayoutBounds().getWidth()/2));
                } else {
                    player1 = new Agent("Greek Gorilla", Greek);
                    if(player1Text.getText().equals("Player 1")||player1Text.getText().equals(""))
                        player1Text.setText("Computer");
                    player1Text.setFont(Font.loadFont(ROLAND_FONT,65));
                    player1Text.setLayoutX(alignFromLeft+buttonWidth-(player1Text.getLayoutBounds().getWidth()/2));
                }
            }
        });
        player1Choice.setPrefWidth(2*buttonWidth);
        player1Choice.setLayoutX(alignFromLeft);
        player1Choice.setLayoutY(player1Text.getLayoutY()+30);

        /* Player 2 side of settings */

        player2Text.setText("Player 2");
        player2Text.setFont(Font.loadFont(CONSTANI_FONT,50));
        player2Text.setLayoutX(WINDOW_WIDTH-(alignFromLeft+buttonWidth+(player2Text.getLayoutBounds().getWidth()/2)));
        player2Text.setLayoutY(player1Text.getLayoutY());
        
        Text player2Name = new Text("Name:");
        player2Name.setLayoutX(WINDOW_WIDTH-(alignFromLeft+2*buttonWidth));
        player2Name.setLayoutY(player1Text.getLayoutY()+86);
        player2NameField = new TextField();
        player2NameField.setPrefWidth((2*buttonWidth)-player2Name.getLayoutBounds().getWidth()-5);
        player2NameField.setLayoutX(WINDOW_WIDTH-(alignFromLeft+player2NameField.getPrefWidth()));
        player2NameField.setLayoutY(player1Text.getLayoutY()+70);
        player2NameField.setOnKeyTyped(event -> {
            player2Text.setText(player2NameField.getText());
            player2Text.setLayoutX(WINDOW_WIDTH-(alignFromLeft+buttonWidth+(player2Text.getLayoutBounds().getWidth()/2)));
        });
        
        ChoiceBox player2Choice = new ChoiceBox(FXCollections.observableArrayList("Player", "Computer"));
        player2Choice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == 0) {
                    player2 = new Player("Player 2", Trojan);
                    if(player2Text.getText().equals("Computer")||player2Text.getText().equals(""))
                        player2Text.setText("Player 2");
                    player2Text.setFont(Font.loadFont(CONSTANI_FONT,50));
                    player2Text.setLayoutX(WINDOW_WIDTH-(alignFromLeft+buttonWidth+(player2Text.getLayoutBounds().getWidth()/2)));
                } else {
                    player2 = new Agent("Trojan Turkey", Trojan);
                    if(player2Text.getText().equals("Player 2")||player2Text.getText().equals(""))
                        player2Text.setText("Computer");
                    player2Text.setFont(Font.loadFont(ROLAND_FONT,65));
                    player2Text.setLayoutX(WINDOW_WIDTH-(alignFromLeft+buttonWidth+(player2Text.getLayoutBounds().getWidth()/2)));
                }
            }
        });
        player2Choice.setPrefWidth(2*buttonWidth);
        player2Choice.setLayoutX(WINDOW_WIDTH-(alignFromLeft+2*buttonWidth));
        player2Choice.setLayoutY(player1Text.getLayoutY()+30);
        
        /* other buttons and images */
        
        Button play = new Button();
        play.setPrefSize(buttonWidth, 2*buttonHeight);
        play.setText("Play!");
        play.setLayoutX((WINDOW_WIDTH/2)-(play.getPrefWidth()/2));
        play.setLayoutY(420);
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (player1NameField.getText().length() > 0) {
                    player1.changeName(player1NameField.getText());
                }
                if (player2NameField.getText().length() > 0) {
                    player2.changeName(player2NameField.getText());
                }
                Board.playGame(true);
            }
        });
        
        Button loom = new Button();
        loom.setPrefSize(buttonWidth, 2*buttonHeight);
        loom.setText("To the Loom!");
        loom.setLayoutX((WINDOW_WIDTH/2)-(loom.getPrefWidth()/2));
        loom.setLayoutY(420+(2*buttonHeight)+10);
        loom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (player1NameField.getText().length() > 0) {
                    player1.changeName(player1NameField.getText());
                }
                if (player2NameField.getText().length() > 0) {
                    player2.changeName(player2NameField.getText());
                }
                loomSelect();
            }
        });

        Button home = new Button();
        home.setPrefSize(buttonWidth, buttonHeight);
        home.setText("Back");
        home.setLayoutX((WINDOW_WIDTH/2)-(loom.getPrefWidth()/2));
        home.setLayoutY(420+(4*buttonHeight)+20);
        home.setOnAction(e -> openHome());
        
        ImageView greek = new ImageView(new Image(TILE_URI_BASE+"Oa.png"));
        greek.setLayoutX(150);
        greek.setLayoutY(400);
        greek.setOnMousePressed(e -> playSound("assets/sounds/greek.wav"));
        ImageView trojan = new ImageView(new Image(TILE_URI_BASE+"Ba.png"));
        trojan.setLayoutX(WINDOW_WIDTH-trojan.getLayoutBounds().getWidth()-150);
        trojan.setLayoutY(400);
        trojan.setOnMousePressed(e -> playSound("assets/sounds/trojan.wav"));

        settings.getChildren().addAll(player1Text, player1Choice, player1Name, player1NameField);
        settings.getChildren().addAll(player2Text, player2Choice, player2Name, player2NameField);
        settings.getChildren().addAll(play, loom, home, greek, trojan);
        root.getChildren().add(settings);
    }

    // opens menu for choosing loom options, specifically seed of loom board
    static void loomSelect() {
        root.getChildren().remove(settings);

        Button random = new Button();
        random.setText("Random Seed");
        random.setPrefSize(200,50);
        random.setLayoutX(200);
        random.setLayoutY(WINDOW_HEIGHT/2-30);
        random.setOnAction(e -> {
            gameSeed = -1;
            Board.playGame(false);
        });

        final Button[] seed = {new Button()};
        seed[0].setText("Choose Seed");
        seed[0].setPrefSize(200,50);
        seed[0].setLayoutX(WINDOW_WIDTH-200- seed[0].getPrefWidth());
        seed[0].setLayoutY(WINDOW_HEIGHT/2-30);
        seed[0].setOnAction(e -> {
            root.getChildren().remove(random);
            root.getChildren().remove(seed[0]);

            Text text = new Text("Choose a number from 1-24");
            text.setLayoutX(WINDOW_WIDTH/2-text.getLayoutBounds().getWidth()/2);
            text.setLayoutY(WINDOW_HEIGHT/2-40);

            ChoiceBox<Integer> choice = new ChoiceBox<>(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24));
            choice.setPrefWidth(120);
            choice.setLayoutX(WINDOW_WIDTH/2-choice.getPrefWidth()/2);
            choice.setLayoutY(WINDOW_HEIGHT/2-20);
            choice.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> gameSeed = t1.intValue()-1);

            Button play = new Button("Play");
            play.setPrefWidth(120);
            play.setPrefHeight(60);
            play.setLayoutX(WINDOW_WIDTH/2-play.getPrefWidth()/2);
            play.setLayoutY(WINDOW_HEIGHT/2+13);
            play.setOnAction(e1 -> Board.playGame(false));

            Button back = new Button("Back");
            back.setPrefWidth(120);
            back.setLayoutX(WINDOW_WIDTH/2-play.getPrefWidth()/2);
            back.setLayoutY(WINDOW_HEIGHT/2+20+play.getPrefHeight());
            back.setOnAction(e2 -> {
                root.getChildren().remove(text);
                root.getChildren().remove(choice);
                root.getChildren().remove(play);
                root.getChildren().remove(back);
                loomSelect();
            });

            root.getChildren().addAll(text, choice, play, back);
        });

        root.getChildren().addAll(random, seed[0]);
    }

    // opens menu for when a game is completed
    static void openGameOver() {
        playMusic("assets/end.wav");
        gameOver.getChildren().clear();
        GameBoard.toggleMessage(false);
        Text player1End = new Text();
        Text player2End = new Text();
        if (player1.getScore() > player2.getScore()) {
            player1End.setText("Victorious");
            player1End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player1End.setFill(Color.BLUEVIOLET);
            player2End.setText("Vanquished");
            player2End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player2End.setFill(Color.INDIANRED);
        } else if (player2.getScore() > player1.getScore()) {
            player2End.setText("Victorious");
            player2End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player2End.setFill(Color.BLUEVIOLET);
            player1End.setText("Vanquished");
            player1End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player1End.setFill(Color.INDIANRED);
        } else { // case when players draw
            player1End.setText("Impasse");
            player1End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player1End.setFill(Color.DARKSLATEGREY);
            player2End.setText("Impasse");
            player2End.setFont(Font.loadFont(CHRONICLE_FONT, 30));
            player2End.setFill(Color.DARKSLATEGREY);
        }
        player1End.setLayoutX(20);
        player1End.setLayoutY(WINDOW_HEIGHT-160);
        player2End.setLayoutX(WINDOW_WIDTH-(20+player2End.getLayoutBounds().getWidth()));
        player2End.setLayoutY(180);

        Button rematch = new Button("Rematch!");
        rematch.setPrefWidth(175); rematch.setPrefHeight(30);
        rematch.setLayoutX(WINDOW_WIDTH-rematch.getPrefWidth()-30);
        rematch.setLayoutY(640);
        rematch.setOnAction(e -> {
            if (player1 instanceof Agent)
                player1 = new Agent(player1.getName(), Greek);
            else
                player1 = new Player(player1.getName(), Greek);
            if (player2 instanceof Agent)
                player2 = new Agent(player2.getName(), Trojan);
            else
                player2 = new Player(player2.getName(), Trojan);
            playGame(game instanceof Agamemnon);
        });
        
        Button home = new Button("Home");
        home.setPrefWidth(175);    home.setPrefHeight(30);
        home.setLayoutX(rematch.getLayoutX());
        home.setLayoutY(rematch.getLayoutY()+10+rematch.getPrefHeight());
        home.setOnAction(e -> {
            player1 = new Player("Player 1", Greek);
            player2 = new Player("Player 2", Trojan);
            openHome();
        });
        
        Button exit = new Button("Exit");
        exit.setPrefWidth(175);    exit.setPrefHeight(30);
        exit.setLayoutX(rematch.getLayoutX());
        exit.setLayoutY(home.getLayoutY()+10+home.getPrefHeight());
        exit.setOnAction(e -> Platform.exit());

        Text gameOverText = new Text("Game Over");
        gameOverText.setFont(Font.loadFont(CHRONICLE_FONT, 30));
        gameOverText.setFill(Color.AQUA);
        gameOverText.setLayoutX(WINDOW_WIDTH-rematch.getPrefWidth()/2-30-gameOverText.getLayoutBounds().getWidth()/2);
        gameOverText.setLayoutY(rematch.getLayoutY()-gameOverText.getLayoutBounds().getHeight()+10);
        
        gameOver.getChildren().addAll(player1End, player2End, rematch, home, exit, gameOverText);
        root.getChildren().add(gameOver);

    }

    // opens pause menu while playing the game (Esc key)
    static void pauseMenu() {
        Button home = new Button("Return Home");
        home.setPrefWidth(175);    home.setPrefHeight(30);
        home.setLayoutX(WINDOW_WIDTH-home.getPrefWidth()-30);
        home.setLayoutY(640);
        home.setOnAction(e -> {
            player1 = new Player("Player 1", Greek);
            player2 = new Player("Player 2", Trojan);
            openHome();
        });

        Button exit = new Button("Exit");
        exit.setPrefWidth(175);    exit.setPrefHeight(30);
        exit.setLayoutX(home.getLayoutX());
        exit.setLayoutY(home.getLayoutY()+10+home.getPrefHeight());
        exit.setOnAction(e -> Platform.exit());

        Button back = new Button("Back to Game");
        back.setPrefWidth(175);    exit.setPrefHeight(30);
        back.setLayoutX(home.getLayoutX());
        back.setLayoutY(exit.getLayoutY()+10+exit.getPrefHeight());
        back.setOnAction(e -> {
            paused = false;
            root.getChildren().remove(pause);
        });

        Text pauseText = new Text("Pause");
        pauseText.setFont(Font.loadFont(CHRONICLE_FONT, 30));
        pauseText.setFill(Color.CHARTREUSE);
        pauseText.setLayoutX(WINDOW_WIDTH-home.getPrefWidth()/2-30-pauseText.getLayoutBounds().getWidth()/2);
        pauseText.setLayoutY(home.getLayoutY()-pauseText.getLayoutBounds().getHeight()+10);

        pause.getChildren().addAll(home, exit, back, pauseText);
        root.getChildren().add(pause);
    }

    // plays cloud and grass animation
    private static boolean cloudsForward = true;
    static {
        ImageView cloud1 = new ImageView(new Image("file:assets/cloud1.png"));
        ImageView cloud2 = new ImageView(new Image("file:assets/cloud2.png"));
        ImageView cloud3 = new ImageView(new Image("file:assets/cloud3.png"));

        int[] cloud1Pos = new int[]{30, 45};
        int[] cloud2Pos = new int[]{350, 45};
        int[] cloud3Pos = new int[]{680, 45};

        ImageView grass1 = new ImageView(new Image("file:assets/grass1.png"));
        ImageView grass2 = new ImageView(new Image("file:assets/grass2.png"));
        ImageView grass3 = new ImageView(new Image("file:assets/grass3.png"));

        grass1.setLayoutY(WINDOW_HEIGHT-grass1.getLayoutBounds().getHeight());
        grass2.setLayoutY(grass1.getLayoutY());
        grass3.setLayoutY(grass1.getLayoutY());
        AtomicInteger grassCounter = new AtomicInteger();

        clouds.getChildren().addAll(cloud1, cloud2, cloud3);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200),
                ae -> {
                    if (cloudsForward) {
                        cloud1Pos[0] += 8;
                        cloud2Pos[0] += 8;
                        cloud3Pos[0] += 8;
                    } else {
                        cloud1Pos[0] -= 8;
                        cloud2Pos[0] -= 8;
                        cloud3Pos[0] -= 8;
                    }

                    cloud1.setLayoutX(cloud1Pos[0]); cloud1.setLayoutY(cloud1Pos[1]);
                    cloud2.setLayoutX(cloud2Pos[0]); cloud2.setLayoutY(cloud2Pos[1]);
                    cloud3.setLayoutX(cloud3Pos[0]); cloud3.setLayoutY(cloud3Pos[1]);

                    if (cloud3Pos[0]+cloud3.getLayoutBounds().getWidth() >= WINDOW_WIDTH)
                        cloudsForward = false;
                    if (cloud1Pos[0] <= 0)
                        cloudsForward = true;

                    grassCounter.getAndIncrement();
                    grass.getChildren().clear();
                    switch ((grassCounter.intValue() / 3) % 4) {
                        case 0:
                            grass.getChildren().add(grass1); break;
                        case 1: case 3:
                            grass.getChildren().add(grass2); break;
                        case 2:
                            grass.getChildren().add(grass3);
                    }

                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
