package com.qualcomm.vuforia.samples.libGDX;

        import com.badlogic.gdx.Game;
        import com.badlogic.gdx.assets.AssetManager;
        import com.badlogic.gdx.graphics.g3d.Model;
        import com.badlogic.gdx.utils.Array;
        import com.qualcomm.vuforia.samples.singletons.PropertiesSingleton;
        import com.qualcomm.vuforia.samples.libGDX.screens.ChooseIslandScreen;

public class LaunchGame extends Game {

    public AssetManager assets;
    Array<String> islandNames;
    Array<String> ballNames;
    boolean loading = true;

    @Override
    public void create() {
        assets = new AssetManager();
        islandNames = new Array<String>();
        ballNames = new Array<String>();

        islandNames.add("island");
        islandNames.add("greek");
        islandNames.add("darkice");

        ballNames.add("apple"); //
        ballNames.add("bomb");
        ballNames.add("earth");
        ballNames.add("football");//
        ballNames.add("heart");
        ballNames.add("neon");
        ballNames.add("peach");
        ballNames.add("yarn");

        assets = new AssetManager();
        for(int i = 0; i<islandNames.size; i++)
            assets.load("3d/islands/"+islandNames.get(i)+".g3db", Model.class);

        for(int i = 0; i<ballNames.size; i++)
            assets.load("3d/balls/"+ballNames.get(i)+".g3db", Model.class);

        assets.load("3d/misc/"+"arrow"+".g3db", Model.class);

        assets.finishLoading();

        PropertiesSingleton.getInstance().setAssets(assets);
//        setScreen(new ChooseIslandScreen(this));

    }

}
