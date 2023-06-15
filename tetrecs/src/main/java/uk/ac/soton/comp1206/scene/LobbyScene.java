package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create a Lobby Scene
 */
public class LobbyScene extends BaseScene{


    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    /**
     * Receive and send message to the server
     */
    protected Communicator communicator;

    /**
     * Initialise the scene
     */
    protected Timer timer;

    /**
     * Store all the necessary nodes in the left boxes
     */
    protected VBox leftBox;

    /**
     * Store the available channel from the server
     */
    protected VBox channelBox;
    /**
     * Store all the necessary nodes in the right boxes
     */
    protected VBox rightBox;

    /**
     * Arrange the node in the left box
     */
    protected BorderPane channelRoom;

    /**
     * Store all the chat message in the vertical boxes
     */
    protected VBox chatBox;

    /**
     * Store the username
     */
    protected String userName;

    /**
     * Store the create button
     */
    protected HBox createBox;

    /**
     * State the name of the current channel
     */
    protected String channel;

    /**
     * Display the name of channel
     */
    protected Text channelName;

    /**
     * Arrange the node for the chat message
     */
    protected BorderPane chatPane;

    /**
     * Used to type to send chat message
     */
    protected TextField text;

    /**
     * Scroll the chatBox
     */
    protected ScrollPane scrollPane;

    /**
     * Button to start the game
     */
    protected Button startGame;

    /**
     * Button to leave the channel
     */
    protected Button leaveChannel;

    /**
     * Arrange the buttons(start and leave)
     */
    protected BorderPane buttonBox;

    /**
     * Store box for the editing the name
     */
    protected HBox nameBox;


    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Lobby Scene");
        communicator = gameWindow.getCommunicator();
    }

    @Override
    public void initialise() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                communicator.send("LIST");
            }
        },1,2000);
        communicator = gameWindow.getCommunicator();
        communicator.addListener(message -> Platform.runLater(() -> handleMessage(message)));
        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()==KeyCode.ESCAPE){
                Multimedia.stopBackgroundMusic();
                gameWindow.startMenu();
            }
        });


    }



    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("lobby-background");
        root.getChildren().add(lobbyPane);

        var mainPane = new BorderPane();
        lobbyPane.getChildren().add(mainPane);

        //Title
        var titleBox = new HBox();
        var title = new Text("Lobby");
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        title.getStyleClass().add("bigtitle");
        mainPane.setTop(titleBox);

        var allBox = new BorderPane();
        mainPane.setCenter(allBox);

        //Left Box (show all the available channel)
        leftBox = new VBox();
        allBox.setLeft(leftBox);
        leftBox.setTranslateX(20);
        leftBox.setSpacing(10);
        var channelTitle = new Text("Channels");
        channelTitle.getStyleClass().add("heading");


        //Create Channel Button
        createBox = new HBox();
        createBox.setSpacing(10);
        var createButton = new Button("Create Channel");
        createButton.getStyleClass().add("blue-button");
        createBox.getChildren().add(createButton);
        createButton.setOnAction(event -> {
            createButton.setDisable(true);
            var createName = new TextField();
            createBox.getChildren().add(createName);
            createName.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    communicator.send("CREATE " + createName.getText());
                    channelName.setText("Channels : "+ createName.getText());
                    rightBox.setVisible(true);
                    createBox.getChildren().remove(createName);
                    createButton.setDisable(false);
                }
                });
            });

        //Edit Nickname Button
        nameBox = new HBox();
        var nameButton = new Button("Edit Nickname");
        nameBox.getChildren().add(nameButton);
        nameButton.getStyleClass().add("blue-button");
        nameButton.setOnAction(event -> {
            nameButton.setDisable(true);
            nameBox.getChildren().remove(nameButton);
            var createNickName = new TextField();
            nameBox.getChildren().add(createNickName);
            createNickName.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    communicator.send("NICK " + createNickName.getText());
                    userName=createNickName.getText();
                    nameBox.getChildren().remove(createNickName);
                    nameButton.setDisable(false);
                    nameBox.getChildren().add(nameButton);
                    nameButton.setVisible(true);
                }
            });
        });


        leftBox.getChildren().addAll(channelTitle,createBox);

        //Edit Username
        var name = new TextField();
        name.setPromptText("Enter your name ");


        //Right Box (appear when you click on the channel)
        rightBox = new VBox();
        allBox.setRight(rightBox);
        channelRoom = new BorderPane();
        rightBox.setVisible(false);

        //Channel Name
        channelName = new Text("Channel : " + channel);
        channelName.getStyleClass().add("heading");
        channelRoom.setLeft(channelName);
        channelRoom.setRight(nameBox);


        //ChatBox
        chatPane = new BorderPane();
        chatBox = new VBox();
        chatPane.getStyleClass().add("gameBox");
        scrollPane = new ScrollPane(chatBox);
        scrollPane.setPrefSize(gameWindow.getWidth()/2+30,gameWindow.getHeight()/2+30);
        scrollPane.getStyleClass().add("scroller");
        chatPane.setCenter(scrollPane);
        chatBox.getStyleClass().add("messages");

        //Message Box
        var horizontalPane = new HBox();
        chatPane.setBottom(horizontalPane);
        text = new TextField();
        text.setPromptText("Enter message");
        HBox.setHgrow(text, Priority.ALWAYS);


        text.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()==KeyCode.ENTER){
                communicator.send("MSG "+text.getText());
                text.clear();
            }
        });
        horizontalPane.getChildren().add(text);

        //Start Game and Leave Channel Buttons
        buttonBox = new BorderPane();
        startGame = new Button("Start Game");
        startGame.getStyleClass().add("green-button");
        startGame.setOnAction(event -> {
            communicator.send("START");
            Multimedia.stopBackgroundMusic();

        });
        startGame.setVisible(false);
        leaveChannel = new Button("Leave Channel");
        leaveChannel.getStyleClass().add("red-button");
        leaveChannel.setOnAction(event -> {
            communicator.send("PART");
            rightBox.setVisible(false);
            createBox.setVisible(true);

        });
        buttonBox.setLeft(leaveChannel);
        buttonBox.setRight(startGame);
        rightBox.getChildren().addAll(channelRoom,chatPane,buttonBox);

    }


    /**
     * Handles messages that are received form the server
     * @param message holds the message
     */
    protected void handleMessage(String message) {

        if(message.startsWith("CHANNELS")){
            leftBox.getChildren().remove(channelBox);
            channelBox = new VBox();
            channelBox.setSpacing(10);

            var channels = message.replace(" ","").replace("CHANNELS","").split("\n");
            logger.info(channels[0]);
            for (String channelNames : channels){
                var channelsButton = new Button(channelNames);
                channelsButton.getStyleClass().add("buttonName");

                channelsButton.setOnAction(event ->{
                    logger.info("ButtonClicked");
                    communicator.send("JOIN " + channelNames);

                });
                channelBox.getChildren().add(channelsButton);
            }
            leftBox.getChildren().add(channelBox);

        }else if(message.startsWith("JOIN")){
            chatBox.getChildren().clear();
            var joinArr = message.split(" ");
            channelName.setText("Channels : "+joinArr[1]);
            rightBox.setVisible(true);
            createBox.setVisible(false);


        }else if (message.startsWith("MSG")){
            var msg = message.replace("MSG ","").split(":");
            Text text = new Text(msg[0]+" : "+msg[1]);
            text.getStyleClass().add("heading");
            chatBox.getChildren().add(text);
            scrollPane.setVvalue(1);


        }else if(message.startsWith("USERS")){
            var users = message.replace(" ","").replace("USERS","").split("\n");
            var userBox = new HBox();
            userBox.setSpacing(10);
            for(String user : users){
                var name = new Text(user);
                if(user.equals(userName)) {
                    name.getStyleClass().add("userName");
                }else{
                    name.getStyleClass().add("heading");
                }
                userBox.getChildren().add(name);
            }
            chatPane.setTop(userBox);

        } else if (message.startsWith("START")) {
            Multimedia.playSound("transition.wav");
            gameWindow.loadScene(new MultiplayerScene(gameWindow));


        } else if(message.startsWith("HOST")){
            startGame.setVisible(true);

        }else if(message.startsWith("NICK")){
            var name =message.replace("NICK ","");
            userName = name;

        }else if(message.startsWith("ERROR")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(message.replace("ERROR",""));
            alert.showAndWait();


        }
        scrollPane.setVvalue(1);

    }
}
