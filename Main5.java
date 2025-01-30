import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Main5 {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java Main5 <input-file>");
      return;
    }

    String inputFileName = args[0];
    String outputFileName = inputFileName + ".log";

    try (PrintStream out = new PrintStream(outputFileName)) {
      List<String> inputs = Files.readAllLines(Paths.get(inputFileName));
      for (String line : inputs) {
        out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}