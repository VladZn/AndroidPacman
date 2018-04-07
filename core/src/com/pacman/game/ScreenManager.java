package com.pacman.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pacman.game.units.PacMan;

public class ScreenManager {
    public enum ScreenType {
        MENU, GAME, GAMEOVER
    }

    private MyGdxGame game;
    private SpriteBatch batch;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private LoadingScreen loadingScreen;
    private MenuScreen menuScreen;
    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    private ScreenManager() {
    }

    public void init(MyGdxGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(1280, 720);
        this.viewport = new FitViewport(1280, 720, camera);
        this.gameScreen = new GameScreen(batch, camera);
        this.menuScreen = new MenuScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
        this.gameOverScreen = new GameOverScreen(batch);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCamera() {
        camera.position.set(640, 360, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
        resetCamera();
        game.setScreen(loadingScreen);
        switch (type) {
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case GAMEOVER:
                targetScreen = gameOverScreen;
                Assets.getInstance().loadAssets(ScreenType.GAMEOVER);
                break;
        }
    }

    public void transferPacmanToGameOverScreen(PacMan pacMan) {
        gameOverScreen.setPacMan(pacMan);
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
