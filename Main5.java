import utils.Board;
import utils.Child;
import utils.Tagger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main5 {
    public static void main(String[] args) {
        // 引数の数が1でなければ使用方法を表示して終了する
        if (args.length != 1) {
            System.err.println("Usage: java Main5 <input-file>");
            System.exit(1);
        }
        String inputFile = args[0];
        String outputFile = inputFile + ".log";

        // 入力ファイルと出力ファイルを同時にオープンする
        try (Scanner sc = new Scanner(new File(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            // 入力から各パラメータを取得する
            int R = sc.nextInt();
            int T = sc.nextInt();
            int N = sc.nextInt();
            int O = sc.nextInt();
            int C = sc.nextInt();


            Board board = new Board(R, T);

            List<Tagger> taggers = new ArrayList<>();
            List<Child> children = new ArrayList<>();
            for (int i = 0; i < O; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                taggers.add(new Tagger(x, y, pattern));
            }
            for (int i = 0; i < C; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int pattern = sc.nextInt();
                children.add(new Child(x, y, pattern));
            }

            int step = 0;
            boolean gameEnded = (C == 0);
            writer.println("step " + step);
            logStatus(taggers, children, writer);

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

                // 各プレイヤーの nearestOpponent を更新
                for (Tagger t : taggers) {
                    t.findNearestOpponent(children);
                }
                for (Child c : children) {
                    c.findNearestOpponent(taggers);
                }

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

                // 衝突判定
                for (Tagger t : taggers) {
                    for (Child c : children) {
                        if (!c.isCaptured() && t.getX() == c.getX() && t.getY() == c.getY()) {
                            c.setCaptured(true);
                        }
                    }
                }

                // すれ違い判定
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

                step++;
                writer.println("step " + step);
                logStatus(taggers, children, writer);

                // 勝敗判定
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
                writer.println("C");
            } else {
                writer.println("O");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 鬼と子供の状態をファイルに出力する。
     */
    private static void logStatus(List<Tagger> taggers, List<Child> children, PrintWriter writer) {
        StringBuilder output = new StringBuilder();

        for (Tagger t : taggers) {
            output.append(t.getX()).append(" ").append(t.getY()).append("\n");
        }

        for (Child c : children) {
            if (c.isCaptured()) {
                output.append("captured\n");
            } else {
                output.append(c.getX()).append(" ").append(c.getY()).append("\n");
            }
        }
        writer.print(output.toString());
    }
}
