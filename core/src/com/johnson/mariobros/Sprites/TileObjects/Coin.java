package com.johnson.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.johnson.mariobros.MarioBros;
import com.johnson.mariobros.Scenes.Hud;
import com.johnson.mariobros.Screens.PlayScreen;
import com.johnson.mariobros.Sprites.Items.ItemDef;
import com.johnson.mariobros.Sprites.Items.Mushroom;
import com.johnson.mariobros.Sprites.Mario;

/**
 * Created by johnson mendonca on 07-Jan-17.
 */

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileset;
    private final int BLANK_COIN = 28;
    private AssetManager manager;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileset = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
        manager = new AssetManager();
        manager.load("audio/sounds/coin.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
        manager.finishLoading();

    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLANK_COIN)
            manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();

            } else
                manager.get("audio/sounds/coin.wav", Sound.class).play();
        getCell().setTile(tileset.getTile(BLANK_COIN));
        //when mario hits coin with his head the points increased to 100 everytime he hits
        Hud.addScore(100);
       }
    }

    public void dispose(){
        manager.dispose();
    }
}
