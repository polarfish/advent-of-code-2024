import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        long result = calculateStones(parseInput(input), 25);

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        long result = calculateStones(parseInput(input), 75);

        return String.valueOf(result);
    }

    long[] parseInput(String input) {
        return Arrays.stream(input.split(" ")).mapToLong(Long::parseLong).toArray();
    }

    private long calculateStones(long[] stones, int steps) {
        Map<Long, Long> frequency = Arrays.stream(stones).boxed()
            .collect(Collectors.toMap(Function.identity(), l1 -> 1L));
        for (int i = 0; i < steps; i++) {
            Map<Long, Long> newFrequency = new HashMap<>(frequency.size() * 2);
            for (Entry<Long, Long> stone : frequency.entrySet()) {
                long num = stone.getKey();
                long count = stone.getValue();
                long[] split;
                if (num == 0L) {
                    newFrequency.merge(1L, count, Long::sum);
                } else if ((split = split(num)) != null) {
                    newFrequency.merge(split[0], count, Long::sum);
                    newFrequency.merge(split[1], count, Long::sum);
                } else {
                    newFrequency.merge(num * 2024, count, Long::sum);
                }
            }
            frequency = newFrequency;
        }
        return frequency.values().stream().mapToLong(Long::longValue).sum();
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
