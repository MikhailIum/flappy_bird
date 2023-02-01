import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bird {
    double birdLocationX;
    double birdLocationY;
    double birdSpeed;
    double birdAngleInDegrees;
    public BufferedImage[] birdImages = new BufferedImage[3];
    boolean isJumping;
    boolean gameOver;
    public static final double g = 0.12; //g = 0.12

    Bird() throws IOException {
        birdImages[0] = ImageIO.read(new File("res/birds.png")).getSubimage(0, 0, 60, 42 );
        birdImages[1] = ImageIO.read(new File("res/birds.png")).getSubimage(60, 0, 60, 42 );
        birdImages[2] = ImageIO.read(new File("res/birds.png")).getSubimage(120, 0, 60, 42 );
        birdSpeed = 0;
        birdLocationX = 50;
        birdLocationY = 300;
        birdAngleInDegrees = 0;
        isJumping = false;
        gameOver = false;
    }



    public void jump(double dt){
        isJumping = true;
        while (birdSpeed > - 8){
            birdSpeed = (birdSpeed - g * dt * 2);
            update(2);
            birdAngleInDegrees = -30;
        }
        isJumping = false;
    }

    public void update(double dt){
        if (birdLocationY < 530)
        {
            if (!isJumping && birdSpeed > - 18) birdSpeed = (birdSpeed + g * dt / 2);
            if (!isJumping && birdAngleInDegrees < 90) birdAngleInDegrees += 0.28;
            birdLocationY = (birdLocationY + birdSpeed * dt * g);
        }
        else gameOver = true;
    }

}
