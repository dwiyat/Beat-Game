package edu.mills.cs280.audiorunner;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * ScoreBoard holds the player's score and implements the visual for it and score floaters
 * 
 * 
 * @author jklein
 *
 */
public class ScoreBoard {
	private final int FONTSIZE = 64; // Size of Font in Texture
	private final float SCREENPERCENTAGE = 0.1f; // Percentage of screen to draw
	// score on
	private final float FLOATER_SPEED = .03f;

	private Texture mNumbersTexture;
	private int mScore;
	private float mDrawSize; // Size to draw font on screen
	private float mNewScore;
	private LinkedList<ScoreFloater> floaters;

	/**
	 * constuctor, initializes variables
	 */

	public ScoreBoard() {
		mNumbersTexture = new Texture(Gdx.files.internal("data/numbers.png"));
		mScore = 0;
		mDrawSize = Gdx.graphics.getHeight() * SCREENPERCENTAGE;
		floaters = new LinkedList<ScoreFloater>();
	}

	/**
	 * Draws the score at top right of screen
	 * 
	 * @param SpriteBatch
	 *            , Draw within the current SpriteBatch
	 */
	public void draw(SpriteBatch spriteBatch) {
		String mScoreArray = Integer.toString(mScore);

		if (mScore < mNewScore) {
			mScore++;
		} else {
			mNewScore = 0;
		}

		spriteBatch.begin();
		for (int i = 0; i < mScoreArray.length(); i++) {
			char tempChar = mScoreArray.charAt(i);
			spriteBatch.draw(
					mNumbersTexture,
					(float) (Gdx.graphics.getWidth() - mDrawSize
							* (mScoreArray.length() - i)), // Draw at X position
							(float) (Gdx.graphics.getHeight() - mDrawSize), // Draw at Y
							// position
							mDrawSize, mDrawSize, // Size of Number
							Character.digit(tempChar, 10) * FONTSIZE, 0, // Get part of
							// texture
							FONTSIZE, FONTSIZE, // Size of gotten part
							false, false); // Flip X, Flip Y
		}

		for (int i = 0; i < floaters.size(); i++) {
			ScoreFloater tempFloater = floaters.get(i);
			tempFloater.moveFloater();
			if (tempFloater.getCurrentPosition().distanceFrom(
					Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) < Gdx.graphics
					.getWidth() * SCREENPERCENTAGE) {
				addCountingScore(tempFloater.points);
				floaters.remove(i);
			} else {
				tempFloater.draw(spriteBatch);
			}
		}

		spriteBatch.end();
	}

	/**
	 * Adds some points to the score
	 * 
	 * @param Integer
	 *            , Amount of points to add
	 */
	public void addScore(int points) {
		mScore += points;
	}

	/**
	 * Adds some points to the score and have the scoreBoard count up to it
	 * 
	 * @param Integer
	 *            , Amount of points to add
	 */
	public void addCountingScore(int points) {
		mNewScore = mScore + points;
	}

	/**
	 * Retrieves score 
	 * @return score
	 */
	public int getScore(){
		return mScore;
	}

	/**
	 * Adds some points to the score
	 * 
	 * @param Integer
	 *            , Amount of points to add
	 */
	public void addFloaterScore(int x, int y, int points) {
		floaters.add(new ScoreFloater(x,
				y - ScreenHandler.getWorldPosition().y, points));
	}

	/**
	 * Nested ScoreFloater Class for adding scores that float eventually to the score board
	 * 
	 * @param Integer
	 *            , X position to start
	 * @param Integer
	 *            , Y position to start
	 * @param Integer
	 *            , amount of points to add
	 */
	class ScoreFloater {
		private final float FLOATERSIZE = .8f; // ratio of size floaters will draw as

		private float posX;
		private float posY;
		private int points;
		
		/**
		 * Returns the current position of X and Y
		 * @return the position of X and Y at the specified position
		 */
		public Vector2 getCurrentPosition(){
			return new Vector2((int)posX,(int)posY);
		}

		/**
		 * 
		 * @param x Assigns position of X to x
		 * @param y Assigns position of Y to y
		 * @param points Assigns points to points
		 */
		public ScoreFloater(int x, int y, int points){
			posX = x;
			posY = y;
			this.points = points;
		}

		/**
		 * Moves the position of the floater
		 */
		public void moveFloater() {
			posX += Math.abs(posX - Gdx.graphics.getWidth()) * FLOATER_SPEED
			* TimeHandler.getTransitionScale();
			posY += Math.abs(posY - Gdx.graphics.getHeight()) * FLOATER_SPEED
			* TimeHandler.getTransitionScale();
		}

		/**
		 * Draws the score board
		 * @param spriteBatch draws textures in the SpriteBatch
		 */
		public void draw(SpriteBatch spriteBatch) {
			String pointArray = Integer.toString(points);
			for (int i = 0; i < pointArray.length(); i++) {
				char tempChar = pointArray.charAt(i);
				float floaterSize = mDrawSize * FLOATERSIZE;
				spriteBatch.draw(
						mNumbersTexture,
						(float) (posX + floaterSize - floaterSize
								* (pointArray.length() - i)), // Draw at X
								// position
								(float) (posY + floaterSize), // Draw at Y position
								floaterSize, floaterSize, // Size of Number
								Character.digit(tempChar, 10) * FONTSIZE, 0, // Get part
								// of
								// texture
								FONTSIZE, FONTSIZE, // Size of gotten part
								false, false); // Flip X, Flip Y
			}
		}
	}

}
