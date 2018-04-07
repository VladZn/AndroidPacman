package com.pacman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMap {
    public enum CellType {
        EMPTY('0'), WALL('1'), FOOD('_'), CHERRY('*'), PLAYER('s'), BLUE('b'), PINK('p'),
        RED('r'), ORANGE('o');

        char datSymbol;

        CellType(char datSymbol) {
            this.datSymbol = datSymbol;
        }
    }

    private CellType[][] data;
    private int foodCount;
    private TextureRegion textureGround;
    private TextureRegion textureWall;
    private TextureRegion textureFood;
    private TextureRegion textureCherry;

    public int getFoodCount() {
        return foodCount;
    }

    private int mapSizeX;
    private int mapSizeY;

    public static final int CELL_SIZE_PX = 80;

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    private HashMap<Character, Vector2> startPositions;

    public GameMap() {
        textureGround = Assets.getInstance().getAtlas().findRegion("ground");
        textureWall = Assets.getInstance().getAtlas().findRegion("wall");
        textureFood = Assets.getInstance().getAtlas().findRegion("food");
        textureCherry = Assets.getInstance().getAtlas().findRegion("energizer");
        loadMap("map.dat");
    }

    public void loadMap(String name) {
        startPositions = new HashMap<Character, Vector2>();
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = Gdx.files.internal(name).reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapSizeX = list.get(0).length();
        mapSizeY = list.size();
        data = new CellType[mapSizeX][mapSizeY];
        for (int y = 0; y < list.size(); y++) {
            for (int x = 0; x < list.get(y).length(); x++) {
                char currentSymb = list.get(y).charAt(x);
                for (int i = 0; i < CellType.values().length; i++) {
                    if (currentSymb == CellType.values()[i].datSymbol) {
                        data[x][mapSizeY - y - 1] = CellType.values()[i];
                        if (CellType.values()[i] == CellType.PLAYER || CellType.values()[i] == CellType.BLUE || CellType.values()[i] == CellType.PINK || CellType.values()[i] == CellType.RED || CellType.values()[i] == CellType.ORANGE) {
                            startPositions.put(currentSymb, new Vector2(x, mapSizeY - y - 1));
                        }
                        break;
                    }
                }
            }
        }
    }

    public Vector2 getUnitPosition(char unitChar) {
        return startPositions.get(unitChar).cpy();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < mapSizeX; i++) {
            for (int j = 0; j < mapSizeY; j++) {
                batch.draw(textureGround, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                if (data[i][j] == CellType.WALL) {
                    batch.draw(textureWall, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                }
                if (data[i][j] == CellType.FOOD) {
                    batch.draw(textureFood, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                }
                if (data[i][j] == CellType.CHERRY) {
                    batch.draw(textureCherry, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return data[cellX][cellY] != CellType.WALL;
    }

    public boolean checkFoodEating(float x, float y) {
        if (data[(int) x][(int) y] == CellType.FOOD) {
            data[(int) x][(int) y] = CellType.EMPTY;
            foodCount--;
            return true;
        }
        return false;
    }

    public boolean checkCherryEating(float x, float y) {
        if (data[(int) x][(int) y] == CellType.CHERRY) {
            data[(int) x][(int) y] = CellType.EMPTY;
            return true;
        }
        return false;
    }
}
