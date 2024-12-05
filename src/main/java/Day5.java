import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day5 extends Day {

    public static void main(String[] args) {
        Day5 day = new Day5();  // https://adventofcode.com/2024/day/5

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(143, day.part1(sample));
        assertEquals(5713, day.part1(full));

        assertEquals(123, day.part2(sample));
        assertEquals(5180, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Set<Integer> rules = new HashSet<>();
        List<int[]> updates = new ArrayList<>();
        extractInput(input, rules, updates);

        int result = 0;
        for (int[] u : updates) {
            boolean inOrder = true;
            top:
            for (int i = 0; i < u.length - 1; i++) {
                for (int j = i + 1; j < u.length; j++) {
                    if (rules.contains(createRuleId(u[j], u[i]))) {
                        inOrder = false;
                        break top;
                    }
                }
            }
            if (inOrder) {
                result += u[u.length / 2];
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Set<Integer> rules = new HashSet<>();
        List<int[]> updates = new ArrayList<>();
        extractInput(input, rules, updates);

        int result = 0;
        for (int[] u : updates) {
            boolean inOrder = true;
            for (int i = 0; i < u.length - 1; i++) {
                for (int j = i + 1; j < u.length; j++) {
                    if (rules.contains(createRuleId(u[j], u[i]))) {
                        inOrder = false;
                        int b = u[j];
                        u[j] = u[i];
                        u[i] = b;
                    }
                }
            }
            if (!inOrder) {
                result += u[u.length / 2];
            }
        }

        return String.valueOf(result);
    }

    private void extractInput(String input, Set<Integer> rules, List<int[]> updates) {
        final boolean[] rulesFinished = {false};
        input.lines().forEach(line -> {
            if (line.isEmpty()) {
                rulesFinished[0] = true;
                return;
            }

            if (!rulesFinished[0]) {
                String[] split = line.split("\\|");
                rules.add(createRuleId(parseInt(split[0]), parseInt(split[1])));
            } else {
                String[] split = line.split(",");
                updates.add(Arrays.stream(split).mapToInt(Integer::parseInt).toArray());
            }
        });
    }

    private int createRuleId(int beforePage, int afterPage) {
        return beforePage * 100 + afterPage;
    }
}
