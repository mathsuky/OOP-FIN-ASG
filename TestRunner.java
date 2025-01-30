import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * テスト実行用クラス
 *
 * 動作概要:
 *  - sampleX-y.txt (入力) と sampleX-y_output.txt (期待する出力) をペアとして扱う。
 *  - それぞれに対し Main5 をサブプロセスで実行し、その標準出力を取得して期待出力と比較する。
 *  - 比較結果を「PASSED」「FAILED」として標準出力に表示する。
 */
public class TestRunner {

    // 実際にテストしたいファイル名（拡張子を除く部分）をリストアップする
    // 例: sample0-0, sample0-1, sample1-0, ...
    private static final String[] TEST_CASES = {
            "sample0-0",
            "sample0-1",
            "sample0-2",
            "sample1-0",
            "sample1-1",
            "sample1-2",
            "sample1-3",
            "sample2-0",
            "sample2-1",
            "sample3-0",
            "sample3-1",
            "sample4-0",
            "sample4-1"
    };

    public static void main(String[] args) {
        for (String baseName : TEST_CASES) {
            String inputFile  = baseName + ".txt";
            String expectFile = baseName + "_output.txt";

            System.out.println("=== Testing " + inputFile + " ===");

            // Main5 をサブプロセスとして実行し、標準出力を文字列として受け取る
            List<String> actualOutput;
            try {
                actualOutput = runProgramAndGetOutput("Main5", inputFile);
            } catch (IOException | InterruptedException e) {
                System.err.println("テスト実行中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            // 期待する出力ファイルを行単位で読み込み
            List<String> expectedOutput = readAllLines(expectFile);

            // 比較
            boolean result = compareLines(expectedOutput, actualOutput);

            // 結果表示
            if (result) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED");
                // 不一致の場合、デバッグしやすいよう差分表示などを行ってもよい
            }
            System.out.println();
        }
    }

    /**
     * 指定したクラス名(Main5)・引数(inputFile)でサブプロセスを起動し、その標準出力を行ごとに取得する。
     * 実行可能なクラスパスの設定は環境に合わせて調整が必要。
     */
    private static List<String> runProgramAndGetOutput(String mainClass, String inputFile)
            throws IOException, InterruptedException {
        // 実行コマンド: "java Main5 sampleX-y.txt"
        // ClassPath等は環境に応じて修正のこと
        ProcessBuilder pb = new ProcessBuilder(
                "java", mainClass, inputFile
        );

        // 実行ディレクトリを設定する場合はここで setDirectory する
        // pb.directory(new File("bin"));

        pb.redirectErrorStream(true); // 標準エラーを標準出力にマージ
        Process process = pb.start();

        // 標準出力を行単位で取得
        List<String> outputLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                outputLines.add(line);
            }
        }

        // サブプロセスの終了を待機
        process.waitFor();

        return outputLines;
    }

    /**
     * 指定したファイルを行単位ですべて読み込み、リストにして返す。
     * ファイルが存在しない場合は空のリストを返す。
     */
    private static List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        File f = new File(fileName);

        if (!f.exists()) {
            // 存在しない場合は空のリストにして返す
            System.err.println("期待ファイルが見つかりません: " + fileName);
            return lines;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("ファイル読み込み中にエラー: " + e.getMessage());
        }

        return lines;
    }

    /**
     * 2つの文字列リストを比較し、すべての行が完全に一致すれば true を返す。
     * 行数や内容に差があれば false を返す。
     */
    private static boolean compareLines(List<String> expected, List<String> actual) {
        if (expected.size() != actual.size()) {
            System.out.printf("行数が一致しません (expected=%d, actual=%d)%n",
                    expected.size(), actual.size());
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            String eLine = expected.get(i);
            String aLine = actual.get(i);
            if (!eLine.equals(aLine)) {
                System.out.printf("行 %d が不一致:%n  expected: \"%s\"%n  actual:   \"%s\"%n", i+1, eLine, aLine);
                return false;
            }
        }
        return true;
    }
}
