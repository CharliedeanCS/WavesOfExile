package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import com.mygdx.game.wavesOfExile;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Waves of Exile");
		config.setWindowedMode(800,480);	//TODO: fix the scaling issues at higher resolutions
		new Lwjgl3Application(new wavesOfExile(), config);
	}
}
