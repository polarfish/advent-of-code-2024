import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day1 extends Day {

    public static void main(String[] args) {
        Day1 day = new Day1();  // https://adventofcode.com/2024/day/1

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(11, day.part1(sample));
        assertEquals(2430334, day.part1(full));

        assertEquals(31, day.part2(sample));
        assertEquals(28786472, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        input.lines().forEach(line -> {
            String[] split = line.split(" {3}");
            left.add(Integer.valueOf(split[0]));
            right.add(Integer.valueOf(split[1]));
        });

        Collections.sort(left);
        Collections.sort(right);

        int total = 0;
        for (int i = 0; i < left.size(); i++) {
            total += Math.abs(left.get(i) - right.get(i));
        }

        return String.valueOf(total);
    }

    @Override
    public String part2(String input) {
        List<Integer> left = new ArrayList<>();
        Map<Integer, Integer> right = new HashMap<>();
        input.lines().forEach(line -> {
            String[] split = line.split(" {3}");
            left.add(Integer.valueOf(split[0]));
            right.merge(Integer.valueOf(split[1]), 1, Integer::sum);
        });

        int result = left.stream().mapToInt(i -> i * right.getOrDefault(i, 0)).sum();

        return String.valueOf(result);
    }
}
