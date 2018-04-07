package com.pacman.game.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pacman.game.Assets;
import com.pacman.game.GameMap;
import com.pacman.game.GameScreen;

public class Monster extends Actor {
    PacMan target;
    int type;
    char unitChar;
    TextureRegion[] whiteRegions;

    public Monster(GameScreen gameScreen, GameMap gameMap, PacMan target, int type, char unitChar) {
        this.gameScreen = gameScreen;
        this.position = gameMap.getUnitPosition(unitChar);
        this.destination = gameMap.getUnitPosition(unitChar);
        this.target = target;
        this.textureRegions = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[type];
        this.whiteRegions = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[4];
        this.gameMap = gameMap;
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.1f;
        this.rotation = 0;
        this.tmp = new Vector2(0, 0);
        this.unitChar = unitChar;
    }

    @Override
    public void resetPosition() {
        this.position = gameMap.getUnitPosition(unitChar);
        this.destination = gameMap.getUnitPosition(unitChar);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentRegion = textureRegions[getCurrentFrame()];
        if (gameScreen.checkHuntTimer() && gameScreen.getHuntTimer() % 0.4f > 0.2f) {
            currentRegion = whiteRegions[getCurrentFrame()];
        }
        if (flipX != currentRegion.isFlipX()) {
            currentRegion.flip(true, false);
        }
        batch.draw(currentRegion, position.x * GameScreen.WORLD_CELL_PX, position.y * GameScreen.WORLD_CELL_PX, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (Vector2.dst(position.x, position.y, destination.x, destination.y) < 0.001f) {
            Direction dir = Direction.values()[MathUtils.random(0, 3)];
            move(dir, true);
        } else {
            tmp.set(destination).sub(position).nor().scl(3 * dt);
            position.add(tmp);
            if (Vector2.dst(position.x, position.y, destination.x, destination.y) < tmp.len()) {
                position.set(destination);
            }
        }
    }
}
