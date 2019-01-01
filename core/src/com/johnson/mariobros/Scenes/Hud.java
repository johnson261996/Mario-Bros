package com.johnson.mariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.johnson.mariobros.MarioBros;


/**
 * Created by johnson mendonca on 05-Jan-17.
 */

public class Hud implements Disposable {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Mario score/time Tracking Variables
    private boolean timeUp; // true when the world timer reaches 0
    private  Integer worldTimer;
    private  float timeCount;
    private static Integer score;

    //Scene2D widgets
    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timerLabel;
    private Label levelLabel;
    private Label worldLabel;
    private  Label marioLabel;

    public Hud(SpriteBatch sb){
        //define our tracking variables
        worldTimer = 300;
        timeCount =0;
        score = 0;

        /*setup the Hud viewport using a new camera seperate from our gamecam
         and define our stage using that viewport and our games spritebatch*/
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table =new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define our labels using the String, and a Label style consisting of
        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timerLabel=new Label("TIME", new Label.LabelStyle(new BitmapFont(),Color.WHITE));
        levelLabel=new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel=new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel=new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add our labels to our table,padding the top,and giving them all equal width expandX
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timerLabel).expandX().padTop(10);
        //add a second flow to our table
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        //add our table to the stage
        stage.addActor(table);

    }
    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }
    public static void addScore(int value){
        score +=value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    public boolean isTimeUp() {
        return timeUp; }

}
