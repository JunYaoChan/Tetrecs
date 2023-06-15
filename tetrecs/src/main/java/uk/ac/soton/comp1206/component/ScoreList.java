package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 *  ScoresList extends the VBox and handles the local scores
 */
public class ScoreList extends VBox {
    private static final Logger logger = LogManager.getLogger(ScoreList.class);


    /**
     * Store all the score in the simple list property
     */
    protected SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty();

    /**
     * Store the username
     */
    protected SimpleStringProperty username = new SimpleStringProperty();



    /**
     * Display the score
     */
    public ScoreList(){
        setAlignment(Pos.CENTER);
        setSpacing(1);
        getStyleClass().add("scoreList");
        this.scores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.scores.addListener(this::updateScoresList);

    }

    /**
     * Listener Method
     * @param changes detects changes to the value
     */
    protected void updateScoresList(ListChangeListener.Change<? extends Pair<String, Integer>> changes) {
        updateScores();

    }

    /**
     * Update all the score and display their name and score
     */
    protected void updateScores() {
        logger.info("Update Scores");
        getChildren().clear();
        int count =0;

        for(Pair i : scores) {
            if (count == 10) {
                break;
            }

            Text player = new Text(i.getKey().toString());
            if (this.getUsernameProperty().equals(i.getKey())) {
                player.getStyleClass().add("heading");
            } else {
                player.getStyleClass().add("heading");

            }
            var boxes = new HBox();
            boxes.setSpacing(10);
            Text number = new Text(Integer.toString(count + 1));
            Text score = new Text(i.getValue().toString());
            score.getStyleClass().add("heading");
            boxes.getChildren().addAll(number, player, score);
            number.getStyleClass().add("heading");
            getChildren().add(boxes);

            reveal(boxes);
            count++;
        }



        }
//    }

    /**
     * Method that return the username
     * @return username
     */
    public SimpleStringProperty getUsernameProperty() {
        return this.username;
    }

    /**
     * Animate the text in the boxes
     * @param boxes contains all the text
     */
    public void reveal(Node boxes){
            FadeTransition fade = new FadeTransition(Duration.millis(1500), boxes);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

    }
    /**
     * Get the score
     * @return the scores array
     */
    public SimpleListProperty<Pair<String, Integer>> getScoresProperty() {
        return scores;
    }
}
