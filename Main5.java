import utils.Board;
import utils.Child;
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

        try (Scanner sc = new Scanner(new File(inputFile))) {
            // 入力から各パラメータを取得する
            int R = sc.nextInt();  // 東西方向の最大値（x座標の上限）
            int T = sc.nextInt();  // 南北方向の最大値（y座標の上限）
            int N = sc.nextInt();  // シミュレーションの時間制限（ステップ数）
            int O = sc.nextInt();  // 鬼（Tagger）の数
            int C = sc.nextInt();  // 子（Child）の数

            // 盤面の作成
            Board board = new Board(R, T);
            // 鬼と子供を別々のリストで管理する
            List<Tagger> taggers = new ArrayList<>();
            List<Child> children = new ArrayList<>();

            // 鬼（Tagger）の初期化
            for (int i = 0; i < O; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                taggers.add(new Tagger(x, y, pattern));
            }
            // 子（Child）の初期化
            for (int i = 0; i < C; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                children.add(new Child(x, y, pattern));
            }

            // シミュレーション開始前の状態出力
            int step = 0;
            boolean gameEnded = (C == 0);
            System.out.println("step " + step);
            logStatus(taggers, children);

            // シミュレーションループ
            while (!gameEnded && step < N) {
                // 各プレイヤーの「移動前の位置」を保持する（後のすれ違い判定用）
                int taggerCount = taggers.size();
                int childCount = children.size();
                int[] prevTaggerXs = new int[taggerCount];
                int[] prevTaggerYs = new int[taggerCount];
                for (int i = 0; i < taggerCount; i++) {
                    Tagger t = taggers.get(i);
                    prevTaggerXs[i] = t.getX();
                    prevTaggerYs[i] = t.getY();
                }
                int[] prevChildXs = new int[childCount];
                int[] prevChildYs = new int[childCount];
                for (int i = 0; i < childCount; i++) {
                    Child c = children.get(i);
                    prevChildXs[i] = c.getX();
                    prevChildYs[i] = c.getY();
                }

                for (Tagger t : taggers) {
                    t.findNearestOpponent(children);
                }

                for (Child c : children) {
                    c.findNearestOpponent(taggers);
                }

                // 各プレイヤーの移動（捕まっていない場合のみ）
                for (Tagger t : taggers) {
                    if (!t.isCaptured()) {
                        t.move(board, children);
                    }
                }
                for (Child c : children) {
                    if (!c.isCaptured()) {
                        c.move(board, taggers);
                    }
                }

                // 衝突判定：各鬼と各子供の座標が一致すれば、その子供は捕まる
                for (Tagger t : taggers) {
                    for (Child c : children) {
                        if (!c.isCaptured() && t.getX() == c.getX() && t.getY() == c.getY()) {
                            c.setCaptured(true);
                        }
                    }
                }

                // すれ違い判定：移動前後で鬼と子供が入れ替わっていれば捕獲
                for (int i = 0; i < taggerCount; i++) {
                    Tagger t = taggers.get(i);
                    for (int j = 0; j < childCount; j++) {
                        Child c = children.get(j);
                        if (!c.isCaptured() &&
                                prevTaggerXs[i] == c.getX() && prevTaggerYs[i] == c.getY() &&
                                t.getX() == prevChildXs[j] && t.getY() == prevChildYs[j]) {
                            c.setCaptured(true);
                        }
                    }
                }

                // ステップ番号更新と状態出力
                step++;
                System.out.println("step " + step);
                logStatus(taggers, children);

                // 勝敗判定：全ての子供が捕まっていれば鬼の勝ち
                boolean allCaptured = true;
                for (Child c : children) {
                    if (!c.isCaptured()) {
                        allCaptured = false;
                        break;
                    }
                }
                if (allCaptured) {
                    gameEnded = true;
                }
            }

            // シミュレーション終了時の勝敗出力
            if (!gameEnded) {
                System.out.println("C"); // 規定時間までに捕まらなかったので子供の勝ち
            } else {
                System.out.println("O"); // 鬼の勝ち
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 鬼と子供の状態を出力する。
     * 鬼の座標を先に出力し、次に子供については捕まっている場合 "captured"、
     * そうでなければ座標を出力する。
     */
    private static void logStatus(List<Tagger> taggers, List<Child> children) {
        StringBuilder output = new StringBuilder();

        // 鬼の状態出力
        for (Tagger t : taggers) {
            output.append(t.getX()).append(" ").append(t.getY()).append("\n");
        }

        // 子供の状態出力
        for (Child c : children) {
            if (c.isCaptured()) {
                output.append("captured\n");
            } else {
                output.append(c.getX()).append(" ").append(c.getY()).append("\n");
            }
        }
        System.out.print(output.toString());
    }
}
