package utils;

public class Board {
    private int sizeX;
    private int sizeY;

    public Board(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public boolean inRange(int x, int y) {
        return 0 <= x && x <= sizeX && 0 <= y && y <= sizeY;
    }

}
