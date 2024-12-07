import java.math.BigDecimal;
import java.util.Arrays;

public class Day7 extends Day {

    public static void main(String[] args) {
        Day7 day = new Day7();  // https://adventofcode.com/2024/day/7

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(3749, day.part1(sample));
        assertEquals(21572148763543L, day.part1(full));

        assertEquals(11387, day.part2(sample));
        assertEquals(581941094529163L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        long result = input.lines().mapToLong(line -> {
            String[] split = line.split(":");
            long expected = Long.parseLong(split[0]);
            int[] values = Arrays.stream(split[1].trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            return solve(expected, values[0], values, 1) ? expected : 0;
        }).sum();
        return String.valueOf(result);
    }

    private boolean solve(long expected, long current, int[] values, int i) {
        if (i == values.length) {
            return expected == current;
        }

        if (current >= expected) {
            return false;
        }

        return solve(expected, current + values[i], values, i + 1)
            || solve(expected, current * values[i], values, i + 1);
    }

    @Override
    public String part2(String input) {
        BigDecimal result = input.lines().map(line -> {
            String[] split = line.split(":");
            long expected = Long.parseLong(split[0]);
            BigDecimal[] values = Arrays.stream(split[1].trim().split(" ")).map(BigDecimal::new)
                .toArray(BigDecimal[]::new);
            return solve2(BigDecimal.valueOf(expected), values[0], values, 1) ? expected : 0;
        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        return result.toString();
    }

    private boolean solve2(BigDecimal expected, BigDecimal current, BigDecimal[] values, int i) {
        if (i == values.length) {
            return expected.equals(current);
        }

        if (current.compareTo(expected) > 0) {
            return false;
        }

        return solve2(expected, current.add(values[i]), values, i + 1)
            || solve2(expected, current.multiply(values[i]), values, i + 1)
            || solve2(expected, concat(current, values[i]), values, i + 1);
    }

    BigDecimal concat(BigDecimal a, BigDecimal b) {
        if (b.equals(BigDecimal.ZERO)) {
            a = a.multiply(BigDecimal.TEN);
        } else {
            BigDecimal tempB = b;
            while (tempB.compareTo(BigDecimal.ZERO) > 0) {
                tempB = tempB.divideToIntegralValue(BigDecimal.TEN);
                a = a.multiply(BigDecimal.TEN);
            }
        }

        return a.add(b);
    }
}
