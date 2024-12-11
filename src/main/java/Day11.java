import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 extends Day {

    public static void main(String[] args) {
        Day11 day = new Day11();  // https://adventofcode.com/2024/day/11

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(55312, day.part1(sample));
        assertEquals(217812, day.part1(full));

        assertEquals(65601038650482L, day.part2(sample));
        assertEquals(259112729857522L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        long result = Arrays.stream(input.split(" "))
            .mapToLong(Long::parseLong)
            .map(l -> calculateStones(l, 25))
            .sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        long result = Arrays.stream(input.split(" "))
            .mapToLong(Long::parseLong)
            .map(l -> calculateStones(l, 75))
            .sum();

        return String.valueOf(result);
    }

    private long calculateStones(long num, int steps) {
        return calculateStones(num, steps, initMemo(steps));
    }

    private long calculateStones(long num, int steps, Map<Integer, Map<Long, Long>> memo) {
        if (steps == 0) {
            return 1;
        }

        Long cachedResult = memo.get(steps).get(num);
        if (cachedResult != null) {
            return cachedResult;
        }

        long result;
        long[] split;
        if (num == 0) {
            result = calculateStones(1, steps - 1, memo);
        } else if ((split = split(num)) != null) {
            result = calculateStones(split[0], steps - 1, memo) + calculateStones(split[1], steps - 1, memo);
        } else {
            result = calculateStones(num * 2024, steps - 1, memo);
        }

        memo.get(steps).put(num, result);
        return result;
    }

    private static Map<Integer, Map<Long, Long>> initMemo(int steps) {
        return IntStream.rangeClosed(1, steps).boxed()
            .collect(Collectors.toMap(Function.identity(), i -> new HashMap<>()));
    }

    private long[] split(long l) {
        long buf = l;
        int digits = 0;
        while (buf > 0) {
            digits++;
            buf /= 10;
        }

        if (digits % 2 == 0) {
            long multiplier = pow(10, digits / 2, 1);
            return new long[]{l / multiplier, l % multiplier};
        }
        return null;
    }

    private long pow(long n, long p, long accumulator) {
        if (p == 0) {
            return accumulator;
        } else {
            return pow(n, p - 1, accumulator * n);
        }
    }
}
