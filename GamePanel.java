import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int PADDLE_WIDTH = 10;
    private final int PADDLE_HEIGHT = 150;
    private final int BALL_SIZE = 30;
    private int playerY = 225, aiY = 225;
    private int ballX, ballY;
    private int ballDX, ballDY;
    private int playerScore = 0, aiScore = 0;
    private Timer timer;
    private boolean upPressed, downPressed, wPressed, sPressed;
    private Clip paddleHitSound;
    private Clip scoreSound;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(5, this);
        loadSounds();
        resetGame();
        timer.start();
    }

    private void loadSounds() {
    try {
        AudioInputStream paddleHitStream = AudioSystem.getAudioInputStream(new File("batida.wav"));
        Clip paddleHitClip = AudioSystem.getClip();
        paddleHitClip.open(paddleHitStream);
        FloatControl paddleHitVolume = (FloatControl) paddleHitClip.getControl(FloatControl.Type.MASTER_GAIN);
        paddleHitVolume.setValue(-40.0f); 

        AudioInputStream scoreStream = AudioSystem.getAudioInputStream(new File("ponto.wav"));
        Clip scoreClip = AudioSystem.getClip();
        scoreClip.open(scoreStream);
        FloatControl scoreVolume = (FloatControl) scoreClip.getControl(FloatControl.Type.MASTER_GAIN);
        scoreVolume.setValue(-50.0f); 

        paddleHitSound = paddleHitClip;
        scoreSound = scoreClip;
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        e.printStackTrace();
    }
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(10, playerY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(760, aiY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
        g.setFont(new Font("Arial", Font.PLAIN, 35));
        g.drawString("Player: " + playerScore, 50, 30);
        g.drawString("AI: " + aiScore, 650, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ballX += ballDX;
        ballY += ballDY;

        if (ballY <= 0 || ballY >= getHeight() - BALL_SIZE) {
            ballDY = -ballDY;
        }

        if (ballX <= 20 && ballY + BALL_SIZE >= playerY && ballY <= playerY + PADDLE_HEIGHT) {
            ballDX = -ballDX;
            playPaddleHitSound();
        } else if (ballX >= 740 && ballY + BALL_SIZE >= aiY && ballY <= aiY + PADDLE_HEIGHT) {
            ballDX = -ballDX;
            playPaddleHitSound();
        }

        if (ballX < 0) {
            aiScore++;
            resetBall();
            playScoreSound();
        }

        if (ballX > getWidth()) {
            playerScore++;
            resetBall();
            playScoreSound();
        }

        if (aiY + PADDLE_HEIGHT / 2 < ballY) {
            aiY = Math.min(aiY + 5, getHeight() - PADDLE_HEIGHT); 
        } else if (aiY + PADDLE_HEIGHT / 2 > ballY) {
            aiY = Math.max(aiY - 5, 0); 
        }

        if (upPressed || wPressed) {
            playerY = Math.max(playerY - 8, 0); 
        }
        if (downPressed || sPressed) {
            playerY = Math.min(playerY + 8, getHeight() - PADDLE_HEIGHT); 
        }

        if (playerScore >= 5) {
            JOptionPane.showMessageDialog(this, "Você ganhou!");
            resetGame();
        } else if (aiScore >= 5) {
            JOptionPane.showMessageDialog(this, "Você perdeu!");
            resetGame();
        }

        repaint();
    }

    private void resetBall() {
        ballX = getWidth() / 2 - BALL_SIZE / 2; 
        ballY = getHeight() / 2 - BALL_SIZE / 2; 
        Random random = new Random();
        ballDX = random.nextBoolean() ? -7 : 7; 
        ballDY = random.nextBoolean() ? -7 : 7; 
    }

    private void resetGame() {
        playerScore = 0;
        aiScore = 0;
        resetBall();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            upPressed = true;
        } else if (key == KeyEvent.VK_DOWN) {
            downPressed = true;
        } else if (key == KeyEvent.VK_W) {
            wPressed = true;
        } else if (key == KeyEvent.VK_S) {
            sPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            upPressed = false;
        } else if (key == KeyEvent.VK_DOWN) {
            downPressed = false;
        } else if (key == KeyEvent.VK_W) {
            wPressed = false;
        } else if (key == KeyEvent.VK_S) {
            sPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void playPaddleHitSound() {
        paddleHitSound.setFramePosition(0);
        paddleHitSound.start();
    }

    private void playScoreSound() {
        scoreSound.setFramePosition(0);
        scoreSound.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
