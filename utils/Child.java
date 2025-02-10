package utils;

import java.util.List;

public class Child extends Player<Tagger> {

    public Child(int x, int y, int pattern) {
        super(x, y, pattern);
        if (pattern == 1) {
            // 初期は東, 南, 西, 北 の順
            directionX = new int[]{1, 0, -1, 0};
            directionY = new int[]{0, -1, 0, 1};
        } else if (pattern == 2) {
            // 初期は西, 南, 東, 北 の順
            directionX = new int[]{-1, 0, 1, 0};
            directionY = new int[]{0, -1, 0, 1};
        } else if (pattern == 3) {
            // 初期は東, 南, 西, 北 の順
            directionX = new int[]{1, 0, -1, 0};
            directionY = new int[]{0, -1, 0, 1};
        }
    }

    @Override
    public void move(Board board, List<Tagger> opponents) {
        if (captured) {
            return;
        }
        switch (pattern) {
            case 0:
                // 動かない
                break;
            case 1:
            case 2:
                moveInDirection(board);
                break;
            case 3:
                if (this.nearestOpponent != null) {
                    moveAwayFrom(this.nearestOpponent, board);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void findNearestOpponent(List<Tagger> opponents) {
        Tagger nearest = null;
        int minDistance = Integer.MAX_VALUE;
        for (Tagger tagger : opponents) {
            // チェス盤距離を計算
            int dx = Math.abs(this.x - tagger.getX());
            int dy = Math.abs(this.y - tagger.getY());
            int distance = Math.max(dx, dy);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = tagger;
            }
        }
        setNearestOpponent(nearest);
    }

    private void moveAwayFrom(Tagger target, Board board) {
        int dx = target.getX() - this.x;
        int dy = target.getY() - this.y;
        // 移動方向を決定，最も近い敵と反対方向に
        int intendedStepX = Integer.compare(0, dx);
        int intendedStepY = Integer.compare(0, dy);
        // 縦横どちらかのみに移動しようとする場合
        if (intendedStepX == 0 || intendedStepY == 0) {
            if (intendedStepX > 0) {
                currentDir = 0; // 東
            } else if (intendedStepY < 0) {
                currentDir = 1; // 南
            } else if (intendedStepX < 0) {
                currentDir = 2; // 西
            } else {
                currentDir = 3; // 北
            }
            int attempts = 0;
            while (attempts < 2) {
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
            return;
        }

        // 斜め方向に移動しようとする場合
        // まずは水平方向に移動
        int nextX = x + intendedStepX;
        if (board.inRange(nextX, y)) {
            x = nextX;
        }
        // 次に垂直方向に移動
        int nextY = y + intendedStepY;
        if (board.inRange(x, nextY)) {
            y = nextY;
        }
        // いずれも不可能な場合は、移動せず現在の位置に留まる
    }

    @Override
    public Player<?> deepCopy() {
        Child copy = new Child(this.x, this.y, this.pattern);
        copy.captured = this.captured;
        copy.currentDir = this.currentDir;
        if (this.directionX != null) {
            copy.directionX = this.directionX.clone();
        }
        if (this.directionY != null) {
            copy.directionY = this.directionY.clone();
        }
        copy.nearestOpponent = null;
        return copy;
    }
}
