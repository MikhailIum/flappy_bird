import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Pipe {
    double pipeLocationX;
    double pipeLocationY;
    BufferedImage pipeBottom;
    BufferedImage pipeTop;

    public Pipe() throws IOException {
        pipeBottom = ImageIO.read(new File("res/pipeBottom.png"));
        pipeTop = ImageIO.read(new File("res/pipeTop.png"));
        pipeLocationY = new Random().nextInt(395) + 185;
        pipeLocationX = 500;
    }

    public void changingLocationY(){
        pipeLocationY = new Random().nextInt(395) + 185;
    }


}
