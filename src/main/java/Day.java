import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

public abstract class Day {

    String name() {
        return getClass().getSimpleName();
    }

    long run() {
        return run(readFile("%s.txt".formatted(name())));
    }

    long run(String input) {
        System.out.printf("Running %s%n", getClass().getSimpleName());
        long time1 = run(input, this::part1, "Part 1 result");
        long time2 = run(input, this::part2, "Part 2 result");
        return time1 + time2;
    }

    long run(String input, Function<String, String> function, String label) {
        long start = System.currentTimeMillis();
        String res = function.apply(input);
        long time = System.currentTimeMillis() - start;
        System.out.printf("[%d ms] %s: %s%n", time, label, res);
        return time;
    }

    abstract String part1(String input);

    abstract String part2(String input);

    public static String readFile(String fileName) {
        try (InputStream is = Day.class.getResourceAsStream(fileName)) {
            Objects.requireNonNull(is, () -> "File %s not found".formatted(fileName));
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertEquals(long expected, String actual) {
        assertEquals(String.valueOf(expected), actual);
    }

    public static void assertEquals(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected: %s, actual: %s".formatted(expected, actual));
        }
    }
}
