package com.johnson.mariobros.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.johnson.mariobros.Controller;
import com.johnson.mariobros.MarioBros;
import com.johnson.mariobros.Scenes.Hud;
import com.johnson.mariobros.Sprites.Enemies.Enemy;
import com.johnson.mariobros.Sprites.Items.Item;
import com.johnson.mariobros.Sprites.Items.ItemDef;
import com.johnson.mariobros.Sprites.Items.Mushroom;
import com.johnson.mariobros.Sprites.Mario;
import com.johnson.mariobros.Tools.B2WorldCreator;
import com.johnson.mariobros.Tools.WorldContactListener;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by johnson mendonca on 04-Jan-17.
 */

public class PlayScreen implements Screen {

    //Reference to our game, used to set Screens
    private MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    public static boolean alreadyDestroyed = false;

    //Tiled map variables
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Mario player;
    public Music music;
    public Controller controller;



    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        controller = new Controller();
        this.game = game;
        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();
        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);
        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("gamemap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        //initially set our gamecam to be centered correctly at the start of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create our box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();

        creator =new B2WorldCreator(this);

        //create mario in our game world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());
        music =Gdx.audio.newMusic(Gdx.files.internal("audio/music/mario_music.ogg"));
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();


        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

    }
    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){

    return atlas;
    }
    @Override
    public void show() {

    }

    public void handleInput(float dt){
        //if user is holding down mouse move our camera
        if(player.currentState !=Mario.State.DEAD) {
            if (controller.isUpPressed() || Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.jump();

            if (controller.isRightPressed() || Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);

            if (controller.isLeftPressed() || Gdx.input.isKeyPressed((Input.Keys.LEFT)) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

            if (controller.isDownPressed() || Gdx.input.isKeyPressed(Input.Keys.DOWN))
                player.fire();
        }
    }

    public void update(float dt){
        //handle user input first
        handleInput(dt);
        handleSpawningItems();
        //takes 1 step in the physics simulation(60 times per second)
        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : creator.getEnimies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }
        for(Item item : items)
        item.update(dt);

        hud.update(dt);

        //attach our gamecam to our players.x co-ordinate
        if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        //seperate our update logic from render
        update(delta);
        //clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render our game map
        renderer.render();

        //renderer our Box2DDebugLines
        b2dr.render(world, gamecam.combined);
            controller.draw();
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnimies())
            enemy.draw(game.batch);
        for(Item item : items)
        item.draw(game.batch);

        game.batch.end();

        //set our batch to now draw what the hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
            return false;
    }

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
      gamePort.update(width,height);
        controller.resize(width, height);
    }
    public TiledMap getMap(){
        return  map;
    }

    public World getWorld(){
        return world;
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

    @Override
    public void dispose() {
        for(Item item : items)
        item.dispose();
        player.dispose();
        map.dispose();
        music.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        for(Enemy enemy : creator.getEnimies())
            enemy.dispose();
        atlas.dispose();

    }
    public Hud getHud(){ return hud; }
}
