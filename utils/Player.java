package utils;

import java.util.List;

public abstract class Player<O extends Player<?>> {
    protected int x;
    protected int y;
    protected int pattern;
    protected boolean captured = false;
    protected int[] directionX;
    protected int[] directionY;
    protected int currentDir = 0;
    protected O nearestOpponent;

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

    /**
     * nearestOpponent の getter
     */
    public O getNearestOpponent() {
        return nearestOpponent;
    }

    /**
     * nearestOpponent の setter
     */
    public void setNearestOpponent(O opponent) {
        if (opponent == null) {
            this.nearestOpponent = null;
        } else {
            // 各サブクラスで実装された deepCopy() を呼び出して深いコピーを得る
            this.nearestOpponent = (O) opponent.deepCopy();
        }
    }

    /**
     * 盤面上で現在の方向（currentDir）に沿って移動を試みる。
     * 盤面外の場合は、最大4方向試行して移動可能な方向を探す。
     */
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

    /**
     * 指定された盤面と対象相手リストを用いて移動を行う。
     * O は対象となる相手の型（Tagger なら Child、Child なら Tagger）を示す。
     */
    public abstract void move(Board board, List<O> opponents);

    /**
     * 渡された対象相手リストの中から、チェス盤距離（Chessboard Distance）が最小の相手を返す。
     */
    protected abstract void findNearestOpponent(List<O> opponents);

    public abstract Player<?> deepCopy();
}
