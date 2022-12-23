package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Music;

import javax.swing.*;

public class StatsScreen implements Screen {

    //define variables for class
    final wavesOfExile game;

    private Music backgroundMusic;
    OrthographicCamera camera;

    private Texture mainMenuBackground;
    //for main menu screen
    private Sprite startButton, settingsButton, quitButton;

    //for settings screen
    private Sprite muteButton, backButton;

    int level = 0;
    int totalZombiesKilled = 0;
    int score = 0;

    private Vector3 cursorPos;
    private boolean inSettings;
    private boolean gameOnMute;

    /**
     * Constructor method for the main game class - initialises all variables and sets up parameters for game
     * @param game Instance of the libgdx 'game' application listener that allows the whole game to run, and switching between screens
     * @param level Level the player reached
     * @param totalKills Number of zombies killed. 0 if player is starting level 1, otherwise passed from previous level
     * @param score Score the player reached this game
     */
    public StatsScreen(final wavesOfExile game, int level, int totalKills, int score) {

        this.level = level;
        this.totalZombiesKilled = totalKills;
        this.score = score;

        //initialise variables for class
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 1024);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/Game_sounds/Background_music.mp3"));

        mainMenuBackground = new Texture(Gdx.files.internal("menuAssets/menuBackground.png"));


        backButton = new Sprite(new Texture("menuAssets/backButton.png"), 0, 0, 149, 149);
        backButton.setPosition(400, 225);
        backButton.setSize(180, 180);

        cursorPos = new Vector3();
        gameOnMute = false;

    }

    /**
     * Called every frame in game. Just builds the screen
     *
     * @param delta
     */
    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 1, 0, 0);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(mainMenuBackground, 0, 0, 1024, 1024);
        game.font.setColor(1, 1, 1, 1);
        game.font.getData().setScale(3.0f);

        //displaying the player score
        game.font.draw(game.batch, "End of Game Summary", 300, 800);
        game.font.draw(game.batch, "Level Reached: " + level, 300, 725);
        game.font.draw(game.batch, "Zombies Killed: " + totalZombiesKilled, 300, 650);
        game.font.draw(game.batch, "Score: " + score, 300, 575);

        backButton.draw(game.batch);
        game.batch.end();

        //if the player presses the back button, call a new instance of menu screen
        if(Gdx.input.justTouched()) {
            camera.unproject(cursorPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (backButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y))
            {
                game.setScreen(new MenuScreen(game));
            }

        }
    }


    @Override
    public void dispose() {

    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }


}

