import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    private static final String[] TEST_CASES = {
            "sample0-0", "sample0-1", "sample0-2", "sample0-3",
            "sample1-0", "sample1-1", "sample1-2", "sample1-3",
            "sample2-0", "sample2-1",
            "sample3-0", "sample3-1",
            "sample4-0", "sample4-1"
    };

    private static int passedCount = 0;
    private static int failedCount = 0;
    private static final List<String> failedTests = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("\n==================== 🏁 TEST RUNNER 🏁 ====================\n");
        for (String baseName : TEST_CASES) {
            String inputFile = baseName + ".txt";
            String expectFile = baseName + "_output.txt";

            List<String> actualOutput;
            try {
                actualOutput = runProgramAndGetOutput("Main5", inputFile);
            } catch (IOException | InterruptedException e) {
                System.err.println("❌ テスト実行中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            List<String> expectedOutput = readAllLines(expectFile);

            boolean result = compareLines(expectedOutput, actualOutput);

            if (result) {
                System.out.println(baseName + ".txt ✅ passed!");
                passedCount++;
            } else {
                System.out.println(baseName + ".txt ❌ failed...");
                failedTests.add(baseName);
                failedCount++;
            }
        }

        // 最終結果の表示
        System.out.println("\n==================== 📊 TEST SUMMARY 📊 ====================");
        System.out.printf("✅ PASSED: %d\n", passedCount);
        System.out.printf("❌ FAILED: %d\n", failedCount);
        System.out.printf("🏆 Success Rate: %.2f%%\n", (passedCount * 100.0 / TEST_CASES.length));

//        if (!failedTests.isEmpty()) {
//            System.out.println("\n==================== ❌ FAILED TEST DETAILS ❌ ====================");
//            for (String failedTest : failedTests) {
//                System.out.println("🔹 Test: " + failedTest + ".txt");
//                List<String> actualOutput = readAllLines(failedTest + ".txt");
//                List<String> expectedOutput = readAllLines(failedTest + "_output.txt");
//                compareLinesVerbose(expectedOutput, actualOutput);
//            }
//        }
    }

    private static List<String> runProgramAndGetOutput(String mainClass, String inputFile)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", mainClass, inputFile);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        List<String> outputLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                outputLines.add(line);
            }
        }
        process.waitFor();
        return outputLines;
    }

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

    private static boolean compareLines(List<String> expected, List<String> actual) {
        return expected.equals(actual);
    }

    private static void compareLinesVerbose(List<String> expected, List<String> actual) {
        if (expected.size() != actual.size()) {
            System.out.printf("⚠️ 行数が一致しません (expected=%d, actual=%d)\n",
                    expected.size(), actual.size());
        }

        for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) {
            String eLine = expected.get(i);
            String aLine = actual.get(i);
            if (!eLine.equals(aLine)) {
                System.out.printf("⚠️ 行 %d が不一致:\n  expected: \"%s\"\n  actual:   \"%s\"\n", i + 1, eLine, aLine);
            }
        }
    }
}
