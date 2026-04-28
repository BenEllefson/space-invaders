import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameModel handles the game state and logic for Space Invaders.
 * This class contains no Swing imports and focuses purely on game mechanics.
 * 
 * Responsibilities:
 * - Track player position and state
 * - Track enemy positions and states
 * - Track projectiles (bullets)
 * - Handle collision detection
 * - Update game state each frame 
 * - Manage game score and lives
 */
public class GameModel {
    // Game dimensions
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // Player properties
    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 40;
    private static final int PLAYER_SPEED = 5;
    private static final int PLAYER_Y = GAME_HEIGHT - 60;
    
    private int playerX;
    
    // Alien properties
    private static final int ALIEN_WIDTH = 30;
    private static final int ALIEN_HEIGHT = 30;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLS = 11;
    private static final int ALIEN_SPEED = 2;
    
    private Alien[][] aliens;
    private int alienDirection; // 1 for right, -1 for left
    
    // Player bullet
    private Bullet playerBullet;
    private static final int PLAYER_BULLET_SPEED = 7;
    
    // Alien bullets
    private List<Bullet> alienBullets;
    private static final int ALIEN_BULLET_SPEED = 5;
    private int alienFireCounter;
    private static final int ALIEN_FIRE_INTERVAL = 60; // frames between shots
    
    // Shields
    private List<Shield> shields;
    private static final int NUM_SHIELDS = 4;
    private static final int SHIELD_WIDTH = 50;
    private static final int SHIELD_HEIGHT = 40;
    private static final int SHIELD_HEALTH = 3; // hits to destroy
    private static final int SHIELD_Y = GAME_HEIGHT - 150;
    
    // Game state
    private int score;
    private int lives;
    private Random random;
    
    /**
     * Inner class to represent an alien.
     */
    public static class Alien {
        public int x, y;
        public boolean alive;
        
        public Alien(int x, int y) {
            this.x = x;
            this.y = y;
            this.alive = true;
        }
    }
    
    /**
     * Inner class to represent a bullet.
     */
    public static class Bullet {
        public int x, y;
        public boolean active;
        
        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
            this.active = true;
        }
    }
    
    /**
     * Inner class to represent a shield.
     */
    public static class Shield {
        public int x, y;
        public int width, height;
        public int health;
        
        public Shield(int x, int y, int width, int height, int health) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.health = health;
        }
    }
    
    /**
     * Constructor initializes the game state.
     */
    public GameModel() {
        playerX = GAME_WIDTH / 2 - PLAYER_WIDTH / 2;
        alienDirection = 1;
        playerBullet = null;
        alienBullets = new ArrayList<>();
        alienFireCounter = 0;
        score = 0;
        lives = 3;
        random = new Random();
        shields = new ArrayList<>();
        
        initializeAliens();
        initializeShields();
    }
    
    /**
     * Initialize the alien formation in 5 rows of 11.
     */
    private void initializeAliens() {
        aliens = new Alien[ALIEN_ROWS][ALIEN_COLS];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                int x = 50 + col * (ALIEN_WIDTH + 10);
                int y = 30 + row * (ALIEN_HEIGHT + 10);
                aliens[row][col] = new Alien(x, y);
            }
        }
    }
    
    /**
     * Initialize shields positioned between the player and aliens.
     */
    private void initializeShields() {
        int spacing = GAME_WIDTH / (NUM_SHIELDS + 1);
        for (int i = 0; i < NUM_SHIELDS; i++) {
            int x = spacing * (i + 1) - SHIELD_WIDTH / 2;
            shields.add(new Shield(x, SHIELD_Y, SHIELD_WIDTH, SHIELD_HEIGHT, SHIELD_HEALTH));
        }
    }
    
    /**
     * Updates game logic for each frame.
     */
    public void update() {
        moveAliens();
        updatePlayerBullet();
        updateAlienBullets();
        fireAlienBullets();
        detectCollisions();
    }
    
    /**
     * Move the player left (if not at the left edge).
     */
    public void movePlayerLeft() {
        if (playerX > 0) {
            playerX -= PLAYER_SPEED;
        }
    }
    
    /**
     * Move the player right (if not at the right edge).
     */
    public void movePlayerRight() {
        if (playerX < GAME_WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }
    }
    
    /**
     * Fire a player bullet if one isn't already in flight.
     */
    public void firePlayerBullet() {
        if (playerBullet == null) {
            int bulletX = playerX + PLAYER_WIDTH / 2 - 2;
            int bulletY = PLAYER_Y - 10;
            playerBullet = new Bullet(bulletX, bulletY);
        }
    }
    
    /**
     * Update the player bullet position and check if it's out of bounds.
     */
    private void updatePlayerBullet() {
        if (playerBullet != null && playerBullet.active) {
            playerBullet.y -= PLAYER_BULLET_SPEED;
            if (playerBullet.y < 0) {
                playerBullet = null;
            }
        }
    }
    
    /**
     * Update all alien bullets and remove those out of bounds.
     */
    private void updateAlienBullets() {
        for (Bullet bullet : alienBullets) {
            if (bullet.active) {
                bullet.y += ALIEN_BULLET_SPEED;
            }
        }
        alienBullets.removeIf(bullet -> bullet.y > GAME_HEIGHT);
    }
    
    /**
     * Move the alien formation right until edge, then down and reverse direction.
     */
    private void moveAliens() {
        boolean atEdge = false;
        
        if (alienDirection == 1) {
            // Check if any alien would go off the right edge
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    if (aliens[row][col].alive && aliens[row][col].x + ALIEN_WIDTH >= GAME_WIDTH) {
                        atEdge = true;
                        break;
                    }
                }
                if (atEdge) break;
            }
        } else {
            // Check if any alien would go off the left edge
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    if (aliens[row][col].alive && aliens[row][col].x <= 0) {
                        atEdge = true;
                        break;
                    }
                }
                if (atEdge) break;
            }
        }
        
        if (atEdge) {
            // Move down and reverse direction
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    aliens[row][col].y += ALIEN_HEIGHT;
                }
            }
            alienDirection *= -1;
        } else {
            // Move horizontally
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    aliens[row][col].x += ALIEN_SPEED * alienDirection;
                }
            }
        }
    }
    
    /**
     * Fire alien bullets at random intervals.
     */
    private void fireAlienBullets() {
        alienFireCounter++;
        
        if (alienFireCounter >= ALIEN_FIRE_INTERVAL) {
            alienFireCounter = 0;
            
            // Pick a random alive alien to fire
            List<Alien> aliveAliens = new ArrayList<>();
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    if (aliens[row][col].alive) {
                        aliveAliens.add(aliens[row][col]);
                    }
                }
            }
            
            if (!aliveAliens.isEmpty()) {
                Alien shooter = aliveAliens.get(random.nextInt(aliveAliens.size()));
                int bulletX = shooter.x + ALIEN_WIDTH / 2 - 2;
                int bulletY = shooter.y + ALIEN_HEIGHT;
                alienBullets.add(new Bullet(bulletX, bulletY));
            }
        }
    }
    
    /**
     * Detect collisions between bullets and aliens, shields, or the player.
     */
    private void detectCollisions() {
        // Check player bullet against aliens
        if (playerBullet != null && playerBullet.active) {
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    Alien alien = aliens[row][col];
                    if (alien.alive && checkCollision(playerBullet, alien)) {
                        alien.alive = false;
                        playerBullet = null;
                        score += 10;
                        break;
                    }
                }
                if (playerBullet == null) break;
            }
        }
        
        // Check player bullet against shields
        if (playerBullet != null && playerBullet.active) {
            for (Shield shield : shields) {
                if (checkCollisionWithShield(playerBullet, shield)) {
                    playerBullet = null;
                    shield.health--;
                    break;
                }
            }
        }
        
        // Check alien bullets against shields
        for (Bullet bullet : alienBullets) {
            if (bullet.active) {
                for (Shield shield : shields) {
                    if (checkCollisionWithShield(bullet, shield)) {
                        bullet.active = false;
                        shield.health--;
                        break;
                    }
                }
            }
        }
        
        // Check alien bullets against player
        for (Bullet bullet : alienBullets) {
            if (bullet.active && checkCollisionWithPlayer(bullet)) {
                bullet.active = false;
                lives--;
                break;
            }
        }
        
        // Remove shields with no health
        shields.removeIf(shield -> shield.health <= 0);
    }
    
    /**
     * Check if a bullet collides with an alien.
     */
    private boolean checkCollision(Bullet bullet, Alien alien) {
        return bullet.x < alien.x + ALIEN_WIDTH &&
               bullet.x + 4 > alien.x &&
               bullet.y < alien.y + ALIEN_HEIGHT &&
               bullet.y + 10 > alien.y;
    }
    
    /**
     * Check if a bullet collides with the player.
     */
    private boolean checkCollisionWithPlayer(Bullet bullet) {
        return bullet.x < playerX + PLAYER_WIDTH &&
               bullet.x + 4 > playerX &&
               bullet.y < PLAYER_Y + PLAYER_HEIGHT &&
               bullet.y + 10 > PLAYER_Y;
    }
    
    /**
     * Check if a bullet collides with a shield.
     */
    private boolean checkCollisionWithShield(Bullet bullet, Shield shield) {
        return bullet.x < shield.x + shield.width &&
               bullet.x + 4 > shield.x &&
               bullet.y < shield.y + shield.height &&
               bullet.y + 10 > shield.y;
    }
    
    // ==================== Getter Methods ====================
    
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return PLAYER_Y; }
    public int getPlayerWidth() { return PLAYER_WIDTH; }
    public int getPlayerHeight() { return PLAYER_HEIGHT; }
    
    public Alien[][] getAliens() { return aliens; }
    public int getAlienWidth() { return ALIEN_WIDTH; }
    public int getAlienHeight() { return ALIEN_HEIGHT; }
    
    public Bullet getPlayerBullet() { return playerBullet; }
    public List<Bullet> getAlienBullets() { return alienBullets; }
    
    public List<Shield> getShields() { return shields; }
    public int getShieldWidth() { return SHIELD_WIDTH; }
    public int getShieldHeight() { return SHIELD_HEIGHT; }
    
    public int getScore() { return score; }
    public int getLives() { return lives; }
    
    public int getGameWidth() { return GAME_WIDTH; }
    public int getGameHeight() { return GAME_HEIGHT; }
}
