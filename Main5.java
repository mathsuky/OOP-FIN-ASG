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
            // シミュレーションループ
            int step = 0;
            boolean gameEnded = (C == 0);
            System.out.println("step " + step);
            logStatus(players);
            while (!gameEnded && step < N) {
                // 移動後の状態を出力する
                step++;

                // 各プレイヤーの移動を実施（同時移動をシミュレートするために各プレイヤーで move() を呼ぶ）
                // ※ すれ違い判定や衝突判定などが必要な場合、移動前の状態の記録が必要です
                for (Player p : players) {
                    p.move(board, players);
                }

                System.out.println("step " + step);
                logStatus(players);


                // 勝敗判定（ここでは全プレイヤーが捕まった場合を鬼の勝ちとする）
                if (players.stream().allMatch(Player::isCaptured)) {
                    gameEnded = true;
                }
            }
            // シミュレーションが終了した時点で、勝敗を出力する
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

        // 出力が空でない場合のみ出力
        if (output.length() > 0) {
            System.out.print(output.toString());
        }
    }
}
