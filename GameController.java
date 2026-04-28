import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * GameController is the main orchestrator for the Space Invaders game.
 * It wires together the GameModel and GameView, and contains the main entry point.
 * 
 * Responsibilities:
 * - Create and initialize the game window (JFrame)
 * - Create the GameModel and GameView
 * - Set up the game loop 
 * - Handle user input and dispatch to the model
 * - Manage the overall game flow
 */
public class GameController {
    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer gameLoop;
    
    // Key tracking
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    /**
     * Constructor sets up the controller and initializes the game.
     */
    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        
        // Set up frame
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(view);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        // Set up keyboard input
        setupKeyboardInput();
        
        frame.setVisible(true);
        
        // Start the game loop
        startGameLoop();
    }
    
    /**
     * Set up keyboard input handling.
     */
    private void setupKeyboardInput() {
        view.setFocusable(true);
        view.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e);
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                // Not used
            }
        });
    }
    
    /**
     * Handle key press events.
     */
    private void handleKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = true;
        } else if (key == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        } else if (key == KeyEvent.VK_SPACE) {
            model.firePlayerBullet();
        }
    }
    
    /**
     * Handle key release events.
     */
    private void handleKeyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = false;
        } else if (key == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
    
    /**
     * Start the game loop with a Swing Timer at 60 FPS (16.67ms per frame).
     */
    private void startGameLoop() {
        gameLoop = new Timer(16, e -> {
            // Handle continuous key presses
            if (leftPressed) {
                model.movePlayerLeft();
            }
            if (rightPressed) {
                model.movePlayerRight();
            }
            
            // Update game state
            model.update();
            
            // Redraw the view
            view.repaint();
            
            // Check if game is over and stop the loop
            if (isGameOver()) {
                gameLoop.stop();
            }
        });
        gameLoop.start();
    }
    
    /**
     * Check if the game has ended (no lives left or all aliens defeated).
     */
    private boolean isGameOver() {
        if (model.getLives() <= 0) {
            return true;
        }
        
        // Check if all aliens are dead
        GameModel.Alien[][] aliens = model.getAliens();
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (aliens[row][col].alive) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Main entry point for the Space Invaders game.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}
