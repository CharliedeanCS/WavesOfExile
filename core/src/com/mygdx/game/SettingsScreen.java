package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Music;

public class SettingsScreen implements Screen
{

    //Vars
        final SettingsScreen game;
        private Sprite muteButton, backButton;
        private boolean gameOnMute;
        private Music backgroundMusic;
        OrthographicCamera camera;
        private Texture SettingsBackground;

    //constructor
    public SettingsScreen(final SettingsScreen game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 1024);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/Game_sounds/Background_music.mp3"));
        //Slider code does not work
        //transport mute button here
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 1, 0, 0);
        camera.update();

        //game.batch.draw(SettingsBackground, 0, 0, 1024, 1024);
        //game.font.setColor(0, 1, 0, 1);
        //game.font.getData().setScale(2, 2);

        backButton = new Sprite(new Texture("menuAssets/backButton.png"), 0, 0, 149, 149);

        backButton.setPosition(450, 350);
        backButton.setSize(180, 180);
    }

    @Override
    public void show() {

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
    public void hide() {

    }

}
