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
                Tagger nearest = findNearestOpponent(opponents);
                if (nearest != null) {
                    moveAwayFrom(nearest, board);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected Tagger findNearestOpponent(List<Tagger> opponents) {
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
        return nearest;
    }

    /**
     * 指定された Tagger から離れる方向へ移動する。
     * 基本的には「近づく」場合の逆方向をとるが、端にいてその方向が進めない場合は
     * 水平・垂直のみの移動、または時計回りの代替方向を試みる。
     */
    private void moveAwayFrom(Tagger target, Board board) {
        int dx = target.getX() - this.x;
        int dy = target.getY() - this.y;
        // 近づくための移動方向（-1,0,1）の逆をとる
        int intendedStepX = -Integer.compare(dx, 0);
        int intendedStepY = -Integer.compare(dy, 0);

        int nextX = this.x + intendedStepX;
        int nextY = this.y + intendedStepY;
        // 対角移動が可能かチェック
        if (board.inRange(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
            return;
        }
        // 対角移動不可の場合、水平のみで試す
        nextX = this.x + intendedStepX;
        nextY = this.y;
        if (board.inRange(nextX, nextY)) {
            this.x = nextX;
            return;
        }
        // 垂直のみで試す
        nextX = this.x;
        nextY = this.y + intendedStepY;
        if (board.inRange(nextX, nextY)) {
            this.y = nextY;
            return;
        }
        // さらに、時計回りの代替方向を試す（例：北端で北が進めないなら東方向を試す）
        int altStepX = intendedStepY;
        int altStepY = -intendedStepX;
        nextX = this.x + altStepX;
        nextY = this.y + altStepY;
        if (board.inRange(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
            return;
        }
        // いずれも不可能な場合は、移動せず現在の位置に留まる
    }
}
