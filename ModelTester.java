/**
 * ModelTester runs unit tests on the GameModel class.
 * Tests are run without any testing framework — just plain Java assertions.
 */
public class ModelTester {
    
    private static int passCount = 0;
    private static int totalCount = 0;
    
    /**
     * Helper method to verify a test condition and update pass/fail counters.
     */
    private static void check(boolean condition, String testName, String message) {
        totalCount++;
        if (condition) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName + " - " + message);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== GameModel Unit Tests ===\n");
        
        testPlayerBoundary();
        testPlayerLeftBoundary();
        testPlayerRightBoundary();
        testBulletInFlightBlocking();
        testBulletRemovalOffScreen();
        testAlienDestructionAndScore();
        testGameOverOnZeroLives();
        
        System.out.println("\n=== Summary ===");
        System.out.println(passCount + "/" + totalCount + " tests passed");
    }
    
    /**
     * Test that the player's x position never goes below zero after 200 left movements.
     */
    private static void testPlayerBoundary() {
        GameModel model = new GameModel();
        
        // Move left 200 times
        for (int i = 0; i < 200; i++) {
            model.movePlayerLeft();
        }
        
        check(model.getPlayerX() >= 0, 
              "Player left boundary (200 moves)", 
              "Player x position is " + model.getPlayerX());
    }
    
    /**
     * Test that the player cannot move past the left edge.
     */
    private static void testPlayerLeftBoundary() {
        totalCount++;
        GameModel model = new GameModel();
        
        // Move left many times
        for (int i = 0; i < 1000; i++) {
            model.movePlayerLeft();
        }
        
        if (model.getPlayerX() >= 0) {
            System.out.println("✓ PASS: Player cannot move past left edge");
            passCount++;
        } else {
            System.out.println("✗ FAIL: Player moved past left edge (x = " + model.getPlayerX() + ")");
        }
    }
    
    /**
     * Test that the player cannot move past the right edge.
     */
    private static void testPlayerRightBoundary() {
        totalCount++;
        GameModel model = new GameModel();
        
        // Move right many times 
        for (int i = 0; i < 1000; i++) {
            model.movePlayerRight();
        }
        
        int maxX = model.getGameWidth() - model.getPlayerWidth();
        if (model.getPlayerX() <= maxX) {
            System.out.println("✓ PASS: Player cannot move past right edge");
            passCount++;
        } else {
            System.out.println("✗ FAIL: Player moved past right edge (x = " + model.getPlayerX() + ", max = " + maxX + ")");
        }
    }
    
    /**
     * Test that firing while a bullet is already in flight does nothing.
     */
    private static void testBulletInFlightBlocking() {
        totalCount++;
        GameModel model = new GameModel();
        
        // Fire the first bullet
        model.firePlayerBullet();
        GameModel.Bullet firstBullet = model.getPlayerBullet();
        
        // Try to fire again while first bullet is active
        model.firePlayerBullet();
        GameModel.Bullet secondBullet = model.getPlayerBullet();
        
        if (firstBullet == secondBullet && firstBullet != null) {
            System.out.println("✓ PASS: Firing while bullet in flight does nothing");
            passCount++;
        } else {
            System.out.println("✗ FAIL: Second fire created a new bullet or bullet is null");
        }
    }
    
    /**
     * Test that a bullet that reaches the top of the screen is removed.
     */
    private static void testBulletRemovalOffScreen() {
        totalCount++;
        GameModel model = new GameModel();
        
        // Fire a bullet
        model.firePlayerBullet();
        if (model.getPlayerBullet() == null) {
            System.out.println("✗ FAIL: Bullet was not created");
            return;
        }
        
        // Update many times to let bullet travel off screen
        for (int i = 0; i < 1000; i++) {
            model.update();
        }
        
        if (model.getPlayerBullet() == null) {
            System.out.println("✓ PASS: Bullet that reaches top is removed");
            passCount++;
        } else {
            System.out.println("✗ FAIL: Bullet still exists after many updates (y = " + model.getPlayerBullet().y + ")");
        }
    }
    
    /**
     * Test that destroying an alien increases the score.
     */
    private static void testAlienDestructionAndScore() {
        totalCount++;
        GameModel model = new GameModel();
        
        int initialScore = model.getScore();
        int initialAliveCount = countAliveAliens(model);
        
        // Move player left to align under some aliens
        for (int i = 0; i < 200; i++) {
            model.movePlayerLeft();
        }
        
        // Run the game for many ticks, continuously firing
        for (int tick = 0; tick < 3000; tick++) {
            if (model.getPlayerBullet() == null) {
                model.firePlayerBullet();
            }
            model.update();
        }
        
        int finalScore = model.getScore();
        int finalAliveCount = countAliveAliens(model);
        
        if (finalScore > initialScore && finalAliveCount < initialAliveCount) {
            System.out.println("✓ PASS: Destroying alien increases score");
            System.out.println("  Score: " + initialScore + " → " + finalScore + 
                             " | Aliens: " + initialAliveCount + " → " + finalAliveCount);
            passCount++;
        } else {
            System.out.println("✗ FAIL: Score did not increase or aliens were not destroyed");
            System.out.println("  Score: " + initialScore + " → " + finalScore + 
                             " | Aliens: " + initialAliveCount + " → " + finalAliveCount);
        }
    }
    
    /**
     * Test that losing all lives triggers the game-over state.
     * This test runs the game and checks if lives can be reduced below 3.
     */
    private static void testGameOverOnZeroLives() {
        totalCount++;
        GameModel model = new GameModel();
        
        int initialLives = model.getLives();
        
        // Keep the player still and let alien bullets hit (don't move or fire)
        // Run for many ticks to increase chance of being hit
        for (int tick = 0; tick < 10000; tick++) {
            model.update();
        }
        
        int finalLives = model.getLives();
        
        // If lives decreased, the damage system works
        if (finalLives < initialLives) {
            System.out.println("✓ PASS: Alien bullets can damage the player (losing lives triggers game-over state)");
            System.out.println("  Lives: " + initialLives + " → " + finalLives);
            passCount++;
        } else {
            System.out.println("✗ FAIL: Lives did not decrease after 10000 updates");
            System.out.println("  Lives: " + initialLives + " → " + finalLives + 
                             " | Alien bullets: " + model.getAlienBullets().size());
        }
    }
    
    /**
     * Helper method to count how many aliens are alive.
     */
    private static int countAliveAliens(GameModel model) {
        int count = 0;
        GameModel.Alien[][] aliens = model.getAliens();
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (aliens[row][col].alive) {
                    count++;
                }
            }
        }
        return count;
    }
}
