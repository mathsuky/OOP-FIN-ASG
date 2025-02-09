package utils;

import java.util.List;

public class Tagger extends Player<Child> {

    public Tagger(int x, int y, int pattern) {
        super(x, y, pattern);
        if (pattern == 1) {
            // 初期は北方向（北, 東, 南, 西）
            directionX = new int[]{0, 1, 0, -1};
            directionY = new int[]{1, 0, -1, 0};
        } else if (pattern == 2) {
            // 初期は南方向（南, 東, 北, 西）
            directionX = new int[]{0, 1, 0, -1};
            directionY = new int[]{-1, 0, 1, 0};
        }
    }

    /**
     * Tagger の移動処理。
     * pattern が 0～2 の場合は moveInDirection を用い、3 の場合は
     * 対象である Child の中から最も近いものに向かって 1 ステップ移動する。
     */
    @Override
    public void move(Board board, List<Child> opponents) {
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
                    moveToward(this.nearestOpponent, board);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void findNearestOpponent(List<Child> opponents) {
        Child nearest = null;
        int minDistance = Integer.MAX_VALUE;
        for (Child child : opponents) {
            if (child.isCaptured()) {
                continue;
            }
            int dx = Math.abs(this.x - child.getX());
            int dy = Math.abs(this.y - child.getY());
            int distance = Math.max(dx, dy);  // チェス盤距離
            if (distance < minDistance) {
                minDistance = distance;
                nearest = child;
            }
        }
        setNearestOpponent(nearest);
    }

    /**
     * 指定された Child に向かって 1 ステップ移動する。
     * x, y それぞれの軸での移動量は Integer.compare によって決定する。
     */
    private void moveToward(Child target, Board board) {
        int dx = target.getX() - this.x;
        int dy = target.getY() - this.y;
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);
        int nextX = this.x + stepX;
        int nextY = this.y + stepY;
        if (board.inRange(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
        }
    }

    @Override
    public Player<?> deepCopy() {
        Tagger copy = new Tagger(this.x, this.y, this.pattern);
        copy.captured = this.captured;
        copy.currentDir = this.currentDir;
        if (this.directionX != null) {
            copy.directionX = this.directionX.clone();
        }
        if (this.directionY != null) {
            copy.directionY = this.directionY.clone();
        }
        // nearestOpponent は、コピーの対象外または必要に応じて別途コピーする
        return copy;
    }
}
