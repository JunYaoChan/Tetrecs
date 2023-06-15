package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;


/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Play sound effect and background music
     */
    protected Multimedia multimedia = new Multimedia();

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");

        root.getChildren().add(menuPane);


        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Title
        Image titleImage = new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        ImageView imageView = new ImageView(titleImage);
        imageView.setPreserveRatio(true);
        mainPane.setCenter(imageView);
        imageView.setFitHeight(150);
//        imageView.setTranslateY(-10);
        imageView.setRotate(-7);
        // Create a RotateTransition
        RotateTransition rotation = new RotateTransition(Duration.seconds(1), imageView);
        rotation.setToAngle(10);
        rotation.setAutoReverse(true); // Optional: Set to true if you want the animation to reverse after completion

        rotation.setCycleCount(RotateTransition.INDEFINITE); // Optional: Set the number of cycles for the animation (INDEFINITE for infinite loop)
        rotation.play();


        rotation.play();


        var buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        mainPane.setBottom(buttonBox);
        buttonBox.setSpacing(10);
        var playButton = new Button("Single Player");
        playButton.getStyleClass().add("buttonName1");

        var multiplayerButton = new Button("Multiplayer");
        multiplayerButton.getStyleClass().add("buttonName1");


        //Instruction Button
        var instructionsButton = new Button("Instruction");
        instructionsButton.getStyleClass().add("buttonName1");

        var exitButton = new Button("Exit");
        exitButton.getStyleClass().add("buttonName1");

        //Make the buttons transparent to the background
        playButton.setBackground(null);
        multiplayerButton.setBackground(null);
        instructionsButton.setBackground(null);
        exitButton.setBackground(null);



        //Bind the button action to the startGame method in the menu
        playButton.setOnAction(this::startGame);

        //Bind the button action to the showInstruction method in the menu
        instructionsButton.setOnAction(this::showInstruction);

        //Bind the button action to the startMultiplayer method in the menu
        multiplayerButton.setOnAction(this::startMultiplayer);

        //Bind the button action to exit the application
        exitButton.setOnMouseClicked(event ->  App.getInstance().shutdown());

        //Start the background music
        this.multimedia.playBackgroundMusic("menu.mp3");

        buttonBox.getChildren().addAll(playButton, multiplayerButton,instructionsButton,exitButton);
    }

    /**
     * Handle when the startMultiplayer button is pressed
     * @param event event
     */
    private void startMultiplayer(ActionEvent event) {
        gameWindow.startLobby();
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        Multimedia.stopBackgroundMusic();
        gameWindow.startChallenge();
    }

    /**
     * Handle when the showInstruction button is pressed
     * @param event event
     */
    private void showInstruction(ActionEvent event) {
        gameWindow.startInstructions();
    }

}
