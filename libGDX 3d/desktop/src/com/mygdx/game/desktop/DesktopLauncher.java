package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.BaseGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Debug screen resolutions

//		config.width = 1280;
//		config.height = 720;

//		config.width = 800;
//		config.height = 600;

//		config.width = 640;
//		config.height = 400;

//		config.width = 2560;
//		config.height = 1440;

		config.width = 1920;
		config.height = 1080;

//		config.width = 1777;
//		config.height = 1080;

		config.samples = 3;

		new LwjglApplication(new BaseGame(), config);
	}
}