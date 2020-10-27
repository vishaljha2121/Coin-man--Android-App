package com.vishal.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture  dizzyMan;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinsYs = new ArrayList<Integer>();
	int coinCount = 0;
	Texture coin;
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	int bombCount = 0;
	Texture bomb;
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();

	Rectangle manRectangle;

	int score = 0;
	BitmapFont scoreFont, startingFont, endingFont;
	int gameState = 0;

	Random random;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		dizzyMan = new Texture("dizzy-1.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().scale(10);

		startingFont = new BitmapFont();
		startingFont.setColor(Color.YELLOW);
		startingFont.getData().scale(5);

		endingFont = new BitmapFont();
		endingFont.setColor(Color.RED);
		endingFont.getData().scale(5);
	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinsYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getHeight());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1){
			//game is live
			//bombs
			if (bombCount <150){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
			}
			bombRectangle.clear();
			for (int j = 0;j<bombXs.size();j++){
				batch.draw(bomb,bombXs.get(j),bombYs.get(j));
				bombXs.set(j,bombXs.get(j) - 8);
				bombRectangle.add(new Rectangle(bombXs.get(j),bombYs.get(j),bomb.getWidth(),bomb.getHeight()));
			}

			//coins
			if(coinCount < 100) {
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}

			coinRectangle.clear();
			for (int i = 0;i < coinXs.size();i++){
				batch.draw(coin,coinXs.get(i),coinsYs.get(i));
				coinXs.set(i,coinXs.get(i) - 4);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinsYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			if (Gdx.input.justTouched()){
				velocity = -10;
			}
			if (pause<8){
				pause++;
			}else {
				pause =0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity += gravity;
			manY -= velocity;
			if (manY <= 0){
				manY = 0;
			}

		}else if(gameState == 0){
			//waiting to start
			startingFont.draw(batch,"COIN MAN BY VISHAL \nTOUCH TO START",Gdx.graphics.getWidth()/2 - 400,Gdx.graphics.getHeight()/2);
			if (Gdx.input.justTouched()){
				gameState = 1;
			}

		}else if (gameState == 2){
			//game over
			endingFont.draw(batch,"GAME OVER \n TOUCH TO RESTART",Gdx.graphics.getWidth()/2 - 400,Gdx.graphics.getHeight()/2);
			if (Gdx.input.justTouched()){
				gameState = 1;
				score = 0;
				manY = Gdx.graphics.getHeight() / 2;
				coinXs.clear();
				coinsYs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount = 0;
			}

		}



		if (gameState == 1) {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else if (gameState == 2){
			batch.draw(dizzyMan,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}


		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY,man[manState].getWidth(),man[manState].getHeight());

		for (int x = 0; x < coinRectangle.size(); x++){
			if (Intersector.overlaps(manRectangle,coinRectangle.get(x))){
				//Gdx.app.log("Coin!","Collision");
				score++;
				coinRectangle.remove(x);
				coinXs.remove(x);
				coinsYs.remove(x);
				break;
			}
		}
		for (int x = 0; x < bombRectangle.size(); x++){
			if (Intersector.overlaps(manRectangle,bombRectangle.get(x))){
				Gdx.app.log("Bomb!","Collision");
				gameState = 2;
			}
		}

		scoreFont.draw(batch,String.valueOf(score),100,200);
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
