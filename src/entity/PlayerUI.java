package entity;

import Main.GamePanel;
import Main.KeyHandler;
import java.awt.*;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class PlayerUI extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    int id;
    String direction;
    public boolean logout;


    public PlayerUI(GamePanel gp, KeyHandler keyH, int id) {
        this.gp = gp;
        this.keyH = keyH;
        this.id = id;

        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenHeight / 2 - gp.tileSize / 2;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "null";
    }

    public void getPlayerImage() {

        try {
            normal1 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight1.png"));
            normal2 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight3.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/img/player/knight-right2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyH.upPressed == true) {
            direction = "up";
        } else if (keyH.downPressed == true) {
            direction = "down";
        } else if (keyH.leftPressed == true) {
            direction = "left";
        } else if (keyH.rightPressed == true) {
            direction = "right";
        } else {
            direction = "null";
        }

        // Check tile collision
        collisionOn = false;
        gp.cChecker.checkTile(this);

        // if collsion is false, player can move
        if (collisionOn == false) {
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }

        spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        // g2.setColor(Color.white);
        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                break;
            case "null":
                if (spriteNum == 1) {
                    image = normal1;
                }
                if (spriteNum == 2) {
                    image = normal2;
                }
                break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
