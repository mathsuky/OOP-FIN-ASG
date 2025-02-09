import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * テストケースを実行し、結果と差分情報を標準出力に表示するテストランナーです。
 */
public class TestRunner {

    // テストケースのベース名（各テストは sampleX-y.txt と sampleX-y_output.txt のペア）
    private static final String[] TEST_CASES = {
            "sample0-0", "sample0-1", "sample0-2", "sample0-3",
            "sample1-0", "sample1-1", "sample1-2", "sample1-3",
            "sample2-0", "sample2-1",
            "sample3-0", "sample3-1",
            "sample4-0", "sample4-1"
    };

    private static int passedCount = 0;
    private static int failedCount = 0;

    // 失敗したテストケースの詳細情報を保持するリスト
    private static final List<FailedTest> failedTests = new ArrayList<>();

    /**
     * 失敗テストの詳細を保持するための内部クラス
     */
    private static class FailedTest {
        String baseName;
        List<String> expectedOutput;
        List<String> actualOutput;

        FailedTest(String baseName, List<String> expectedOutput, List<String> actualOutput) {
            this.baseName = baseName;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
        }
    }

    public static void main(String[] args) {
        System.out.println("\n==================== 🏁 TEST RUNNER 🏁 ====================\n");
        for (String baseName : TEST_CASES) {
            // 入力ファイル名は sampleX-y.txt
            String inputFile = baseName + ".txt";
            // 期待出力ファイルは sampleX-y_output.txt
            String expectFile = baseName + "_output.txt";
            // 実際の出力は Main5 が出力するログファイル sampleX-y.txt.log とする
            String actualOutputFile = inputFile + ".log";

            try {
                // Main5 を実行して、出力ファイルが作成されるのを待つ
                runProgram("Main5", inputFile);
            } catch (IOException | InterruptedException e) {
                System.err.println("❌ テスト実行中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            // 出力ファイルと期待ファイルの内容をそれぞれ読み込む
            List<String> actualOutput = readAllLines(actualOutputFile);
            List<String> expectedOutput = readAllLines(expectFile);

            boolean result = compareLines(expectedOutput, actualOutput);
            if (result) {
                System.out.println(baseName + ".txt ✅ passed!");
                passedCount++;
            } else {
                System.out.println(baseName + ".txt ❌ failed...");
                // 失敗テストの詳細を保持する
                failedTests.add(new FailedTest(baseName, expectedOutput, actualOutput));
                failedCount++;
            }
        }

        // 失敗したテストケースの差分詳細を出力
        if (!failedTests.isEmpty()) {
            System.out.println("\n==================== ❌ FAILED TEST DETAILS ❌ ====================");
            for (FailedTest ft : failedTests) {
                System.out.println("🔹 Test: " + ft.baseName + ".txt");
                compareLinesVerbose(ft.expectedOutput, ft.actualOutput);
                System.out.println(); // テスト間に空行を入れる
            }
        }

        // テスト結果のサマリを出力
        System.out.println("\n==================== 📊 TEST SUMMARY 📊 ====================");
        System.out.printf("✅ PASSED: %d\n", passedCount);
        System.out.printf("❌ FAILED: %d\n", failedCount);
        System.out.printf("🏆 Success Rate: %.2f%%\n", (passedCount * 100.0 / TEST_CASES.length));
    }

    /**
     * 指定したメインクラスと入力ファイルでプログラムを実行する。
     * 実行後、プログラムが出力するログファイル（"filename.txt.log"）が生成されるものとする。
     */
    private static void runProgram(String mainClass, String inputFile)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", mainClass, inputFile);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();
    }

    /**
     * 指定したファイルを行単位で読み込み、リストとして返す。
     */
    private static List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        File f = new File(fileName);
        if (!f.exists()) {
            System.err.println("⚠️ 期待ファイルが見つかりません: " + fileName);
            return lines;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("⚠️ ファイル読み込み中にエラー: " + e.getMessage());
        }
        return lines;
    }

    /**
     * 期待出力と実際出力のリストが完全に一致すれば true を返す。
     */
    private static boolean compareLines(List<String> expected, List<String> actual) {
        return expected.equals(actual);
    }

    /**
     * 期待出力と実際出力の各行を比較し、
     * 差分が発見された場合はその最初の一箇所のみ詳細を出力する。
     */
    private static void compareLinesVerbose(List<String> expected, List<String> actual) {
        int max = Math.min(expected.size(), actual.size());
        for (int i = 0; i < max; i++) {
            if (!expected.get(i).equals(actual.get(i))) {
                System.out.printf("⚠️ 行 %d が不一致:\n  expected: \"%s\"\n  actual:   \"%s\"\n",
                        i + 1, expected.get(i), actual.get(i));
                return;
            }
        }
        if (expected.size() != actual.size()) {
            System.out.printf("⚠️ 行数が一致しません (expected=%d, actual=%d)\n",
                    expected.size(), actual.size());
        }
    }
}
