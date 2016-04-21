package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.BaseGame;
import com.mygdx.game.ContactTest;
import com.mygdx.game.ContactTest2;
import com.mygdx.game.GameScreen;
import com.mygdx.game.test;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new BaseGame(), config);
	}
}