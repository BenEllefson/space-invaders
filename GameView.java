import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * GameView extends JPanel and handles rendering of the Space Invaders game.
 * This class is responsible for all visual display of game elements.
 * 
 * Responsibilities:
 * - Render the player ship
 * - Render aliens (enemies)
 * - Render projectiles
 * - Render score and game status
 * - Handle all drawing logic
 */
public class GameView extends JPanel {
    private GameModel model;
    
    /**
     * Constructor sets up the game view with a reference to the game model.
     * 
     * @param model the GameModel instance to render
     */
    public GameView(GameModel model) {
        this.model = model;
        setBackground(Color.BLACK);
    }
    
    /**
     * Paints the game components on the panel.
     * Called automatically when the panel needs to be redrawn.
     * 
     * @param g the Graphics object to draw with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw game elements
        drawPlayer(g);
        drawAliens(g);
        drawShields(g);
        drawPlayerBullet(g);
        drawAlienBullets(g);
        drawHUD(g);
        
        // Draw game-over message if game has ended
        if (isGameOver()) {
            drawGameOver(g);
        }
    }
    
    /**
     * Draw the player ship at the bottom of the screen.
     */
    private void drawPlayer(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), 
                   model.getPlayerWidth(), model.getPlayerHeight());
        
        // Draw a small triangle on top to indicate ship direction
        g.setColor(Color.LIGHT_GRAY);
        int[] xPoints = {model.getPlayerX() + model.getPlayerWidth() / 2,
                         model.getPlayerX() + 5,
                         model.getPlayerX() + model.getPlayerWidth() - 5};
        int[] yPoints = {model.getPlayerY(), 
                         model.getPlayerY() + 15,
                         model.getPlayerY() + 15};
        g.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draw the alien formation.
     */
    private void drawAliens(Graphics g) {
        g.setColor(Color.RED);
        GameModel.Alien[][] aliens = model.getAliens();
        
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                GameModel.Alien alien = aliens[row][col];
                if (alien.alive) {
                    g.fillRect(alien.x, alien.y, 
                              model.getAlienWidth(), model.getAlienHeight());
                    
                    // Draw simple eyes
                    g.setColor(Color.BLACK);
                    g.fillRect(alien.x + 5, alien.y + 5, 4, 4);
                    g.fillRect(alien.x + 18, alien.y + 5, 4, 4);
                    g.setColor(Color.RED);
                }
            }
        }
    }
    
    /**
     * Draw the shields with color based on health.
     * Health 3: Full green, Health 2: Yellow, Health 1: Dim red.
     */
    private void drawShields(Graphics g) {
        for (GameModel.Shield shield : model.getShields()) {
            // Choose color based on shield health
            if (shield.health >= 3) {
                g.setColor(Color.GREEN);
            } else if (shield.health == 2) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(new Color(200, 0, 0)); // Dim red
            }
            
            g.fillRect(shield.x, shield.y, shield.width, shield.height);
            
            // Draw a border to make shields more visible
            g.setColor(Color.WHITE);
            g.drawRect(shield.x, shield.y, shield.width, shield.height);
        }
    }
    
    /**
     * Draw the player's bullet if one is in flight.
     */
    private void drawPlayerBullet(Graphics g) {
        GameModel.Bullet bullet = model.getPlayerBullet();
        if (bullet != null && bullet.active) {
            g.setColor(Color.YELLOW);
            g.fillRect(bullet.x, bullet.y, 4, 10);
        }
    }
    
    /**
     * Draw all alien bullets currently on screen.
     */
    private void drawAlienBullets(Graphics g) {
        g.setColor(Color.MAGENTA);
        for (GameModel.Bullet bullet : model.getAlienBullets()) {
            if (bullet.active) {
                g.fillRect(bullet.x, bullet.y, 4, 10);
            }
        }
    }
    
    /**
     * Draw the heads-up display: score and lives remaining.
     */
    private void drawHUD(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Draw score on the left
        g.drawString("Score: " + model.getScore(), 10, 25);
        
        // Draw lives on the right
        String livesText = "Lives: " + model.getLives();
        int textWidth = g.getFontMetrics().stringWidth(livesText);
        g.drawString(livesText, getWidth() - textWidth - 10, 25);
    }
    
    /**
     * Draw a centered game-over message.
     */
    private void drawGameOver(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Game-over message
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        
        String message = model.getLives() <= 0 ? "GAME OVER" : "YOU WIN!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, 
                    (getWidth() - messageWidth) / 2,
                    getHeight() / 2 - 20);
        
        // Draw final score
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Final Score: " + model.getScore();
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText,
                    (getWidth() - scoreWidth) / 2,
                    getHeight() / 2 + 30);
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
}
