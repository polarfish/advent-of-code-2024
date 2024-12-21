import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 extends Day {

    public static void main(String[] args) {
        Day21 day = new Day21();  // https://adventofcode.com/2024/day/21

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(126384, day.part1(sample));
        assertEquals(156714, day.part1(full));

        assertEquals(154115708116294L, day.part2(sample));
        assertEquals(191139369248202L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    private static final Map<Character, List<List<Character>>> N_PAD = new HashMap<>() {{
        put('0', List.of(List.of('2', '^'), List.of('A', '>')));
        put('1', List.of(List.of('2', '>'), List.of('4', '^')));
        put('2', List.of(List.of('0', 'v'), List.of('1', '<'), List.of('3', '>'), List.of('5', '^')));
        put('3', List.of(List.of('2', '<'), List.of('6', '^'), List.of('A', 'v')));
        put('4', List.of(List.of('1', 'v'), List.of('5', '>'), List.of('7', '^')));
        put('5', List.of(List.of('2', 'v'), List.of('4', '<'), List.of('6', '>'), List.of('8', '^')));
        put('6', List.of(List.of('3', 'v'), List.of('5', '<'), List.of('9', '^')));
        put('7', List.of(List.of('4', 'v'), List.of('8', '>')));
        put('8', List.of(List.of('5', 'v'), List.of('7', '<'), List.of('9', '>')));
        put('9', List.of(List.of('6', 'v'), List.of('8', '<')));
        put('A', List.of(List.of('0', '<'), List.of('3', '^')));
    }};

    private static final Map<Character, List<List<Character>>> D_PAD = new HashMap<>() {{
        put('^', List.of(List.of('A', '>'), List.of('v', 'v')));
        put('>', List.of(List.of('A', '^'), List.of('v', '<')));
        put('v', List.of(List.of('^', '^'), List.of('>', '>'), List.of('<', '<')));
        put('<', List.of(List.of('v', '>')));
        put('A', List.of(List.of('>', 'v'), List.of('^', '<')));
    }};

    private static final List<Map<Character, List<List<Character>>>> PADS = List.of(N_PAD, D_PAD);


    @Override
    public String part1(String input) {
        Map<MemoKey, Long> memo = new HashMap<>();
        long result = input.lines().mapToLong(line -> {
            long size = translate(line, 2, 0, memo);
            int codeNum = Integer.parseInt(line.substring(0, 3));
            return size * codeNum;
        }).sum();
        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Map<MemoKey, Long> memo = new HashMap<>();
        long result = input.lines().mapToLong(line -> {
            long size = translate(line, 25, 0, memo);
            int codeNum = Integer.parseInt(line.substring(0, 3));
            return size * codeNum;
        }).sum();
        return String.valueOf(result);
    }

    private long translate(String seq, int level, int buttonsIndex, Map<MemoKey, Long> memo) {
        MemoKey memoKey = new MemoKey(seq, level);
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }

        Map<Character, List<List<Character>>> pad = PADS.get(buttonsIndex);
        long result = 0;
        seq = "A" + seq;

        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < seq.length() - 1; i++) {
            pairs.add(seq.substring(i, i + 2));
        }

        for (String pair : pairs) {
            List<String> paths = search(pair.charAt(0), pair.charAt(1), pad);
            if (level == 0) {
                result += paths.stream().mapToLong(String::length).min().orElse(0);
            } else {
                result += paths.stream().mapToLong(path -> translate(path, level - 1, 1, memo)).min().orElse(0L);
            }
        }

        memo.put(memoKey, result);

        return result;
    }

    record MemoKey(String seq, Integer level) {

    }

    private List<String> search(char from, char to, Map<Character, List<List<Character>>> pad) {
        Queue<Step> queue = new ArrayDeque<>();
        queue.add(new Step(from, Collections.emptyList()));
        Set<Character> visited = new HashSet<>();
        visited.add(from);
        List<String> result = new ArrayList<>();
        long shortest = -1;
        while (!queue.isEmpty()) {
            Step s = queue.poll();
            char curr = s.ch();
            List<Character> path = s.path();
            if (curr == to) {
                if (shortest == -1) {
                    shortest = path.size();
                }
                if (path.size() == shortest) {
                    result.add(path.stream().map(Object::toString).collect(Collectors.joining()) + "A");
                }
                continue;
            }
            if (shortest != -1 && path.size() >= shortest) {
                continue;
            }
            for (List<Character> transitions : pad.get(curr)) {
                char btn = transitions.get(0);
                char dir = transitions.get(1);
                if (visited.contains(btn)) {
                    continue;
                }
                List<Character> path2 = new ArrayList<>(path);
                path2.add(dir);
                queue.add(new Step(btn, path2));
            }
            visited.add(curr);
        }
        return result;
    }

    record Step(Character ch, List<Character> path) {

    }

}
