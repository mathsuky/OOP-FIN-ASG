package utils;

import java.util.List;

public abstract class Player {
    protected int x;
    protected int y;
    protected int pattern;
    protected boolean captured = false;
    protected int[] directionX;
    protected int[] directionY;
    protected int currentDir = 0;

    public Player(int x, int y, int pattern) {
        this.x = x;
        this.y = y;
        this.pattern = pattern;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void moveInDirection(Board board) {
        int attempts = 0;
        while (attempts < 4) {
            int nextX = x + directionX[currentDir];
            int nextY = y + directionY[currentDir];
            if (board.inRange(nextX, nextY)) {
                x = nextX;
                y = nextY;
                return;
            }
            currentDir = (currentDir + 1) % 4;
            attempts++;
        }
    }

    public abstract void move(Board board, List<Player> allPlayers);
}
