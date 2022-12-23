package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class wavesOfExile extends Game {

    public SpriteBatch batch;   //for rendering textures/texture regions
    public BitmapFont font;     //for rendering text
    public ShapeRenderer shape; //for rendering shapes

    //called on the start of the game
    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        shape = new ShapeRenderer();
        this.setScreen(new MenuScreen(this));

    }

    //called every frame
    @Override
    public void render() {

        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shape.dispose();
    }

}
