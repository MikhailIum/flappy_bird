import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLSyntaxErrorException;
import java.util.Scanner;
//i love vodka

public class BirdPanel extends JPanel {
    public Bird bird = new Bird();
    public Pipe pipeFirst = new Pipe();
    public Pipe pipeSecond = new Pipe();

    // добавление картинок
    BufferedImage background = ImageIO.read(new File("res/background.png"));
    BufferedImage ground = ImageIO.read(new File("res/long_ground.png"));
    BufferedImage resetImage = ImageIO.read(new File("res/resetImage.png"));

    // добавление кнопки reset
    JButton resetBtn = new JButton();

    int points = 0;
    String bestResultString;

    double groundKf = 0;
    double pipeFirstKf = 0;
    double pipeSecondKf = 0;
    boolean isFirstPipeInCenter = false;
    boolean isPressed;
    int numberOfBestScoreDigits;
    int numberOfScoreDigits;
    double birdPositionDouble = 0;
    int birdPosition;


    public BirdPanel() throws IOException {
        isPressed = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
       super.paintComponent(g);
       birdPosition = (int) Math.floor(birdPositionDouble);

       if (!bird.gameOver)
       {
           this.remove(resetBtn);
           groundKf -= 1;
           if (isPressed) pipeFirstKf += 1;
           if (isFirstPipeInCenter) pipeSecondKf += 1;
       }

        // фон
        g.drawImage(background, 0 , -100, null);

        // трубы
        g.drawImage(pipeFirst.pipeBottom, (int) pipeFirst.pipeLocationX - (int) pipeFirstKf, (int) (pipeFirst.pipeLocationY), null);
        g.drawImage(pipeFirst.pipeTop, (int) pipeFirst.pipeLocationX - (int) pipeFirstKf, (int) (pipeFirst.pipeLocationY - pipeFirst.pipeTop.getHeight() - 180), null);
        if (!isFirstPipeInCenter && (int) pipeFirst.pipeLocationX - (int) pipeFirstKf < 150)
            isFirstPipeInCenter = true;
        if (isFirstPipeInCenter) {
            g.drawImage(pipeSecond.pipeBottom, (int) pipeSecond.pipeLocationX - (int) pipeSecondKf, (int) (pipeSecond.pipeLocationY), null);
            g.drawImage(pipeSecond.pipeTop, (int) pipeSecond.pipeLocationX - (int) pipeSecondKf, (int) (pipeSecond.pipeLocationY - pipeSecond.pipeTop.getHeight() - 180), null);
        }
        if (pipeFirstKf > 700) {
            pipeFirstKf = 0;
            pipeFirst.changingLocationY();
        }
        if (pipeSecondKf > 700) {
            pipeSecondKf = 0;
            pipeSecond.changingLocationY();
        }

        // птичка
        g.drawImage(forChangingAngle().filter(bird.birdImages[birdPosition], null), (int) bird.birdLocationX, (int) bird.birdLocationY, null);

        // земля
        g.drawImage(ground, (int) groundKf, 580, null);
        if (groundKf < -37*15) groundKf = 0;

        // create the font

        Font customFont = Font.getFont("TimesRoman");
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Samson.ttf")).deriveFont(Font.PLAIN, 100);
        } catch (FontFormatException | IOException e) {
            System.out.println(e);
        }

        //счёт
        int x = points;
        while (x > 0) {
            numberOfScoreDigits++;
            x /= 10;
        }
        String pointsString = String.valueOf(points);
        if (!bird.gameOver) {
            g.setFont(customFont);
            g.setColor(new Color(0xFFFFFF));
            g.drawString(pointsString, 200 - 20 * (numberOfScoreDigits - 1), 100);
        }
        numberOfScoreDigits = 0;


        if(bird.gameOver){
            // менюшка
            g.drawImage(resetImage, 150,200, null);
            g.setFont(customFont.deriveFont(Font.PLAIN, 50));
            g.setColor(new Color(0xF97757));
            g.drawString("Game over", 135, 170);

            // очки
            x = points;
            if (x == 0) numberOfScoreDigits++;
            while (x > 0) {
                numberOfScoreDigits++;
                x /= 10;
            }
            g.setFont(customFont.deriveFont(Font.PLAIN, 70));
            g.setColor(new Color(0xFFFFFF));
            g.drawString(pointsString, 225 - 10 * (numberOfScoreDigits - 1), 305);
            numberOfScoreDigits = 0;

            // кнопка рестарта
            ImageIcon resetBtnImage = new ImageIcon("res/resetBtnImage.png");

            resetBtn.setSize(resetBtnImage.getIconWidth(), resetBtnImage.getIconHeight());
            resetBtn.setBorder(null);
            resetBtn.setLocation(135,450);
            resetBtn.setIcon(resetBtnImage);

            this.add(resetBtn);

            try {
                bestScore();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            g.drawString(bestResultString, 225 - 20 * (numberOfBestScoreDigits - 1), 390);

            resetBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    restart();
                }
            });
        }
    }

    public void restart(){
        bird.gameOver = false;
        points = 0;
        pipeFirstKf = 0;
        pipeSecondKf = 0;
        isPressed = false;
        isFirstPipeInCenter = false;
        bird.birdSpeed = 0;
        bird.birdLocationY = 300;
        bird.birdAngleInDegrees = 0;
        pipeFirst.changingLocationY();
        pipeSecond.changingLocationY();
    }

    public void bestScore() throws FileNotFoundException {
        String fileString;
        int bestResult = 0;
        FileReader fileReader = new FileReader("leaderboard.txt");
        Scanner scanner = new Scanner(fileReader);
        bestResultString = "";


        if (scanner.hasNext()) {
            fileString = scanner.nextLine();
            for (int i = 0; i < fileString.length(); i++) {
                if (fileString.charAt(i) >= '0' && fileString.charAt(i) <= '9') {
                    bestResult = bestResult * 10 + fileString.charAt(i) - 48;
                    bestResultString = Integer.toString(bestResult);
                }
            }
        }


        if (points > bestResult) {
            try {
                FileWriter fileWriter = new FileWriter("leaderboard.txt");
                fileWriter.write(Integer.toString(this.points));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bestResultString = Integer.toString(points);
        }


        try {
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        numberOfBestScoreDigits = bestResultString.length();
    }

    public void checkCollision() throws InterruptedException {
        if (((bird.birdLocationX + bird.birdImages[birdPosition].getWidth() - 4 > pipeFirst.pipeLocationX - pipeFirstKf
                && bird.birdLocationX + 8 < pipeFirst.pipeLocationX - pipeFirstKf + pipeFirst.pipeTop.getWidth()
        ) && (bird.birdLocationY + bird.birdImages[birdPosition].getHeight() - 8 > pipeFirst.pipeLocationY || bird.birdLocationY + 8 < pipeFirst.pipeLocationY - 180))
        || ((bird.birdLocationX + bird.birdImages[birdPosition].getWidth()  - 4 > pipeSecond.pipeLocationX - pipeSecondKf
                && bird.birdLocationX + 8 < pipeSecond.pipeLocationX - pipeSecondKf + pipeSecond.pipeTop.getWidth()
        ) && (bird.birdLocationY + bird.birdImages[birdPosition].getHeight() - 8 > pipeSecond.pipeLocationY || bird.birdLocationY + 8 < pipeSecond.pipeLocationY - 180))){

            bird.gameOver = true;

            while (bird.birdLocationY < 530) {
                bird.birdLocationY += 5;
                Thread.sleep(0,1);
                this.repaint();
            }
        }

        if ((bird.birdLocationX == pipeFirst.pipeLocationX - pipeFirstKf) || (bird.birdLocationX == pipeSecond.pipeLocationX - pipeSecondKf))
            points++;

    }



    private AffineTransformOp forChangingAngle(){
        double angleInDegrees = bird.birdAngleInDegrees; // Угол поворота в градусах
        double angleInRadians = Math.toRadians(angleInDegrees);
        double locationX = bird.birdImages[birdPosition].getWidth() / 2;
        double locationY = bird.birdImages[birdPosition].getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(angleInRadians, locationX, locationY);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    }

    public void beforeJumping() throws InterruptedException {
        while (!isPressed){
            while (bird.birdLocationY < 310) {
                if (isPressed) break;
                bird.birdLocationY += 0.1;
                repaint();
                birdPositionDouble += 0.02;
                if (birdPositionDouble >= 3)
                    birdPositionDouble = 0;
                Thread.sleep(1);
            }
            while (bird.birdLocationY > 290) {
                if (isPressed) break;
                bird.birdLocationY -= 0.1;
                repaint();
                birdPositionDouble += 0.02;
                if (birdPositionDouble >= 3)
                    birdPositionDouble = 0;
                Thread.sleep(1);
            }
        }
    }
}
