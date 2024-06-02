package com.game.flappybird;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBirdGame extends ApplicationAdapter {
	String TAG = "FlappyBirdGame";
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture backgroundTexture;
	Texture topTubeTexture;
	Texture bottomTubeTexture;
	Texture[] birdsTexture;
	Texture gameOverTexture;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	int gravity = 2;
	Circle birdCircle;

	float gap = 200;
	float maxTubeOffset;

	Random random;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Rectangle[] topTubesRectangle;
	Rectangle[] bottomTubesRectangle;

	int score = 0;
	int highScore = 0;

	int scoringTube = 0;

	//showing score
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();

		//score
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		backgroundTexture = new Texture("background-day.png");
		gameOverTexture = new Texture("gameover.png");
		birdsTexture = new Texture[3];
		birdsTexture[0] = new Texture("yellowbird-downflap.png");
		birdsTexture[1] = new Texture("yellowbird-midflap.png");
		birdsTexture[2] = new Texture("yellowbird-upflap.png");


		bottomTubeTexture = new Texture("pipe-green-up.png");
		topTubeTexture = new Texture("pipe-green-down.png");
		maxTubeOffset = (float) Gdx.graphics.getHeight() /2 - gap/2 - 100;

		random = new Random();

		distanceBetweenTubes = (float) Gdx.graphics.getWidth() / 2;

		topTubesRectangle = new Rectangle[numberOfTubes];
		bottomTubesRectangle = new Rectangle[numberOfTubes];

		startGame();

	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		flapState = ++flapState % 3;

		batch.begin();
		batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1){
			if (tubeX[scoringTube]< (float) Gdx.graphics.getWidth() /2) {
				score++;
				Gdx.app.log(TAG, "Score: " + score);
				scoringTube = ++scoringTube % numberOfTubes;

				if (score > highScore) {
					highScore = score;
				}
			}

			if (Gdx.input.justTouched()) {
				velocity = -20;
			}

			//tube
			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < -topTubeTexture.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (random.nextFloat() - .5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] -= tubeVelocity;

				}

				batch.draw(topTubeTexture, tubeX[i], (float) Gdx.graphics.getHeight() /2 + gap/2 + tubeOffset[ i]);
				batch.draw(bottomTubeTexture, tubeX[i], (float) Gdx.graphics.getHeight() /2 - gap/2-bottomTubeTexture.getHeight()+ tubeOffset[ i]);

				topTubesRectangle[i] = new Rectangle(tubeX[i], (float) Gdx.graphics.getHeight() /2 + gap/2 + tubeOffset[ i], topTubeTexture.getWidth(), topTubeTexture.getHeight());
				bottomTubesRectangle[i] = new Rectangle(tubeX[i], (float) Gdx.graphics.getHeight() /2 - gap/2-bottomTubeTexture.getHeight()+ tubeOffset[ i], bottomTubeTexture.getWidth(), bottomTubeTexture.getHeight());
			}




			if (birdY > 0 ) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				Gdx.app.log(TAG, "touched ");
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameOverTexture, (float) Gdx.graphics.getWidth() /2 - (float) gameOverTexture.getWidth() /2, (float) Gdx.graphics.getHeight() /2 - (float) gameOverTexture.getHeight() /2);
			if (Gdx.input.justTouched()) {
				Gdx.app.log(TAG, "touched ");
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}


		batch.draw(birdsTexture[flapState], (float) Gdx.graphics.getWidth() /2-birdsTexture[flapState].getWidth(), birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();

		//for collision detection
		// - bird
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		birdCircle.set((float) Gdx.graphics.getWidth() /2, birdY+ (float) birdsTexture[flapState].getHeight() /2, (float) birdsTexture[flapState].getWidth()/2);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		// - tubes
		for (int i = 0; i < numberOfTubes; i++) {
			//shapeRenderer.rect(tubeX[i], (float) Gdx.graphics.getHeight() /2 + gap/2 + tubeOffset[ i], topTubeTexture.getWidth(), topTubeTexture.getHeight());
			//shapeRenderer.rect(tubeX[i], (float) Gdx.graphics.getHeight() /2 - gap/2-bottomTubeTexture.getHeight()+ tubeOffset[ i], bottomTubeTexture.getWidth(), bottomTubeTexture.getHeight());

			if (Intersector.overlaps(birdCircle, topTubesRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubesRectangle[i])) {
				gameState = 2;
			}
		}

		shapeRenderer.end();


	}


	public void startGame() {
		birdY = (float) Gdx.graphics.getHeight() /2 - (float) birdsTexture[flapState].getHeight() /2;
		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = (float) Gdx.graphics.getWidth() /2 - (float) topTubeTexture.getWidth() /2 + distanceBetweenTubes * i;
			tubeOffset[i] = (random.nextFloat() - .5f) * (Gdx.graphics.getHeight() - gap - 200);
			topTubesRectangle[i] = new Rectangle();
			bottomTubesRectangle[i] = new Rectangle();
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundTexture.dispose();
		topTubeTexture.dispose();
		bottomTubeTexture.dispose();
		birdsTexture[0].dispose();
		birdsTexture[1].dispose();
		birdsTexture[2].dispose();
	}
}
