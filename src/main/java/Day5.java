import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
        List<List<Integer>> updates = new ArrayList<>();
        extractInput(input, rules, updates);
        Comparator<Integer> pagesComparator = (i1, i2) -> rules.contains(createRuleId(i2, i1)) ? 1 : -1;
        int result = updates.stream()
            .filter(pages -> isSorted(pages, pagesComparator))
            .mapToInt(this::getMiddlePage)
            .sum();
        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Set<Integer> rules = new HashSet<>();
        List<List<Integer>> updates = new ArrayList<>();
        extractInput(input, rules, updates);
        Comparator<Integer> pagesComparator = (i1, i2) -> rules.contains(createRuleId(i2, i1)) ? 1 : -1;
        int result = updates.stream()
            .filter(pages -> !isSorted(pages, pagesComparator))
            .map(pages -> pages.stream().sorted(pagesComparator).toList())
            .mapToInt(this::getMiddlePage)
            .sum();
        return String.valueOf(result);
    }

    private void extractInput(String input, Set<Integer> rules, List<List<Integer>> updates) {
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
                updates.add(Arrays.stream(split).map(Integer::valueOf).toList());
            }
        });
    }

    private int createRuleId(int beforePage, int afterPage) {
        return beforePage * 100 + afterPage;
    }

    private boolean isSorted(List<Integer> pages, Comparator<Integer> pagesComparator) {
        Iterator<Integer> iter = pages.iterator();
        Integer current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            if (pagesComparator.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    private Integer getMiddlePage(List<Integer> pages) {
        return pages.get(pages.size() / 2);
    }
}
