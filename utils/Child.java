package utils;

import java.util.List;

public class Child extends Player {

    public Child(int x, int y, int pattern) {
        super(x, y, pattern);
        if (pattern == 1) {
            directionX = new int[]{1, 0, -1, 0};    //東, 南, 西, 北
            directionY = new int[]{0, -1, 0, 1};
        } else if (pattern == 2) {
            directionX = new int[]{-1, 0, 1, 0};    //西, 南, 東, 北
            directionY = new int[]{0, -1, 0, 1};
        }
    }

    @Override
    public void move(Board board, List<Player> allPlayers) {
        if (captured) {
            return; // 捕まっている場合は移動しない 通常は鬼は捕まらないが，念のため
        }
        switch (pattern) {
            case 0:
                // 動かない
                break;
            case 1:
                // 初期は北方向へ移動、端に着いたら時計回りに方向転換
                moveInDirection(board);
                break;
            case 2:
                // 初期は南方向へ移動、端に着いたら反時計回りに方向転換
                moveInDirection(board);
                break;
            case 3:
                // 全ての子プレイヤーから最も近いものを探し、その子に向かって1ステップ移動
                // Chessboard Distance（チェス盤の距離）などを用いるとよい
                break;
            default:
                break;
        }
    }
}
