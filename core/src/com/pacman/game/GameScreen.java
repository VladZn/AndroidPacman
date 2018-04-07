package com.pacman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pacman.game.units.Monster;
import com.pacman.game.units.PacMan;

public class GameScreen implements Screen {
    public static final int WORLD_CELL_PX = 80;

    private SpriteBatch batch;

    private PacMan pacMan;
    private GameMap gameMap;
    private Camera camera;
    private Monster[] monsters;
    private BitmapFont font48;
    private float huntTimer;

    private Stage stage;
    private Skin skin;

    private boolean paused;

    public void activateHuntTimer() {
        huntTimer = 5.0f;
    }

    public boolean checkHuntTimer() {
        return huntTimer > 0.0f;
    }

    public float getHuntTimer() {
        return huntTimer;
    }

    public GameScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    // Домашнее задание:
    // Разбор кода
    // Реализовать мозги ботам, пусть гоняются за пакманом если он в пределах 8 клеток
    // ------------------------------------
    // План работы:
    // Сохранение состояния игры
    // Карта должна быть из оригинальной игры
    // Таблица рекордов с вводом имени на GameOverScreen
    // Рефакторинг глобальный
    // Перенос на андроид (начнем на 7 занятии с этого)
    // Разобраться с едой: точки, вишни и энерджайзеры
    // Уровни
    // Уровни сложности(радиус погони, скорость движения ботов, скорость пакмана)
    // Инструкция/правила
    // Пасхалки(секретный уровень)/видеоролики/подкрутить графику/мутаторы?
    // Дать имена ботам (блинки, пинки, инки, клайд)

    @Override
    public void show() {
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");
        this.gameMap = new GameMap();
        this.pacMan = new PacMan(this, gameMap);
        this.monsters = new Monster[4];
        this.monsters[0] = new Monster(this, gameMap, pacMan, 0, 'r');
        this.monsters[1] = new Monster(this, gameMap, pacMan, 1, 'b');
        this.monsters[2] = new Monster(this, gameMap, pacMan, 2, 'o');
        this.monsters[3] = new Monster(this, gameMap, pacMan, 3, 'p');
        this.camera.position.set(640, 360, 0);
        this.camera.update();
        this.huntTimer = 0.0f;
        this.createGUI();
        this.paused = false;
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font48", font48);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("shortButton");
        textButtonStyle.font = font48;
        skin.add("simpleSkin", textButtonStyle);

        Button btnPause = new TextButton("II", skin, "simpleSkin");
        Button btnMenu = new TextButton("M", skin, "simpleSkin");
        btnPause.setPosition(1140, 580);
        btnMenu.setPosition(1140, 40);
        stage.addActor(btnPause);
        stage.addActor(btnMenu);
        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = !paused;
            }
        });
        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameMap.render(batch);
        pacMan.render(batch);
        for (int i = 0; i < monsters.length; i++) {
            monsters[i].render(batch);
        }
        resetCamera();
        batch.setProjectionMatrix(camera.combined);
        pacMan.renderGUI(batch, font48);
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        if (!paused) {
            pacMan.update(dt);
            for (int i = 0; i < monsters.length; i++) {
                monsters[i].update(dt);
            }
            checkCollisions();
            if (checkHuntTimer()) {
                huntTimer -= dt;
            }
        }
        cameraTrackPacMan();
        stage.act(dt);
    }

    public void checkCollisions() {
        for (int i = 0; i < monsters.length; i++) {
            if (Vector2.dst(pacMan.getPosition().x + 0.5f, pacMan.getPosition().y + 0.5f, monsters[i].getPosition().x + 0.5f, monsters[i].getPosition().y + 0.5f) < 0.5f) {
                if (!pacMan.checkSafe() && !checkHuntTimer()) {
                    pacMan.resetPosition();
                    pacMan.minusLife();
                    if (pacMan.getLives() < 0) {
                        ScreenManager.getInstance().transferPacmanToGameOverScreen(pacMan);
                        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER);
                    }
                }
                if (checkHuntTimer()) {
                    pacMan.addScore(200);
                    monsters[i].resetPosition();
                }
            }
        }
    }

    public void cameraTrackPacMan() {
        camera.position.set(pacMan.getPosition().x * 80 + 40, pacMan.getPosition().y * 80 + 40, 0);
        if (camera.position.x < 640) {
            camera.position.x = 640;
        }
        if (camera.position.y < 360) {
            camera.position.y = 360;
        }
        if (camera.position.x > gameMap.getMapSizeX() * GameMap.CELL_SIZE_PX - 640) {
            camera.position.x = gameMap.getMapSizeX() * GameMap.CELL_SIZE_PX - 640;
        }
        if (camera.position.y > gameMap.getMapSizeY() * GameMap.CELL_SIZE_PX - 360) {
            camera.position.y = gameMap.getMapSizeY() * GameMap.CELL_SIZE_PX - 360;
        }
        camera.update();
    }

    public void resetCamera() {
        camera.position.set(640, 360, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
