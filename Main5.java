import utils.Board;
import utils.Child;
import utils.Player;
import utils.Tagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// TODO: 絶対に標準出力ではなくてファイル出力に変え忘れない！！！！！！！！
class Main5 {
    public static void main(String[] args) {
        // 引数の数が1でなければ使用方法を表示して終了する
        if (args.length != 1) {
            System.err.println("Usage: java Main5 <input-file>");
            System.exit(1);
        }
        String inputFile = args[0];

        // 入力ファイルからシミュレーションのパラメータとプレイヤーの情報を読み込む
        try (Scanner sc = new Scanner(new File(inputFile))) {
            // 入力から各パラメータを取得する
            int R = sc.nextInt();  // 東西方向の最大値（x座標の上限）
            int T = sc.nextInt();  // 南北方向の最大値（y座標の上限）
            int N = sc.nextInt();  // シミュレーションの時間制限（ステップ数）
            int O = sc.nextInt();  // 鬼（Tagger）の数
            int C = sc.nextInt();  // 子（Child）の数

            // 盤面の作成
            Board board = new Board(R, T);
            // 全プレイヤー（Tagger と Child）のリストを作成する
            List<Player> players = new ArrayList<>();

            // 鬼（Tagger）の初期化
            for (int i = 0; i < O; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                players.add(new Tagger(x, y, pattern));
            }

            // 子（Child）の初期化
            for (int i = 0; i < C; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                players.add(new Child(x, y, pattern));
            }

            // シミュレーション開始前に、子がいない場合はゲーム終了
            int step = 0;
            boolean gameEnded = (C == 0);
            System.out.println("step " + step);
            logStatus(players);

            // シミュレーションループ
            while (!gameEnded && step < N) {
                // 各プレイヤーの「移動前の位置」を配列にコピー（ディープコピー相当）
                // ※ 各プレイヤーの現在位置（getX(), getY()）の値を primitive でコピー
                int playerCount = players.size();
                int[] prevXs = new int[playerCount];
                int[] prevYs = new int[playerCount];
                for (int i = 0; i < playerCount; i++) {
                    Player p = players.get(i);
                    prevXs[i] = p.getX();
                    prevYs[i] = p.getY();
                }

                // 各プレイヤーの移動を実施（同時移動をシミュレート）
                for (Player p : players) {
                    p.move(board, players);
                }

                // 衝突判定（同一領域にいる場合）
                for (Player p : players) {
                    if (p instanceof Tagger) {
                        for (Player q : players) {
                            if (q instanceof Child && p.getX() == q.getX() && p.getY() == q.getY()) {
                                q.setCaptured(true);
                            }
                        }
                    }
                }

                // すれ違い判定の実装：
                // Tagger（鬼）とChild（子）のそれぞれの「前の位置」と「移動後の位置」を比較する。
                // すれ違い判定の条件：
                //  鬼の前の位置 == 子の移動後の位置 かつ
                //  鬼の移動後の位置 == 子の前の位置
                for (int i = 0; i < playerCount; i++) {
                    Player p = players.get(i);
                    if (!(p instanceof Tagger)) continue;
                    for (int j = 0; j < playerCount; j++) {
                        Player q = players.get(j);
                        if (!(q instanceof Child)) continue;
                        if (prevXs[i] == q.getX() && prevYs[i] == q.getY() &&
                                p.getX() == prevXs[j] && p.getY() == prevYs[j]) {
                            q.setCaptured(true);
                        }
                    }
                }

                // ステップ番号を更新し、状態を出力
                step++;
                System.out.println("step " + step);
                logStatus(players);

                // 勝敗判定：全ての子が捕まったら鬼の勝ちとして終了
                if (players.stream().filter(p -> p instanceof Child).allMatch(Player::isCaptured)) {
                    gameEnded = true;
                }
            }
            // シミュレーション終了時の勝敗出力
            if (!gameEnded) {
                System.out.println("C"); // 規定時間までに捕まらなかったので子の勝ち
            } else {
                System.out.println("O"); // 鬼の勝ち
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * プレイヤーの現在の状態を標準出力に出力します。
     * Tagger（鬼）の座標を先に出力し、続いて Child（子）の座標または "captured" の文字列を出力します。
     *
     * @param players プレイヤーのリスト
     */
    private static void logStatus(List<Player> players) {
        StringBuilder output = new StringBuilder();

        // まず Tagger の状態を出力
        for (Player p : players) {
            if (p instanceof Tagger) {
                output.append(p.getX()).append(" ").append(p.getY()).append("\n");
            }
        }

        // 次に Child の状態を出力（捕まっている場合は "captured" と出力）
        for (Player p : players) {
            if (p instanceof Child) {
                if (p.isCaptured()) {
                    output.append("captured\n");
                } else {
                    output.append(p.getX()).append(" ").append(p.getY()).append("\n");
                }
            }
        }
        System.out.print(output.toString());
    }
}
