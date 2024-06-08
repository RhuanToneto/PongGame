import javax.swing.JFrame;

public class PongGame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public PongGame() {
        setTitle("Pong Game");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setLocationRelativeTo(null); 
    }

    public static void main(String[] args) {
        PongGame game = new PongGame();
        game.setVisible(true);
    }
}
