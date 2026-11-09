package com.cursedronin.entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {
    public int x, y;
    public int speed = 1;
    public int tileSize;
    
    // Health System
    public int maxHealth = 100;
    public int currentHealth = 100;
    
    // Combat / Proximity States
    public int attackRange = 75; 
    public boolean isAttacking = false;
    public boolean isMoving = false;
    public boolean facingLeft = false;

    // Asset Arrays
    public BufferedImage idleImg;
    public BufferedImage[] walkFrames = new BufferedImage[6];
    public BufferedImage[] runFrames = new BufferedImage[3];
    public BufferedImage[] attackFrames = new BufferedImage[3];

    // Animation Counters
    private int spriteCounter = 0;
    private int spriteNum = 0;

    public Enemy(int startX, int startY, int tileSize) {
        this.x = startX;
        this.y = startY;
        this.tileSize = tileSize;
    }

    public void update(int playerX, int playerY) {
        double dx = playerX - this.x;
        double dy = playerY - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Update facing direction based on Kurai's relative position
        if (dx < 0) {
            facingLeft = true;
        } else if (dx > 0) {
            facingLeft = false;
        }

        if (distance <= attackRange) {
            isAttacking = true;
            isMoving = false;
        } else {
            isAttacking = false;
            isMoving = true;

            if (dx > 0) x += speed;
            if (dx < 0) x -= speed;
            if (dy > 0) y += speed;
            if (dy < 0) y -= speed;
        }

        // Cycle animation frames
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum++;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = idleImg;

        if (isAttacking) {
            if (attackFrames[0] != null) {
                imageToDraw = attackFrames[spriteNum % attackFrames.length];
            }
        } else if (isMoving) {
            if (walkFrames[0] != null) {
                imageToDraw = walkFrames[spriteNum % walkFrames.length];
            }
        }

        int drawWidth = tileSize * 2;
        int drawHeight = tileSize * 2;

        if (imageToDraw != null) {
            if (facingLeft) {
                g2.drawImage(imageToDraw, 
                             x + drawWidth, y, 
                             x, y + drawHeight, 
                             0, 0, 
                             imageToDraw.getWidth(), 
                             imageToDraw.getHeight(), 
                             null);
            } else {
                g2.drawImage(imageToDraw, x, y, drawWidth, drawHeight, null);
            }
        }

        // Draw Health Bar Above Enemy
        drawHealthBar(g2, x + 10, y - 12, drawWidth - 20);
    }

    private void drawHealthBar(Graphics2D g2, int barX, int barY, int barWidth) {
        int barHeight = 6;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        
        g2.setColor(Color.RED);
        int healthWidth = (int) ((double) currentHealth / maxHealth * barWidth);
        g2.fillRect(barX, barY, Math.max(0, healthWidth), barHeight);
        
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
}