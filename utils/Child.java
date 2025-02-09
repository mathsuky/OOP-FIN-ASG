package utils;

import java.util.List;

public class Child extends Player<Tagger> {

    public Child(int x, int y, int pattern) {
        super(x, y, pattern);
        if (pattern == 1) {
            // 初期は東, 南, 西, 北 の順（東側を優先）
            directionX = new int[]{1, 0, -1, 0};
            directionY = new int[]{0, -1, 0, 1};
        } else if (pattern == 2) {
            // 初期は西, 南, 東, 北 の順（西側を優先）
            directionX = new int[]{-1, 0, 1, 0};
            directionY = new int[]{0, -1, 0, 1};
        } else if (pattern == 3) {
            // 初期は東, 南, 西, 北 の順（東側を優先）
            directionX = new int[]{1, 0, -1, 0};
            directionY = new int[]{0, -1, 0, 1};
        }
    }

    /**
     * Child の移動処理。
     * pattern 0～2 の場合は moveInDirection を用い、
     * pattern 3 の場合は最も近い鬼から離れる方向へ 1 ステップ移動する。
     */
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
            int dx = Math.abs(this.x - tagger.getX());
            int dy = Math.abs(this.y - tagger.getY());
            int distance = Math.max(dx, dy);  // チェス盤距離
            if (distance < minDistance) {
                minDistance = distance;
                nearest = tagger;
            }
        }
        setNearestOpponent(nearest);
    }

    /**
     * 指定された Tagger から離れる方向へ移動する。
     * 基本的には「近づく」場合の逆方向をとるが、端にいてその方向が進めない場合は
     * 水平・垂直のみの移動、または時計回りの代替方向を試みる。
     */
    private void moveAwayFrom(Tagger target, Board board) {
//        System.out.println("this: " + this.x + " " + this.y);
        int dx = target.getX() - this.x;
        int dy = target.getY() - this.y;
        //printデバッグ
//        System.out.println("dx: " + dx + " dy: " + dy);
        // 近づくための移動方向（-1,0,1）の逆をとる
//        int intendedStepX = -Integer.compare(dx, 0);
//        int intendedStepY = -Integer.compare(dy, 0);
        int intendedStepX;
        int intendedStepY;
        if (dx > 0) {
            intendedStepX = -1;
        } else if (dx < 0) {
            intendedStepX = 1;
        } else {
            intendedStepX = 0;
        }
        if (dy > 0) {
            intendedStepY = -1;
        } else if (dy < 0) {
            intendedStepY = 1;
        } else {
            intendedStepY = 0;
        }
        //printデバッグ
//        System.out.println("intendedStepX: " + intendedStepX + " intendedStepY: " + intendedStepY);
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
            moveInDirection(board);
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
        // nearestOpponent はここではコピーせず、必要に応じて別途設定してください。
        copy.nearestOpponent = null;
        return copy;
    }
}
