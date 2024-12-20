import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day19 extends Day {

    public static void main(String[] args) {
        Day19 day = new Day19();  // https://adventofcode.com/2024/day/19

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(6, day.part1(sample));
        assertEquals(336, day.part1(full));

        assertEquals(16, day.part2(sample));
        assertEquals(758890600222015L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Day19Input in = parseInput(input);
        TrieNode root = new TrieNode(in.patterns());
        Map<String, Long> memo = new HashMap<>();
        long result = in.designs().stream()
            .mapToLong(design -> countCombinations(design, root, memo, true))
            .sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Day19Input in = parseInput(input);
        TrieNode root = new TrieNode(in.patterns());
        Map<String, Long> memo = new HashMap<>();
        long result = in.designs().stream()
            .mapToLong(design -> countCombinations(design, root, memo, false))
            .sum();

        return String.valueOf(result);
    }

    record Day19Input(List<String> patterns, List<String> designs) {

    }

    private Day19Input parseInput(String input) {
        String[] split = input.split("\n\n");
        return new Day19Input(
            Arrays.stream(split[0].split(", ")).toList(),
            Arrays.stream(split[1].split("\n")).toList()
        );
    }

    private long countCombinations(String design, TrieNode root, Map<String, Long> memo, boolean stopOnFirstResult) {
        if (design.isEmpty()) {
            return 1;
        }

        if (memo.containsKey(design)) {
            return memo.get(design);
        }

        long result = 0L;
        List<Integer> sizes = root.findStartPatternsSizes(design);
        for (Integer s : stopOnFirstResult ? sizes.reversed() : sizes) {
            result += countCombinations(design.substring(s), root, memo, stopOnFirstResult);
            if (result > 0 && stopOnFirstResult) {
                break;
            }
        }

        memo.put(design, result);
        return result;
    }


    public static class TrieNode {

        private final TrieNode[] children = new TrieNode[26];
        private boolean isWord;

        public TrieNode() {
        }

        public TrieNode(List<String> patterns) {
            patterns.forEach(this::insert);
        }

        public void insert(String word) {
            TrieNode current = this;

            for (int i = 0; i < word.length(); i++) {
                int ind = word.charAt(i) - 'a';
                if (current.children[ind] == null) {
                    current.children[ind] = new TrieNode();
                }
                current = current.children[ind];
            }
            current.isWord = true;
        }

        public List<Integer> findStartPatternsSizes(String design) {
            List<Integer> result = new ArrayList<>();
            TrieNode current = this;
            int size = 0;
            while (size < design.length() && (current = current.children[design.charAt(size) - 'a']) != null) {
                size++;
                if (current.isWord) {
                    result.add(size);
                }
            }
            return result;
        }
    }
}
