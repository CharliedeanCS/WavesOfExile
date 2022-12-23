package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import org.graalvm.compiler.loop.MathUtil;
import com.badlogic.gdx.utils.Timer;
import org.w3c.dom.css.Rect;

import javax.script.ScriptEngine;
import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameScreen implements Screen {

    final wavesOfExile game;
    private final boolean gameOnMute;
    private Music BackgroundMusic;

    //camera related variables
    OrthographicCamera camera;
    ExtendViewport mainViewport;
    float windowWidth;
    float windowHeight;
    int NoRoomPlayer = 0;
    int levelNoOnScreen = 0;
    static final double lerp = 0.9;  //multiplier for camera movement to follow player around map
    static final int SCREEN_EDGE_DISTANCE_X = 300;  //constant for border between player sprite and edge
    static final int SCREEN_EDGE_DISTANCE_Y = 250;  //of screen (how close the player gets to the edge of the
    //screen before the camera moves)
    static final int PLAYER_WORLD_EDGE_DISTANCE_X = 10;     //the closest the player sprite can get to the edge of the
    static final int PLAYER_WORLD_EDGE_DISTANCE_Y = 10;     //world map (ie. world not screen)

    //constants to store the width and height of the world will represent
    //the total size of the map, rather than what is visible (what is visible is defined by the viewport)
    static final int WORLD_WIDTH = 1024;
    static final int WORLD_HEIGHT = 1024;

    //timing related variables
    long gameStartTime, timeSinceGameStart, lastEnemySpawnTime;

    //player position related variables
    Vector3 playerWorldCoords;
    Vector3 playerScreenPos;
    Vector3 currentAimPos;      //current screen coordinates of the cursor

    //sprites and textures
    Texture backgroundMap;
    //multiplier by which to change the scaling of the sprites, so they are displayed correctly
    static final double PLAYER_TEXTURE_SCALING = 4.5;
    static final double ENEMY_TEXTURE_SCALING = 4.5;
    private Texture[] playerTextures = new Texture[4];
    private Texture[] HealthBar = new Texture[4];
    private Sprite player;
    private Sprite[] Health = new Sprite[4];
    //array list to hold all enemies
    ArrayList<Enemy> enemies;
    //bullet sprite
    private Sprite bulletLine;
    private int bulletLineShow;

    //collision related variables
    private Texture Mask = new Texture("Collis.png");
    private Texture Bullet = new Texture("Bullet.png");
    private Texture BulletDamage = new Texture("BulletDamage.png");

    private Rectangle RectBullet;
    private Sprite[] Collision = new Sprite[13];
    private Sprite[] Room = new Sprite[6];
    private Rectangle RectPlayer;
    private Rectangle RectColl;
    //boolean array of 4 variables to block movement in a given direction if a collision occurs
    private Boolean[] playerHasCollided;
    private Rectangle[] RectRoom = new Rectangle[6];

    //location related variables/constants
    int playerX = 400;
    int playerY = 400;
    static int MOVEMENT_SPEED = 2;    //constant for movement speed
    
    int HealthNumber = 0;
    int HealthLocationX = 500000;
    int HealthLocationY = 500000;
    int SpeedLocationX = 300000;
    int SpeedLocationY = 300000;
    int PointsLocationX = 300000;
    int PointsLocationY = 300000;
    int DamageLocationX = 300000;
    int DamageLocationY = 300000;
    
    //sounds and music
    Music backgroundMusic;

    Sound winSound, gameOverSound, // Game Sounds
        gunshotSound, playerDamageSound, // Player Sounds
        healthBoostSound, speedBoostSound, // Powerup Sounds
        grassWalkingSound, pavementWalkingSound, woodWalkingSound, // Walking Sounds
        zombieHitSound, zombieDyingSound; // Zombie Sounds


    //score and level progression related variables
    Score score = new Score(0,1);   //instance of the score class for progression and levels
    int Level = 1;
    int zombiesKilled = 0;
    int globalScore = 0;

    //power up related variables
    int Timer;
    int RandomPowerup = 0;
    //health regeneration
    private Texture HealthMask;
    private Rectangle HealthRect;
    private Sprite HealthPickup;
    boolean pickUpHealth = false;
    //speed boost
    private Texture SpeedMask;
    private Rectangle SpeedRect;
    private Sprite SpeedPickup;
    boolean pickUpSpeed = false;
    //2x points
    private Texture PointsMask;
    private Rectangle PointsRect;
    private Sprite PointsPickup;
    boolean pickUpPoints = false;
    int pointsModifier = 1;     //increases the rate at which points are earned (set to 2 if 2x is active) 2x damage
    private Texture DamageMask;
    private Rectangle DamageRect;
    private Sprite DamagePickup;
    boolean pickUpDamage = false;
    int dmgModifier = 1;        //increases the rate at which damage is dealt (set to 2 if 2x is active)
    int dmgModifierTimer = 0;
    int randomDespawn = 0;
    int canShoot = 0;

    /**
     * Constructor method for the main game class - initialises all variables and sets up parameters for game
     * @param game Instance of the libgdx 'game' application listener that allows the whole game to run, and switching between screens 
     * @param gameOnMute True if player has turned game sound off in settings before starting game
     * @param backgroundMusic Passed in from the menu screen as it starts playing there; also allows it to be stopped within the game
     * @param Level Level number that the current game is on (between 1 and 3)
     * @param zombiesKilled Number of zombies killed. 0 if player is starting level 1, otherwise passed from previous level
     * @param globalScore Player's total score on this playthrough. 0 if player is starting level 1, otherwise passed from previous level
     */
    public GameScreen (final wavesOfExile game, boolean gameOnMute, Music backgroundMusic, int Level, int zombiesKilled, int globalScore) {

        this.game = game;
        this.gameOnMute = gameOnMute;
        this.backgroundMusic = backgroundMusic;
        this.Level = Level;
        score.setLevel(Level);
        this.zombiesKilled = zombiesKilled;
        score.setTotalZombiesKilled(zombiesKilled);
        this.globalScore = globalScore;
        score.setGlobalScore(globalScore);

        gameStartTime = TimeUtils.millis();

        //initialising all textures for the player and the health bar
        playerTextures[0] = new Texture(Gdx.files.internal("playerAssets/PlayerFront.png"));
        playerTextures[1] = new Texture(Gdx.files.internal("playerAssets/PlayerRight.png"));
        playerTextures[2] = new Texture(Gdx.files.internal("playerAssets/PlayerBack.png"));
        playerTextures[3] = new Texture(Gdx.files.internal("playerAssets/PlayerLeft.png"));
        HealthBar[0] = new Texture(Gdx.files.internal("Healthfull.png"));
        HealthBar[1] = new Texture(Gdx.files.internal("Health2t3.png"));
        HealthBar[2] = new Texture(Gdx.files.internal("Health1t3.png"));
        HealthBar[3] = new Texture(Gdx.files.internal("Healthempty.png"));


        //initialising player and array list of enemy sprites
        player = new Sprite(playerTextures[0],64,64,64,64);
        enemies = new ArrayList<Enemy>();

        //initialise power up sprites and textures
        HealthMask = new Texture("powerups/rsz_health.png");
        HealthPickup = new Sprite(HealthMask,0,0,16,16);
        SpeedMask = new Texture("powerups/rsz_lightning.png");
        SpeedPickup = new Sprite(SpeedMask,0,0,16,16);
        PointsMask = new Texture("powerups/rsz_double.png");
        PointsPickup = new Sprite(PointsMask,0,0,16,16);
        DamageMask = new Texture("powerups/rsz_boost.png");
        DamagePickup = new Sprite(DamageMask,0,0,16,16);


        //retrieve dimensions of game window on screen (can change during game so maybe fetch every time as well?)
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(250, 250 * (windowHeight / windowWidth));
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);    //set camera to be centred on viewport
        camera.setToOrtho(false);

        //viewport will allow the window to be resized and scaled properly
        mainViewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);

        //set background texture
        backgroundMap = new Texture(Gdx.files.internal("BackgroundMap.png"));


        /*** SOUNDS ***/

        // Game Sounds
        winSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Game_sounds/Custom_win_sound.mp3"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Game_sounds/Game_Over_Sound.mp3"));

        // Player Sounds
        gunshotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Player sounds/Gunshot1.mp3"));
        playerDamageSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Player sounds/Player Damage.mp3"));

        // Powerup Sounds
        healthBoostSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Powerups sounds/Health heal Boost.mp3"));
        speedBoostSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Powerups sounds/Speed Boost.mp3"));

        // Walking Sounds
        grassWalkingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Walking sounds/Grass walking.mp3"));
        pavementWalkingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Walking sounds/Pavement walking.mp3"));
        woodWalkingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Walking sounds/Wood walking.mp3"));

        // Zombie Sounds
        zombieHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Zombie sounds/Zombie Hit.mp3"));
        zombieDyingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Zombie sounds/Zombie Dying.mp3"));



        //for storing the coordinates of the player character in the world (as opposed to on screen coordinates)
        playerWorldCoords = new Vector3();
        playerScreenPos = new Vector3();
        currentAimPos = new Vector3();

        //initialise collision sprites
        for (int i = 0; i < 13; i++) {
            Collision[i] = new Sprite(Mask, 0, 0, 16, 16);
        }


        //Collision rectangles for player and power ups
        RectPlayer = new Rectangle(player.getX(),player.getY(),player.getWidth(),player.getHeight());
        HealthRect = new Rectangle(HealthPickup.getX(),HealthPickup.getY(),HealthPickup.getWidth(),HealthPickup.getHeight());
        SpeedRect = new Rectangle(SpeedPickup.getX(),SpeedPickup.getY(),SpeedPickup.getWidth(),SpeedPickup.getHeight());
        PointsRect = new Rectangle(PointsPickup.getX(),PointsPickup.getY(),PointsPickup.getWidth(),PointsPickup.getHeight());
        DamageRect = new Rectangle(DamagePickup.getX(),DamagePickup.getY(),DamagePickup.getWidth(),DamagePickup.getHeight());

        for (int i = 0;i<6;i++) {
            Room[i] = new Sprite(Mask,0,0,16,16);
        }
        for (int i = 0;i<4;i++) {
            Health[i] = new Sprite(HealthBar[i],0,0,37,6);
        }
        for (int i = 0; i<6;i++) {
            RectRoom[i] = new Rectangle(Room[i].getX(),Room[i].getY(),Room[i].getWidth(),Room[i].getHeight());
        }

        playerHasCollided = new Boolean[4];
        for (int i = 0; i < playerHasCollided.length; i++) {
            playerHasCollided[i] = false;
        }
    }


    /**
     * Called every frame in game. Calls all other functions/methods including player and enemy movement/rendering,
     * collisions, power ups, enemy spawning, checking if game over
     * @param delta
     */
    @Override
    public void render(float delta) {

        //clear the screen and fill it with a solid colour (black)
        ScreenUtils.clear(0, 0, 1, 1);
        timeSinceGameStart = TimeUtils.millis();

        //draw all objects on screen
        game.batch.begin();
        game.batch.draw(backgroundMap, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        game.font.setColor(1, 1, 1, 1);

        if (levelNoOnScreen < 200){
            game.font.setColor(1, 1, 1, 1);
            game.font.getData().setScale(2.0f);
            if(Level == 3) {
                game.font.draw(game.batch, "LEVEL " + Level + ": HORDE MODE", playerX - 20, playerY - 100);
            }
            else {
                game.font.draw(game.batch, "LEVEL " + Level, playerX - 20, playerY - 100);
            }
        }
        else if (levelNoOnScreen > 200){
            game.font.setColor(1, 1, 1, 0);
        }
        levelNoOnScreen++;
        

        for (int i = 0; i < Collision.length; i++) {
            Collision[i].draw(game.batch);
        }
        Health[HealthNumber].draw(game.batch);

        //draw the player sprite (texture width/height scaled using function, so it is displayed correctly)
        game.batch.draw(player.getTexture(), playerX, playerY,
                scaleAndRoundSpriteTextures(player)[0], scaleAndRoundSpriteTextures(player)[1]);
        
        //draw each enemy on screen (texture width/height scaled using function, so it is displayed correctly)
        for (int i = 0; i < enemies.size(); i++) {
            Enemy curEnemy = enemies.get(i);
            Sprite curEnemySprite = curEnemy.getEnemySprite();
            game.batch.draw(curEnemySprite.getTexture(),
                    curEnemy.getEnemyX(), curEnemy.getEnemyY(),
                    scaleAndRoundSpriteTextures(curEnemySprite)[0], scaleAndRoundSpriteTextures(curEnemySprite)[1]);
        }

        game.font.setColor(1, 1, 1, 1);
        game.font.getData().setScale(0.9F);
        game.font.draw(game.batch,"Score: " +score.getLevelScore(), playerX+8, playerY+90);

        // Bullet draw
        if (bulletLineShow > 0) {
            bulletLineShow--;
            bulletLine.draw(game.batch);
        }
        if (canShoot > 0) {
            canShoot--;
        }
        if (dmgModifier == 2 && dmgModifierTimer > 0) {
            dmgModifierTimer--;

            if (dmgModifierTimer == 0) {
                dmgModifier = 1;
            }
        }

            //Pickup draw
        if (pickUpHealth){
            HealthPickup.draw(game.batch);
            randomDespawn++;
        }
        if (pickUpHealth && randomDespawn % 400 == 0){
            HealthPickup.setPosition(10000,10000);
        }
        if (pickUpSpeed){
            SpeedPickup.draw(game.batch);
            randomDespawn++;
        }
        if (pickUpSpeed && randomDespawn % 400 == 0){
            SpeedPickup.setPosition(10000,10000);
        }
        if (pickUpPoints){
            PointsPickup.draw(game.batch);
            randomDespawn++;
        }
        if (pickUpPoints && randomDespawn % 400 == 0){
            PointsPickup.setPosition(10000,10000);
        }
        if (pickUpDamage){
            DamagePickup.draw(game.batch);
            randomDespawn++;
        }
        if (pickUpDamage && randomDespawn % 400 == 0){
            DamagePickup.setPosition(10000,10000);
        }

        game.batch.end();

        Health[HealthNumber].setPosition(playerX+14,playerY-8);
        //handles all the processes for spawning and movement of enemies
        enemyProcesses();

        //check if any of the enemies have touched the player, and if so end the game
        checkGameOver();

        //Creates the Collision Rectangles - must check collisions before taking input/moving player
        //as the collision status needs to be reset afterwards (below)
        enemyCollisions();

        //handle all user input in here (also calls camera movement if necessary)
        takeInput();
        player.setPosition(playerX, playerY);


        if(RectPlayer.overlaps(HealthRect) && pickUpHealth){
            int randomHealth = MathUtils.random(100);
            if (HealthNumber > 0 && randomHealth == 99) {
                HealthNumber = HealthNumber - 1;
                HealthPickup.setPosition(1000000,10000);
                SpeedPickup.setPosition(1000000,10000);
                if (!gameOnMute)
                    healthBoostSound.play(); // Play health boost sound
            }
        }
        if(RectPlayer.overlaps(SpeedRect) && pickUpSpeed && !RectPlayer.overlaps(HealthRect)){
            if (!gameOnMute)
                speedBoostSound.play(); // Play speed boost sound
            int randomSpeed = MathUtils.random(100);
            if (MOVEMENT_SPEED == 2 && randomSpeed < 50){
                MOVEMENT_SPEED=5;
                SpeedPickup.setPosition(1000000,10000);
                SpeedRect = SpeedPickup.getBoundingRectangle();
            }
        }
        int randomSpeed = MathUtils.random(100);
        if (MOVEMENT_SPEED == 5 && randomSpeed == 99){
            MOVEMENT_SPEED=2;
            SpeedPickup.setPosition(1000000,10000);
        }

        if(RectPlayer.overlaps(PointsRect) && pickUpPoints){
            int randomPoints = MathUtils.random(100);
            if (pointsModifier == 1 && randomPoints == 99){
                pointsModifier = 2;
                PointsPickup.setPosition(1000000,10000);
                PointsPickup.setPosition(1000000,10000);
            }
        }
        int randomPoints = MathUtils.random(100);
        if (pointsModifier == 2 && randomPoints == 99){
            pointsModifier=1;
            PointsPickup.setPosition(1000000,10000);
        }

        if(RectPlayer.overlaps(DamageRect) && pickUpDamage) {

            if (dmgModifier == 1){
                dmgModifierTimer = 600;
                dmgModifier = 2;
                DamagePickup.setPosition(1000000,10000);
                DamagePickup.setPosition(1000000,10000);
            }
        }

        /*if (dmgModifier == 2 && dmgModifier){
            dmgModifier=1;
            DamagePickup.setPosition(1000000,10000);
        }*/

        for (int i = 0; i < playerHasCollided.length; i++) {
            playerHasCollided[i] = false;
        }

        //update camera position with basic interpolation to make movement smoother
        cameraMove();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }


    /**
     * Called when the player shoots - draws a path for the bullet and checks if it hits an enemy or a wall
     */
    private void shoot() throws InterruptedException {

        canShoot = 12;

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        // Angle (in radians), for direction to shoot in
        double shootDirection = Math.atan2(
                player.getY() + player.getHeight() / 2 - mousePos.y,
                player.getX() + player.getWidth() / 2 - mousePos.x
        ) + (Math.toRadians(180));


        // Create bullet rectangle for overlap() collisions
        RectBullet = new Rectangle(
                player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2,
                2,
                2
        );

        // Play gunshot sound effect
        if (!gameOnMute)
            gunshotSound.play();

        // Search for a collision
        int searchDistance = 0,
                searchMax = 2000,
                searchIncrement = 1;
        boolean searchRunning = true;

        // Run search loop
        while (searchDistance < searchMax && searchRunning) {

            // For each wall
            for (int j = 0; j < 11; j++) {
                // Found collision with wall
                if (RectBullet.overlaps(new Rectangle(Collision[j].getX(), Collision[j].getY(), Collision[j].getWidth(), Collision[j].getHeight()))) {


                    // End search
                    searchRunning = false;

                    break;
                }
            }

            // Found collision with enemy
            for (int j = 0; j < enemies.size(); j++) {
                Enemy shootEnemy = enemies.get(j);
                if (RectBullet.overlaps(shootEnemy.getRectEnemy())) {

                    int ZombieHealth = shootEnemy.getEnemyHealth();
                    if (ZombieHealth <= 1){
                        if (!gameOnMute)
                            zombieDyingSound.play(); // Play zombie death sound

                        score.setLevelScore(score.getLevelScore() + (25*pointsModifier));
                        score.setTotalZombiesKilled(score.getTotalZombiesKilled() + 1);
                        RandomPowerup = MathUtils.random(4);
                        if (RandomPowerup == 1){
                            HealthLocationX = shootEnemy.getEnemyX();
                            HealthLocationY = shootEnemy.getEnemyY();
                            SpawnHealthPowerup(HealthLocationX,HealthLocationY);
                        }
                        if (RandomPowerup ==2){
                            SpeedLocationX = shootEnemy.getEnemyX();
                            SpeedLocationY = shootEnemy.getEnemyY();
                            SpawnSpeedPowerup(SpeedLocationX,SpeedLocationY);
                        }
                        if (RandomPowerup ==3){
                            PointsLocationX = shootEnemy.getEnemyX();
                            PointsLocationY = shootEnemy.getEnemyY();
                            SpawnPointsPowerup(PointsLocationX,PointsLocationY);
                        }
                        if (RandomPowerup ==4){
                            DamageLocationX = shootEnemy.getEnemyX();
                            DamageLocationY = shootEnemy.getEnemyY();
                            SpawnDamagePowerup(DamageLocationX,DamageLocationY);
                        }

                        //CHECKING WHETHER WE CAN ADVANCE TO THE NEXT LEVEL
                        if (score.advanceLevel(score.getLevelScore()) == 2) {
                            score.setGlobalScore(score.getGlobalScore() + score.getLevelScore());
                            game.setScreen(new GameScreen(game, gameOnMute, backgroundMusic,Level+1, score.getTotalZombiesKilled(), score.getGlobalScore()));
                        }
                        if (score.advanceLevel(score.getLevelScore()) == 3) {
                            score.setGlobalScore(score.getGlobalScore() + score.getLevelScore());
                            game.setScreen(new GameScreen(game, gameOnMute, backgroundMusic, Level+1, score.getTotalZombiesKilled(), score.getGlobalScore()));
                        }

                        shootEnemy.setEnemyX(20000);
                        shootEnemy.setEnemyY(20000);
                    }

                    else if (ZombieHealth > 1){
                        if (!gameOnMute)
                            zombieHitSound.play(); // Play zombie hit sound

                        ZombieHealth = ZombieHealth - dmgModifier;
                        shootEnemy.setEnemyHealth(ZombieHealth);
                    }

                    // End search
                    searchRunning = false;

                    break;
                }
            }

            // No collision found, move in shoot direction by minimum collision width
            searchDistance += searchIncrement;

            // Move bullet to find next collisions
            RectBullet.setX((float)( RectBullet.getX() + Math.cos(shootDirection) * searchIncrement));
            RectBullet.setY((float)( RectBullet.getY() + Math.sin(shootDirection) * searchIncrement));
        }

        if (dmgModifier == 2) {
            bulletLine = new Sprite(BulletDamage, 0, 0, 16, 16);
        }
        else {
            bulletLine = new Sprite(Bullet, 0, 0, 16, 16);
        }
        bulletLine.setSize(searchDistance,2);
        bulletLine.setOrigin(1, 1);

        bulletLine.setPosition(
                player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2
        );
        bulletLine.setRotation((float) Math.toDegrees(shootDirection));

        bulletLineShow = 5;

        //final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Spawns a speed power up on the map, given the coordinates of where the enemy died which dropped it
     * @param SpeedX The x coordinate of the power up to be spawned
     * @param SpeedY The y coordinate of the power up to be spawned
     */
    public void SpawnSpeedPowerup(int SpeedX,int SpeedY){
        pickUpSpeed = true;
        SpeedPickup.setPosition(SpeedX,SpeedY);
        SpeedPickup.setSize(16,16);
        SpeedRect = SpeedPickup.getBoundingRectangle();
    }

    /**
     * Spawns a health power up on the map, given the coordinates of where the enemy died which dropped it
     * @param HealthX The x coordinate of the power up to be spawned
     * @param HealthY The y coordinate of the power up to be spawned
     */
    public void SpawnHealthPowerup(int HealthX,int HealthY){
        pickUpHealth = true;
        HealthPickup.setPosition(HealthX,HealthY);
        HealthPickup.setSize(16,16);
        HealthRect = HealthPickup.getBoundingRectangle();
    }

    /**
     * Spawns a double points power up on the map, given the coordinates of where the enemy died which dropped it
     * @param PointsX The x coordinate of the power up to be spawned
     * @param PointsY The y coordinate of the power up to be spawned
     */
    public void SpawnPointsPowerup(int PointsX,int PointsY){
        pickUpPoints = true;
        PointsPickup.setPosition(PointsX,PointsY);
        PointsPickup.setSize(16,16);
        PointsRect = PointsPickup.getBoundingRectangle();
    }

    /**
     * Spawns a double damage power up on the map, given the coordinates of where the enemy died which dropped it
     * @param DamageX The x coordinate of the power up to be spawned
     * @param DamageY The y coordinate of the power up to be spawned
     */
    public void SpawnDamagePowerup(int DamageX,int DamageY){
        pickUpDamage = true;
        DamagePickup.setPosition(DamageX,DamageY);
        DamagePickup.setSize(16,16);
        DamageRect = DamagePickup.getBoundingRectangle();
    }

    /**
     * Scales the player and enemy sprites before drawing them on screen, so they are the displayed correctly
     * Also rounds the calculated value to an integer, so it can be drawn properly
     * @param sprite The sprite to be rescaled
     * @return An array of two integers representing the new width and height of the sprite
     */
    public int[] scaleAndRoundSpriteTextures(Sprite sprite) {

        int spriteWidth = sprite.getTexture().getWidth();
        int spriteHeight = sprite.getTexture().getHeight();

        if (sprite == player) {
            spriteWidth /= PLAYER_TEXTURE_SCALING;
            spriteHeight /= PLAYER_TEXTURE_SCALING;
        } else {                                    //if the sprite is one of the enemies
            spriteWidth /= ENEMY_TEXTURE_SCALING;
            spriteHeight /= ENEMY_TEXTURE_SCALING;
        }

        spriteWidth = Math.round(spriteWidth);
        spriteHeight = Math.round(spriteHeight);

        int[] scaledRoundedValues = {spriteWidth, spriteHeight};
        return scaledRoundedValues;
    }


    //Very complicated version of Collision using multiple sprites as markers for each block the user cannot cross
    //Sets the sprites positions and checks if they overlap

    /**
     * Calculates all enemy collisions against walls and calls the enemy pathfinding method
     */
    private void enemyCollisions() {

        Room[0].setPosition(0,770);
        Room[0].setSize(570,245);

        Room[3].setPosition(0,0);
        Room[3].setSize(570,460);

        Room[4].setPosition(0,465);
        Room[4].setSize(570,290);

        Room[2].setPosition(700,0);
        Room[2].setSize(620,620);

        Room[1].setPosition(700,670);
        Room[1].setSize(620,364);

        Room[5].setPosition(580,0);
        Room[5].setSize(100,1024);


        RectPlayer = player.getBoundingRectangle();

        Collision[0].setPosition(565,0);
        Collision[0].setSize(3,180);

        Collision[1].setPosition(690,0);
        Collision[1].setSize(3,225);

        Collision[2].setPosition(565,330);
        Collision[2].setSize(3,280);

        Collision[3].setPosition(0,470);
        Collision[3].setSize(560,3);

        Collision[4].setPosition(690,390);
        Collision[4].setSize(3,50);

        Collision[5].setPosition(690,600);
        Collision[5].setSize(3,30);

        Collision[6].setPosition(690,630);
        Collision[6].setSize(300,5);

        Collision[7].setPosition(565,750);
        Collision[7].setSize(3,70);

        Collision[8].setPosition(565,940);
        Collision[8].setSize(3,200);

        Collision[9].setPosition(0,770);
        Collision[9].setSize(560,3);

        Collision[10].setPosition(850,880);
        Collision[10].setSize(20,20);



        for (int i = 0; i < 11; i++) {
            RectColl = new Rectangle(Collision[i].getX(),Collision[i].getY(), Collision[i].getWidth(),Collision[i].getHeight());
            boolean Overlapping = RectPlayer.overlaps(RectColl);
            if (Overlapping){
                StopMovement();
            }
        }
        int i;
        for (i = 0; i <6;i++){
            RectRoom[i] = new Rectangle(Room[i].getX(),Room[i].getY(),Room[i].getWidth(),Room[i].getHeight());
            boolean PlayerInRoom = RectPlayer.overlaps(RectRoom[i]);
            if (PlayerInRoom){
                NoRoomPlayer = 0;
                for (int j = 0; j < enemies.size(); j++) {
                    if (enemies.get(j).getEnemyAlive())
                        enemies.get(j).Pathfinding(i,RectRoom,playerX,playerY,RectPlayer);
                }
            }
        }
    }


    /**
     * Calculates player collisions
     */
    private void StopMovement(){
        if (RectColl.getY() > RectPlayer.getY() &&
                (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))) {
            playerHasCollided[0] = true;
        }
        else if (RectColl.getY() < RectPlayer.getY() &&
                (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))) {
            playerHasCollided[2] = true;
        }
        if (RectColl.getX() < RectPlayer.getX() &&
                (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))) {
            playerHasCollided[3] = true;
        }
        else if (RectColl.getX() > RectPlayer.getX() &&
                (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))) {
            playerHasCollided[1] = true;
        }
    }


    /**
     * Takes user input from keyboard and mouse and calls camera movement if necessary
     */
    private void takeInput() {

        //KEYBOARD INPUT

        //translate the player screen coordinates to world coordinates (the world coordinates are measured from top-left
        //whereas screen coordinates are from bottom left, so must 'invert' the y coordinate
        //PLAYERWORLDCOORDS ARE MEASURED FROM TOP LEFT, AND PLAYER X/Y FROM BOTTOM LEFT
        playerWorldCoords.set(playerX, WORLD_HEIGHT - playerY, 0);


        //player movement left and right
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setTexture(playerTextures[3]);
            if (playerWorldCoords.x > PLAYER_WORLD_EDGE_DISTANCE_X)//if the player sprite is still within the bounds of the world
                if (playerHasCollided[3] == false)
                    playerX -= MOVEMENT_SPEED;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setTexture(playerTextures[1]);
            Health[HealthNumber].setPosition(playerX,playerY-8);
            if (playerWorldCoords.x < (WORLD_WIDTH - PLAYER_WORLD_EDGE_DISTANCE_X - player.getWidth()))
                if (playerHasCollided[1] == false)
                    playerX += MOVEMENT_SPEED;
        }

        //player movement up and down
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setTexture(playerTextures[2]);
            Health[HealthNumber].setPosition(playerX+20,playerY-8);
            if (playerWorldCoords.y - player.getHeight() > PLAYER_WORLD_EDGE_DISTANCE_Y)
                if (playerHasCollided[0] == false)
                    playerY += MOVEMENT_SPEED;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setTexture(playerTextures[0]);
            Health[HealthNumber].setPosition(playerX+14,playerY-10);
            if (playerWorldCoords.y < (WORLD_HEIGHT - PLAYER_WORLD_EDGE_DISTANCE_Y))
                if (playerHasCollided[2] == false)
                    playerY -= MOVEMENT_SPEED;
        }

        //if left mouse button pressed, shoot a bullet
        if ((dmgModifier == 2) && (canShoot == 0) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            try {
                shoot();
            } catch (Exception e) {
                System.out.println("Something went wrong.");
            }
        }
        else if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            try {
                shoot();
            } catch (Exception e) {
                System.out.println("Something went wrong.");
            }
        }



        //if no key is currently being pressed, display the forward facing texture
        if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            player.setTexture(playerTextures[0]);
            player.setPosition(playerX, playerY);
        }
    }


    /**
     * Calls the spawnNewEnemy() method if appropriate
     */
    private void enemyProcesses() {

        int randomInt = MathUtils.random(1000);

        //if at least 1 second has passed since the last enemy spawned
        if (timeSinceGameStart - lastEnemySpawnTime > 1000) {
            if (randomInt == 999)
                spawnNewEnemy();
            else if (timeSinceGameStart - lastEnemySpawnTime > 5000 && Level == 1)      //if 5 seconds have passed,
                spawnNewEnemy();                                                        //spawn a new enemy anyway
            else if (timeSinceGameStart - lastEnemySpawnTime > 2500 && Level == 2)
                spawnNewEnemy();
            else if (timeSinceGameStart - lastEnemySpawnTime > 500 && Level == 3)
                spawnNewEnemy();
        }
    }


    /**
     * Spawns a new enemy by calling the constructor in the Enemy class and adding the new enemy to the enemy array
     */
    public void spawnNewEnemy() {

        boolean coordsFound = false;
        int newX = 0;
        int newY = 0;

        while (!coordsFound) {
            //generate two random integers between the edge of world buffer and the opposite edge of world buffer
            //(i.e. between 10 and 790 for x, between 10 and 470)
            newX = MathUtils.random(PLAYER_WORLD_EDGE_DISTANCE_X, WORLD_HEIGHT - PLAYER_WORLD_EDGE_DISTANCE_X);
            newY = MathUtils.random(PLAYER_WORLD_EDGE_DISTANCE_Y, WORLD_HEIGHT - PLAYER_WORLD_EDGE_DISTANCE_Y);

            if (Math.abs(newX - camera.position.x) > camera.viewportWidth / 2 + 50 &&
                    Math.abs(newY - camera.position.y) > camera.viewportHeight / 2 + 50) {
                coordsFound = true;
            }
        }

        enemies.add(new Enemy(newX, newY, Level));

        //set to the time now in milliseconds
        lastEnemySpawnTime = TimeUtils.millis();
    }

    /**
     * Handles all camera movement to keep up with the player while maintaining smooth movement
     */
    private void cameraMove() {

        //use an interpolation on the camera to move it around to follow the player
        if (playerX < SCREEN_EDGE_DISTANCE_X || playerX > (windowWidth - SCREEN_EDGE_DISTANCE_X))
                camera.position.x += (playerX - camera.position.x) * lerp * Gdx.graphics.getDeltaTime();
        if (playerY < SCREEN_EDGE_DISTANCE_Y || playerY > (windowHeight - SCREEN_EDGE_DISTANCE_Y))
            camera.position.y += (playerY - camera.position.y) * lerp * Gdx.graphics.getDeltaTime();


        //bound the camera within the world limits
        camera.position.x = MathUtils.clamp(camera.position.x,
                camera.viewportWidth/2, WORLD_WIDTH - camera.viewportWidth/2);
        camera.position.y = MathUtils.clamp(camera.position.y,
                camera.viewportHeight/2, WORLD_HEIGHT - camera.viewportHeight/2);
    }


    /**
     * Called when the player loses the game i.e. when an enemy touches the player and player health reaches zero
     */
    private void checkGameOver() {
        for (int i = 0; i < enemies.size(); i++) {
            if (RectPlayer.overlaps(enemies.get(i).getRectEnemy())) {
                Timer ++;
                if (Timer % 80 == 0){
                    HealthNumber = HealthNumber+1;
                    if (HealthNumber == 3) {
                        if (!gameOnMute)
                            gameOverSound.play(0.2f);
                        backgroundMusic.stop();
                        score.setGlobalScore(score.getLevelScore() + score.getGlobalScore());
                        this.dispose();
                        game.setScreen(new StatsScreen(game, score.getLevel(), score.getTotalZombiesKilled(), score.getGlobalScore()));
                    }
                }
            }
        }
    }


    /**
     * Used for debugging player and camera movement
     */
    private void debugShowCoordinates() {
        Gdx.app.log("Player Screen Location", playerX + ", " + playerY);
        Gdx.app.log("Player World Location", playerWorldCoords.x + ", " + playerWorldCoords.y);
        Gdx.app.log("Camera Location", camera.position.x + ", " + camera.position.y);
    }


    /**
     * Automatically called when the game window is resized - partially implemented but doesn't always work
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {

        //retrieve updated dimensions of game window on screen (can change during game so fetch every time)
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();
        mainViewport.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {
        //play bg music when screen is shown

    }

    @Override
    public void hide() {

    }



    /**
     * Dispose of all textures once the player loses the game and the screen switches to the menu
     */
    @Override
    public void dispose() {
        //player textures
        for (int i = 0; i < 4; i++) {
            playerTextures[i].dispose();
        }

        //enemy textures
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = 0; j < 8; j++) {
                enemies.get(i).getEnemyTextures()[j].dispose();
            }
        }

        //background textures
        backgroundMap.dispose();

        //health bar textures
        for (int i = 0; i < HealthBar.length; i++) {
            HealthBar[i].dispose();
        }

        //collision textures
        for (int i = 0; i < Collision.length; i++) {
            Collision[i].getTexture().dispose();
        }

        //power up textures
        //can't do them here because they're initialised before the start() method
        //(should be defined in the class definition and then initialised in the start() method

        //sounds
        winSound.dispose();
        gameOverSound.dispose();
        gunshotSound.dispose();
        playerDamageSound.dispose();
        healthBoostSound.dispose();
        speedBoostSound.dispose();
        grassWalkingSound.dispose();
        pavementWalkingSound.dispose();
        woodWalkingSound.dispose();
        zombieHitSound.dispose();
        zombieDyingSound.dispose();
    }
}