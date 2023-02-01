import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        JFrame frame = new JFrame();
        frame.setLocation(650,180);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Летучая птица");
        frame.setResizable(true);
        frame.setSize(500, 700);


        BirdPanel birdPanel = new BirdPanel();
        frame.add(birdPanel);


        frame.setIconImage(birdPanel.bird.birdImages[0]);

//        frame.setPreferredSize(new Dimension(500, 700));
//        frame.pack();
//        frame.createBufferStrategy(2);
        frame.setVisible(true);


        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            boolean spaceAlreadyPressed = false;

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                    if (!spaceAlreadyPressed)
                    {
                        if (birdPanel.bird.gameOver)
                            birdPanel.restart();
                        else if (e.getKeyCode() == KeyEvent.VK_SPACE && KeyEvent.KEY_PRESSED == e.getID())
                        {
                            birdPanel.isPressed = true;
                            birdPanel.bird.jump(2);
                            spaceAlreadyPressed = true;
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_SPACE && KeyEvent.KEY_RELEASED == e.getID()) spaceAlreadyPressed = false;
                return false;
            }
        });


        while(true){
            if (!birdPanel.isPressed) birdPanel.beforeJumping();
            if (!birdPanel.bird.gameOver) {
                long prevTime = System.currentTimeMillis();
                Thread.sleep(0, 1);
                double dt = System.currentTimeMillis() - prevTime;
                birdPanel.bird.update(dt);
                birdPanel.checkCollision();
            }

            birdPanel.repaint();
            if (!birdPanel.bird.gameOver) birdPanel.birdPositionDouble += 0.03;
            if (birdPanel.birdPositionDouble >= 3)
                birdPanel.birdPositionDouble = 0;
//            BufferStrategy bs = frame.getBufferStrategy();
//            Graphics g = bs.getDrawGraphics();
//
//            System.out.println(1);
//            birdPanel.paint(g);
//
//            g.dispose();
//            bs.show();


            Thread.sleep(1);
        }


    }
}
