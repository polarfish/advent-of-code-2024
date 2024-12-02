import java.util.Arrays;

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
        long safeReports = input.lines()
            .map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray())
            .filter(levels -> findBadLevel(levels, -1) == -1)
            .count();

        return String.valueOf(safeReports);
    }

    @Override
    public String part2(String input) {
        long safeReports = input.lines()
            .map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray())
            .filter(levels -> {
                int badLevel = findBadLevel(levels, -1);
                return badLevel == -1
                    || findBadLevel(levels, badLevel - 2) == -1
                    || findBadLevel(levels, badLevel - 1) == -1
                    || findBadLevel(levels, badLevel) == -1;
            })
            .count();

        return String.valueOf(safeReports);
    }

    private int findBadLevel(int[] levels, int levelToSkip) {
        int prev;
        int value = -1;
        int totalIncreasing = 0;
        int totalDecreasing = 0;
        for (int i = 0; i < levels.length; i++) {
            if (i == levelToSkip) {
                continue;
            }
            prev = value;
            value = levels[i];
            if (prev == -1) {
                continue;
            }

            int delta;
            if (value > prev) {
                totalIncreasing++;
                delta = value - prev;
                if (totalDecreasing > 0) {
                    return i;
                }
            } else if (value < prev) {
                totalDecreasing++;
                delta = prev - value;
                if (totalIncreasing > 0) {
                    return i;
                }
            } else {
                return i;
            }

            if (delta < 1 || delta > 3) {
                return i;
            }
        }

        return -1;
    }
}
