package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * Create the Instruction Scene which extends the Base Scene
 */
public class InstructionScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(Game.class);
    /**
     * Plays the sound effects and background music
     */
    protected Multimedia multimedia = new Multimedia();

    /**
     * Create a Instruction scene
     *
     * @param gameWindow : game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }
    /**
     * Initialise
     */
    @Override
    public void initialise() {
        //when the ESC key is pressed, it will exit to the menu
        scene.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE ) {
                logger.info("Clean");
                gameWindow.cleanup();
                gameWindow.startMenu();
            }
        });
        multimedia.playBackgroundMusic("menu.mp3");

    }
    /**
     * Build the layout
     */
    @Override
    public void build() {
        logger.info("Building Instruction Scene");
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        //Root
        var instructionPane = new StackPane();
        instructionPane.setMaxWidth(gameWindow.getWidth());
        instructionPane.setMaxHeight(gameWindow.getHeight());
        instructionPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionPane);

        var mainPane = new BorderPane();
        root.getChildren().add(mainPane);


        //Title
        var titleBox = new HBox();
        BorderPane.setAlignment(titleBox, Pos.CENTER);
        titleBox.setAlignment(Pos.TOP_CENTER);
        mainPane.setTop(titleBox);
        var instructionsLabel = new Text("Instructions");
        instructionsLabel.getStyleClass().add("heading");
        titleBox.getChildren().add(instructionsLabel);

        //Instruction Image
        var instructionImg= new ImageView(new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm()));
        mainPane.setCenter(instructionImg);
        instructionImg.setPreserveRatio(true);
        instructionImg.setFitWidth(gameWindow.getHeight());



        //Types of Pieces
        GridPane gridPane = new GridPane();
        for(int i = 0; i<8;i++ ){
            var pieceBoard = new PieceBoard(3,3,gameWindow.getWidth()/12, gameWindow.getWidth()/12);
            pieceBoard.display(GamePiece.createPiece(i));
            gridPane.add(pieceBoard,i,0);
        }
        for(int i = 8; i<15;i++ ){
            var pieceBoard = new PieceBoard(3,3,gameWindow.getWidth()/12, gameWindow.getWidth()/12);
            pieceBoard.display(GamePiece.createPiece(i));
            gridPane.add(pieceBoard,i-8,1);
        }
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setTranslateY(-25);
        mainPane.setBottom(gridPane);




        }

    }

