package com.pacman.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pacman.game.Assets;
import com.pacman.game.GameMap;
import com.pacman.game.GameScreen;

public class PacMan extends Actor {
    private int score;
    private int lives;
    private int foodEaten;
    private float safeTime;
    private StringBuilder guiHelper;

    public int getFoodEaten() {
        return foodEaten;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public void minusLife() {
        lives--;
        setSafeTime(3.0f);
    }

    public void setSafeTime(float safeTime) {
        this.safeTime = safeTime;
    }

    public boolean checkSafe() {
        return safeTime > 0.0f;
    }

    public PacMan(GameScreen gameScreen, GameMap gameMap) {
        this.gameScreen = gameScreen;
        this.position = gameMap.getUnitPosition('s');
        this.destination = gameMap.getUnitPosition('s');
        this.textureRegions = Assets.getInstance().getAtlas().findRegion("pacman").split(SIZE, SIZE)[0];
        this.gameMap = gameMap;
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.1f;
        this.rotation = 0;
        this.tmp = new Vector2(0, 0);
        this.lives = 3;
        this.score = 0;
        this.foodEaten = 0;
        this.guiHelper = new StringBuilder(100);
    }

    @Override
    public void resetPosition() {
        this.position = gameMap.getUnitPosition('s');
        this.destination = gameMap.getUnitPosition('s');
    }

    @Override
    public void render(SpriteBatch batch) {
        if (flipX != textureRegions[getCurrentFrame()].isFlipX()) {
            textureRegions[getCurrentFrame()].flip(true, false);
        }
        if (flipY != textureRegions[getCurrentFrame()].isFlipY()) {
            textureRegions[getCurrentFrame()].flip(false, true);
        }
        if (!checkSafe()) {
            batch.draw(textureRegions[getCurrentFrame()], position.x * GameScreen.WORLD_CELL_PX, position.y * GameScreen.WORLD_CELL_PX, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
        } else {
            if (safeTime % 0.4f < 0.2f) {
                batch.draw(textureRegions[getCurrentFrame()], position.x * GameScreen.WORLD_CELL_PX, position.y * GameScreen.WORLD_CELL_PX, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
            }
        }
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        guiHelper.setLength(0);
        guiHelper.append("Lives: ").append(lives).append("\nScore: ").append(score);
        font.draw(batch, guiHelper, 20, 700);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (checkSafe()) {
            safeTime -= dt;
        }
        if (Vector2.dst(position.x, position.y, destination.x, destination.y) < 0.001f) {
            if (gameMap.checkFoodEating(position.x, position.y)) {
                addScore(5);
                foodEaten++;
            }
            if (gameMap.checkCherryEating(position.x, position.y)) {
                addScore(100);
                foodEaten++;
                gameScreen.activateHuntTimer();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                move(Direction.RIGHT, false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                move(Direction.LEFT, false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                move(Direction.UP, false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                move(Direction.DOWN, false);
            }
        } else {
            tmp.set(destination).sub(position).nor().scl(3 * dt);
            position.add(tmp);
            if (Vector2.dst(position.x, position.y, destination.x, destination.y) < tmp.len()) {
                position.set(destination);
            }
        }
    }
}
