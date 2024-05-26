import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    public static void playSound(String soundFile) {
        try {
            File soundPath = new File("src/" + soundFile);
            if (soundPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("Sound file not found: " + soundFile);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}