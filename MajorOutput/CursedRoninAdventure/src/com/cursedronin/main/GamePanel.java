package com.cursedronin.main;

import com.cursedronin.entity.Enemy;
import com.cursedronin.inputs.KeyHandler;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings({ "unused", "serial" })


public class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16; 
    final int scale = 3; 
    public final int tileSize = originalTileSize * scale; 
    
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; 
    final int screenHeight = tileSize * maxScreenRow; 

    int FPS = 60;
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    // Player Stats & Assets
    int playerX = 150;
    int playerY = 250;
    int playerSpeed = 4;
    int playerMaxHp = 100;
    int playerCurrentHp = 100;

    BufferedImage[] runFrames = new BufferedImage[4];
    BufferedImage idleImage;
    int spriteCounter = 0;
    int spriteNum = 0;
    boolean isMoving = false;
    boolean facingLeft = false;
    
 // Define world/room collision borders (adjust numbers to match your room walls)
    int worldLeftLimit = tileSize * 2;
    int worldRightLimit = tileSize * 14;
    int worldTopLimit = tileSize * 2;
    int worldBottomLimit = tileSize * 10;

    // Enemy Instance
    Enemy redRonin;

    // Background Asset
    BufferedImage backgroundImg;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        loadGameAssets();
    }

    public void loadGameAssets() {
        try {
            // Load background image
            backgroundImg = ImageIO.read(getClass().getResourceAsStream("/maps/room_background.jfif"));
            
            // Load Kurai frames from the kurai subfolder
            runFrames[0] = ImageIO.read(getClass().getResourceAsStream("/images/kurai/kurai1.png"));
            runFrames[1] = ImageIO.read(getClass().getResourceAsStream("/images/kurai/kurai2.png"));
            runFrames[2] = ImageIO.read(getClass().getResourceAsStream("/images/kurai/kurai3.png"));
            runFrames[3] = ImageIO.read(getClass().getResourceAsStream("/images/kurai/kurai4.png"));
            idleImage = runFrames[0];

            // Initialize and load Red Ronin frames from the roninred subfolder
            redRonin = new Enemy(450, 250, tileSize);
            redRonin.idleImg = ImageIO.read(getClass().getResourceAsStream("/images/roninred/ronin_red_idle.png"));
            
            for (int i = 0; i < 3; i++) {
                redRonin.runFrames[i] = ImageIO.read(getClass().getResourceAsStream("/images/roninred/ronin_red_run" + (i + 1) + ".png"));
                redRonin.attackFrames[i] = ImageIO.read(getClass().getResourceAsStream("/images/roninred/ronin_red_attack" + (i + 1) + ".png"));
            }
            for (int i = 0; i < 6; i++) {
                redRonin.walkFrames[i] = ImageIO.read(getClass().getResourceAsStream("/images/roninred/ronin_red_walk" + (i + 1) + ".png"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        int dx = 0, dy = 0;
        isMoving = false;
        
        if (keyH.up) { dy -= playerSpeed; isMoving = true; }
        if (keyH.down) { dy += playerSpeed; isMoving = true; }
        if (keyH.left) { dx -= playerSpeed; isMoving = true; facingLeft = true; }
        if (keyH.right) { dx += playerSpeed; isMoving = true; facingLeft = false; }

        // Normalize diagonal speed
        double nextX = playerX;
        double nextY = playerY;

        if (dx != 0 && dy != 0) {
            nextX += (int)(dx * 0.7071);
            nextY += (int)(dy * 0.7071);
        } else {
            nextX += dx;
            nextY += dy;
        }

        // --- BOUNDARY COLLISION CHECK ---
        // Check if projected position is within room boundaries (accounting for sprite size)
        int spriteWidth = tileSize * 2;
        int spriteHeight = tileSize * 2;

        if (nextX >= worldLeftLimit && nextX + spriteWidth <= worldRightLimit) {
            playerX = (int) nextX;
        }
        if (nextY >= worldTopLimit && nextY + spriteHeight <= worldBottomLimit) {
            playerY = (int) nextY;
        }

        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum + 1) % 4;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; 
        }

        // Update Enemy AI position
        if (redRonin != null) {
            redRonin.update(playerX, playerY);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. Render Room Background
        if (backgroundImg != null) {
            g2.drawImage(backgroundImg, 0, 0, screenWidth, screenHeight, null);
        }

        // 2. Render Enemy (Red Ronin) with its Health Bar
        if (redRonin != null) {
            redRonin.draw(g2);
        }

        // 3. Render Player (Kurai)
        BufferedImage currentImage = isMoving ? runFrames[spriteNum] : idleImage;
        int drawWidth = tileSize * 2;
        int drawHeight = tileSize * 2;

        if (currentImage != null) {
            if (facingLeft) {
                g2.drawImage(currentImage, playerX + drawWidth, playerY, playerX, playerY + drawHeight, 0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
            } else {
                g2.drawImage(currentImage, playerX, playerY, drawWidth, drawHeight, null);
            }
        }

        // Render Kurai's Overhead Health Bar
        drawPlayerHealthBar(g2, playerX + 10, playerY - 12, drawWidth - 20);

        // 4. Render UI / HUD Overlay
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("MAP: Cursed Village Arena", 20, 30);
        
        g2.dispose();
    }

    private void drawPlayerHealthBar(Graphics2D g2, int x, int y, int width) {
        int height = 6;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);
        
        g2.setColor(Color.GREEN);
        int healthWidth = (int) ((double) playerCurrentHp / playerMaxHp * width);
        g2.fillRect(x, y, Math.max(0, healthWidth), height);
        
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);
    }
}