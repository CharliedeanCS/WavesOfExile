package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {

    private Sprite enemySprite;
    private Texture[] enemyTextures = new Texture[8];
    int enemyX;
    int enemyY;
    int zombieHealth;
    private boolean alive;
    private Rectangle RectEnemy;
    boolean EnemyInRoom;
    int NoRoom = 0;
    int time = 0;

    /**
     * Creates a new enemy
     * @param randomValueX X coordinate of the enemy's spawn location
     * @param randomValueY X coordinate of the enemy's spawn location
     * @param zombieHealth The health value of the zombie at spawn time
     */
    public Enemy(int randomValueX, int randomValueY, int zombieHealth) {

        //set textures for enemy - textures named from front clockwise (0 - 7)
        for (int i = 0; i < 8; i++)
            enemyTextures[i] = new Texture(Gdx.files.internal("enemyAssets/enemy" + i+ ".png"));


        this.enemyX = randomValueX;
        this.enemyY = randomValueY;

        //set health equal to current level
        this.zombieHealth = zombieHealth;
        this.alive = true;

        //initialise enemy as sprite
        enemySprite = new Sprite(enemyTextures[0],64,64,64,64);
        enemySprite.setSize(64, 64);
        enemySprite.setOriginCenter();

        RectEnemy = new Rectangle(enemyX,enemyY,enemySprite.getWidth(),enemySprite.getHeight());
    }


    /**
     * Pathfinding for enemies - calls the appropriate method depending on whether player and enemy are in the same room
     * @param i Player's current room
     * @param RectRoom Bounding rectangle for player's room
     * @param playerX Player's x coordinate
     * @param playerY Player's y coordinate
     * @param RectPlayer Bounding rectangle for player
     */
    public void Pathfinding(int i, Rectangle[] RectRoom, int playerX,int playerY, Rectangle RectPlayer) {
        time = 0;
        for (int l = 0; l < 5; l++) {
            time = time+1;
            RectEnemy = new Rectangle(enemyX,enemyY,enemySprite.getWidth(),enemySprite.getHeight());
            EnemyInRoom = RectEnemy.overlaps(RectRoom[l]);
            if (EnemyInRoom) {
                NoRoom = 0 ;
                if (i == l) {
                    this.enemyAI(playerX, playerY);
                }
                if (i != l) {
                    AimForCenter(l,playerY,i,RectRoom,playerX);
                }
            }
            if (!EnemyInRoom){
                NoRoom = NoRoom + 1;
                if (NoRoom > 10) {
                    if (i == 5 && time != 1 && time != 2) {
                        this.enemyAI(playerX, playerY);
                    } else if (time != 3 && time != 4) {
                        this.AimForDoor(i, playerY, RectRoom, playerX);
                    }
                }
            }
        }
    }

    /**
     * Called when the enemy needs to leave a room to go towards the player
     * @param i Player's current room
     * @param playerY Player's y coordinate
     * @param RectRoom Bounding rectangle for player's room
     * @param playerX Player's x coordinate
     */
    public void AimForDoor(int i,int playerY,Rectangle[] RectRoom, int playerX) {
        int[][] DoorLocationsInner = {{520, 850}, {700, 750}, {700, 260}, {200, 200}, {550, 620},{700, 500}};
        int[] tempInner = new int[2];
        if (i == 0) {
            tempInner[0] = DoorLocationsInner[0][0];
            tempInner[1] = DoorLocationsInner[0][1];
        }
        if (i == 1) {
            tempInner[0] = DoorLocationsInner[1][0];
            tempInner[1] = DoorLocationsInner[1][1];
        }
        if (i == 2 && playerY < 382) {
            tempInner[0] = DoorLocationsInner[2][0];
            tempInner[1] = DoorLocationsInner[2][1];
        }
        if (i == 2 && playerY > 382) {
            tempInner[0] = DoorLocationsInner[5][0];
            tempInner[1] = DoorLocationsInner[5][1];
        }
        if (i == 3) {
            tempInner[0] = DoorLocationsInner[3][0];
            tempInner[1] = DoorLocationsInner[3][1];
        }
        if (i == 4) {
            tempInner[0] = DoorLocationsInner[4][0];
            tempInner[1] = DoorLocationsInner[4][1];
        }

        int enemyTextureToDisplay = calculateEnemyTexture(playerX, playerY);
        this.enemySprite.setTexture(enemyTextures[enemyTextureToDisplay]);

        if (tempInner[1] > enemyY) {
            this.enemySprite.setTexture(enemyTextures[4]);
            enemyY++;
        }
        else if (tempInner[1] < enemyY) {
            this.enemySprite.setTexture(enemyTextures[0]);
            enemyY--;
        }
        else if (tempInner[0] < enemyX) {
            this.enemySprite.setTexture(enemyTextures[2]);
            enemyX--;
        }
         else if (tempInner[0] > enemyX) {
            this.enemySprite.setTexture(enemyTextures[6]);
            enemyX++;
        }
    }

    /**
     * Called when the enemy is in the central corridor and needs to move towards the player
     * @param l Enemy's current room
     * @param playerY Player's y coordinate
     * @param i Player's current room
     * @param RectRoom Bounding rectangle for player's current room
     * @param playerX Player's x coordinate
     */
    public void AimForCenter(int l,int playerY, int i, Rectangle[] RectRoom, int playerX) {
        int[][] DoorLocations = {{650, 850}, {620, 750}, {620, 250}, {650, 230}, {650, 650},{620,500}};
        int[] temp = new int[2];


        if (l == 0) {
            temp[0] = DoorLocations[0][0];
            temp[1] = DoorLocations[0][1];
        } else if (l == 1) {
            temp[0] = DoorLocations[1][0];
            temp[1] = DoorLocations[1][1];
        }
        else if (l == 2 && playerY > 382) {
            temp[0] = DoorLocations[5][0];
            temp[1] = DoorLocations[5][1];
        }
        else if (l == 2 && playerY < 382) {
            temp[0] = DoorLocations[2][0];
            temp[1] = DoorLocations[2][1];
        }
        else if (l == 3) {
            temp[0] = DoorLocations[3][0];
            temp[1] = DoorLocations[3][1];
        }
        else if (l == 4) {
            temp[0] = DoorLocations[4][0];
            temp[1] = DoorLocations[4][1];
        }

        int enemyTextureToDisplay = calculateEnemyTexture(playerX, playerY);
        this.enemySprite.setTexture(enemyTextures[enemyTextureToDisplay]);

        if (temp[1] > enemyY) {
            this.enemySprite.setTexture(enemyTextures[4]);
            enemyY++;
        }
        else if (temp[1] < enemyY) {
            this.enemySprite.setTexture(enemyTextures[0]);
            enemyY--;
        }
        else if (temp[0] > enemyX) {
            this.enemySprite.setTexture(enemyTextures[6]);
            enemyX++;
        }
        else if (temp[0] < enemyX) {
            this.enemySprite.setTexture(enemyTextures[2]);
            enemyX--;
        }
    }

    /**
     * Called when player and enemy are in the same room - simple movement algorithm straight towards the player
     * @param playerX X coordinate of player's current position
     * @param playerY Y coordinate of player's current position
     */
    public void enemyAI(int playerX, int playerY){


        int enemyTextureToDisplay = calculateEnemyTexture(playerX, playerY);
        this.enemySprite.setTexture(enemyTextures[enemyTextureToDisplay]);

        if (playerX > enemyX) {
            //this.enemySprite.setTexture(enemyTextures[1]);
            enemyX++;
        }
        else if (playerX < enemyX) {
            //this.enemySprite.setTexture(enemyTextures[3]);
            enemyX--;
        }

        if (playerY > enemyY) {
            this.enemySprite.setTexture(enemyTextures[4]);
            enemyY++;
        }
        else if (playerY < enemyY) {
            //this.enemySprite.setTexture(enemyTextures[0]);
            enemyY--;
        }

        this.enemySprite.setPosition(enemyX, enemyY);
    }


    /**
     * To calculate the appropriate texture of the enemy to display on screen depending on orientation
     * @param playerX x-coordinate of player sprite current location
     * @param playerY y-coordinate of player sprite current location
     * @return integer holding index of texture to display from enemy texture array
     */
    public int calculateEnemyTexture(int playerX, int playerY) {

        //define values (in degrees) at which the enemy texture should change to the next one
        //enumerated clockwise from 0 in increments of 45 degrees (as 360/8 = 45 and there are 8 enemy textures)
        double[] borderValues = {22.5, 67.5, 112.5, 157.5, 202.5, 247.5, 292.5, 337.5};

        //find the difference between the player and enemy coordinates
        int diffX = this.enemyX - playerX;
        int diffY = this.enemyY - playerY;

        //special cases - if the player and enemy X or player and enemy Y are exactly equal
        if (diffX == 0) {
            if (diffY > 0)  //enemy directly above player
                return 4;
            else            //enemy directly below player
                return 0;
        }
        else if (diffY == 0) {
            if (diffX > 0)  //enemy directly right of player
                return 2;
            else            //enemy directly left of player
                return 6;
        }
        
        //calculate angle between enemy and player
        //the angle returned is 0 when enemy on right of player, 90 when enemy above player,
        //-90 when enemy below player, and (-)180 when enemy to left of player
        float enemyToPlayerAngle = MathUtils.atan2(diffY, diffX);   //returns radians
        enemyToPlayerAngle = MathUtils.radDeg * enemyToPlayerAngle; //convert to degrees

        if (Math.abs(enemyToPlayerAngle) < 22.5) {
            return 2;
        } else if (enemyToPlayerAngle >= 22.5 && enemyToPlayerAngle < 67.5) {
            return 1;
        } else if (enemyToPlayerAngle >= 67.5 && enemyToPlayerAngle < 112.5) {
            return 0;
        } else if (enemyToPlayerAngle >= 112.5 && enemyToPlayerAngle < 157.5) {
            return 7;
        } else if (Math.abs(enemyToPlayerAngle) >= 157.5) {
            return 6;
        } else if (enemyToPlayerAngle <= -112.5 && enemyToPlayerAngle > -157.5) {
            return 5;
        } else if (enemyToPlayerAngle <= -67.5 && enemyToPlayerAngle > -112.5) {
            return 4;
        } else if (enemyToPlayerAngle <= -22.5 && enemyToPlayerAngle > -67.5) {
            return 3;
        }

        //catch all to prevent game breaking (this should never occur anyway)
        return 0;
    }


    /**
     * Fetch enemy sprite
     * @return The enemy sprite
     */
    public Sprite getEnemySprite() { return enemySprite; }

    /**
     * Fetch enemy textures
     * @return The enemy textures
     */
    public Texture[] getEnemyTextures() { return enemyTextures; }

    /**
     * Fetch enemy x coordinate
     * @return The enemy x coordinate
     */
    public int getEnemyX() {
        return enemyX;
    }

    /**
     * Fetch enemy y coordinate
     * @return The enemy y coordinate
     */
    public int getEnemyY() { return enemyY; }

    /**
     * Fetch enemy bounding rectangle
     * @return The enemy bounding rectangle
     */
    public Rectangle getRectEnemy(){return RectEnemy;}

    /**
     * Fetch enemy alive status
     * @return The enemy alive status
     */
    public boolean getEnemyAlive(){return alive;}

    /**
     * Set the x coordinate value of this enemy
     * @param enemyX The new value to change to
     */
    public void setEnemyX(int enemyX) {
        this.enemyX = enemyX;
    }

    /**
     * Set the y coordinate value of this enemy
     * @param enemyY The new value to change to
     */
    public void setEnemyY(int enemyY) {
        this.enemyY = enemyY;
    }

    /**
     * Set the enemy's health
     * @param Health New health value
     */
    public void setEnemyHealth(int Health) {
        this.zombieHealth = Health;
    }

    /**
     * Get enemy's current health
     * @return Enemy's current health
     */
    public int getEnemyHealth() {
        return zombieHealth;
    }

}