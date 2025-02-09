package utils;

import java.util.List;

public class Tagger extends Player {


    public Tagger(int x, int y, int pattern) {
        super(x, y, pattern);
        if (pattern == 1) {
            directionX = new int[]{0, 1, 0, -1}; //北，東，南，西
            directionY = new int[]{1, 0, -1, 0};
        } else if (pattern == 2) {
            directionX = new int[]{0, 1, 0, -1}; //南，東，北，西
            directionY = new int[]{-1, 0, 1, 0};
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