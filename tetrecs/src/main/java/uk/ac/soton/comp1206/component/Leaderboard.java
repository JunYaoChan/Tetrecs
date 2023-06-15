package uk.ac.soton.comp1206.component;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * The Leaderboard class extends the score list class that and handles the online leaderboard
 */
public class Leaderboard extends ScoreList{

    private static final Logger logger = LogManager.getLogger(Leaderboard.class);

    private ArrayList<String> userDEAD = new ArrayList();

    /**
     * Set the vbox
     */
    public Leaderboard(){
        setAlignment(Pos.CENTER);
        setSpacing(1);
        getStyleClass().add("scoreList");
        this.scores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.scores.addListener(this::updateScoresList);




    }

    /**
     * Updates the scores in real time
     */
    @Override
    public void updateScores() {
        logger.info("Update LeaderBoard");
        getChildren().clear();
        int count =0;

        for(Pair i : scores) {
            if (count == 3) {
                break;
            }
            logger.info("Method called : " + count);
            Text player = new Text(i.getKey().toString()+ " : "+ i.getValue().toString());
            player.getStyleClass().add("heading");
            logger.info(userDEAD.contains(i.getKey()));
            if(userDEAD.contains(i.getKey().toString())){
                player.setStrikethrough(true);
            }
            getChildren().add(player);


            reveal(player);
            count++;
        }

    }

    /**
     * handles the dead player
     * @param name is the username of the player
     */
    public void userDIE(String name){
        userDEAD.add(name);
        logger.info(userDEAD.get(0));



    }
}
