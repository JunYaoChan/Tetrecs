package uk.ac.soton.comp1206.utility;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Create class that allowed to play or stop the music
 */
public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    /**
     * back
     */
    private static MediaPlayer mediaPlayer;

    /**
     * background
     */
    private static MediaPlayer backgroundPlayer;


    /**
     * Plays the background Music
     * @param music music file to be played
     */
    public static void playBackgroundMusic(String music){
        String toPlay = Multimedia.class.getResource("/music/" + music).toExternalForm();
        try{
            Media play = new Media(toPlay);
            backgroundPlayer = new MediaPlayer(play);
            backgroundPlayer.setAutoPlay(true);
            //loop
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.play();
            logger.info("Playing Media Sound: " + music);



        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.toString());
        }


    }
    /**
     * plays the sounds effect
     * @param sound store the music
     */
    public static void playSound(String sound) {
        String toPlay = Multimedia.class.getResource("/sounds/" + sound).toExternalForm();
            try {
                Media play = new Media(toPlay);
                mediaPlayer = new MediaPlayer(play);
                mediaPlayer.play();
                logger.info("Playing Media Sound: " + sound);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.toString());
            }
        }
    /**
     * Stop background music
     */
    public static void stopBackgroundMusic() {
        backgroundPlayer.stop();
    }


}
