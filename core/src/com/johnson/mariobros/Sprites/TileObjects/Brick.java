package com.johnson.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.johnson.mariobros.MarioBros;
import com.johnson.mariobros.Scenes.Hud;
import com.johnson.mariobros.Screens.PlayScreen;
import com.johnson.mariobros.Sprites.Mario;

/**
 * Created by johnson mendonca on 07-Jan-17.
 */

public class Brick extends InteractiveTileObject {
    private AssetManager manager;

    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
        manager = new AssetManager();
        manager.load("audio/sounds/breakblock.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.finishLoading();

    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            //when mario hits bricks with his head the points increased to 200 everytime he hits
            Hud.addScore(200);
            manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else
            manager.get("audio/sounds/bump.wav", Sound.class).play();


    }

    public void dispose(){
        manager.dispose();
    }
}
