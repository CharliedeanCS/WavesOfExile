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

public class MenuScreen implements Screen {

    //define variables for class
    final wavesOfExile game;

    private Music backgroundMusic;
    OrthographicCamera camera;

    private Texture mainMenuBackground;
    //for main menu screen
    private Sprite startButton, settingsButton, quitButton;

    //for settings screen
    private Sprite muteButton, backButton;


    private Vector3 cursorPos;
    private boolean inSettings;
    private boolean gameOnMute;

    //constructor
    public MenuScreen (final wavesOfExile game) {

        //initialise variables for class
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 1024);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/Game_sounds/Background_music.mp3"));

        mainMenuBackground = new Texture(Gdx.files.internal("menuAssets/menuBackground.png"));

        startButton = new Sprite(new Texture("menuAssets/startButton.png"), 0, 0, 385, 106);
        settingsButton = new Sprite(new Texture("menuAssets/settingsButton.png"), 0, 0, 385, 106);
        quitButton = new Sprite(new Texture("menuAssets/exitButton.png"), 0, 0, 385, 106);
        muteButton = new Sprite(new Texture("menuAssets/muteButton.png"), 0, 0, 141, 144);
        backButton = new Sprite(new Texture("menuAssets/backButton.png"), 0, 0, 149, 149);

        startButton.setPosition(350, 600);
        startButton.setSize(350, 120);
        settingsButton.setPosition(350, 450);
        settingsButton.setSize(350, 120);
        quitButton.setPosition(350, 300);
        quitButton.setSize(350, 120);
        muteButton.setPosition(450, 550);
        muteButton.setSize(180, 180);
        backButton.setPosition(450, 350);
        backButton.setSize(180, 180);

        cursorPos = new Vector3();
        inSettings = false;
        gameOnMute = false;

    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 1, 0, 0);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(mainMenuBackground, 0, 0, 1024, 1024);
        game.font.setColor(0, 1, 0, 1);
        game.font.getData().setScale(2, 2);

        if (inSettings == false) {
            startButton.draw(game.batch);
            settingsButton.draw(game.batch);
            quitButton.draw(game.batch);
        }
        else if (inSettings) {
            if (gameOnMute)
                game.font.draw(game.batch, "Game Sound Off", 430, 780);
            else
                game.font.draw(game.batch, "Game Sound On", 430, 780);
            muteButton.draw(game.batch);
            backButton.draw(game.batch);
        }
        game.batch.end();

        if(Gdx.input.justTouched()) {
            camera.unproject(cursorPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (inSettings == false) {
                //start the game
                if (startButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y)) {
                    if (!gameOnMute) {
                        backgroundMusic.play();
                        System.out.println("PLAY");
                    }
                    this.dispose();
                    game.setScreen(new GameScreen(game, gameOnMute, backgroundMusic,1, 0, 0));
                }

                //quit the game
                if (quitButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y)) {
                    this.dispose();
                    Gdx.app.exit();
                }

                //enter options screen
                if (settingsButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y)) {
                    inSettings = true;
                }
            }
            else if (inSettings) {
                //turn sound on or off
                if (muteButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y)) {

                    //toggle the state of the mute variable
                    if (!gameOnMute)
                        gameOnMute = true;
                    else
                        gameOnMute = false;
                }

                //exit options screen/go back to main menu screen
                if (backButton.getBoundingRectangle().contains(cursorPos.x, cursorPos.y)) {
                    inSettings = false;
                }
            }
        }

        //stop the music playing at the start of the menu screen each time so it can be turned off in game
        //if player decides to mute it in the menu
        if (gameOnMute) {
            backgroundMusic.stop();
        }
    }


    @Override
    public void dispose() {

        mainMenuBackground.dispose();
        startButton.getTexture().dispose();
        settingsButton.getTexture().dispose();
        quitButton.getTexture().dispose();
        muteButton.getTexture().dispose();

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