import java.util.stream.IntStream;

public class Day2 extends Day {

    public static void main(String[] args) {
        Day2 day = new Day2();  // https://adventofcode.com/2024/day/0

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(2, day.part1(sample));
        assertEquals(334, day.part1(full));

        assertEquals(4, day.part2(sample));
        assertEquals(400, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        int safeReports = input.lines()
            .map(line -> line.split(" "))
            .mapToInt(split -> isReportGood(split, -1) ? 1 : 0)
            .sum();

        return String.valueOf(safeReports);
    }

    @Override
    public String part2(String input) {
        int safeReports = input.lines()
            .map(line -> line.split(" "))
            .mapToInt(split ->
                IntStream.range(-1, split.length)
                    .filter(elementToSkip -> isReportGood(split, elementToSkip))
                    .findFirst()
                    .isPresent() ? 1 : 0).sum();

        return String.valueOf(safeReports);
    }

    private static boolean isReportGood(String[] split, int elementToSkip) {
        int prev;
        int value = -1;
        int total = 0;
        for (int i = 0; i < split.length; i++) {
            if (i == elementToSkip) {
                continue;
            }
            prev = value;
            value = Integer.parseInt(split[i]);
            if (prev == -1) {
                continue;
            }
            int delta = value - prev;
            int deltaAbs = Math.abs(value - prev);
            if (deltaAbs < 1 || deltaAbs > 3) {
                break;
            }
            total += Integer.signum(delta);
        }

        return Math.abs(total) == split.length - 1 - (elementToSkip == -1 ? 0 : 1);
    }
}
