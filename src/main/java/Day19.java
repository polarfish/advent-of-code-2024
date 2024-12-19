import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        String[] split = input.split("\n\n");
        List<String> patterns = Arrays.stream(split[0].split(", ")).toList();
        List<String> designs = Arrays.stream(split[1].split("\n")).toList();

        TrieNode root = new TrieNode();
        patterns.forEach(root::insert);
        int[] sizeMap = new int[patterns.stream().mapToInt(String::length).max().orElse(0) + 1];
        Set<String> noMemo = new HashSet<>();

        long result = designs.stream()
            .filter(design -> isDesignPossible(design, 0, root, sizeMap, noMemo))
            .count();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        String[] split = input.split("\n\n");
        List<String> patterns = Arrays.stream(split[0].split(", ")).toList();
        List<String> designs = Arrays.stream(split[1].split("\n")).toList();

        TrieNode root = new TrieNode();
        patterns.forEach(root::insert);
        int[] sizeMap = new int[patterns.stream().mapToInt(String::length).max().orElse(0) + 1];
        Set<String> noMemo = new HashSet<>();
        Map<String, Long> yesMemo = new HashMap<>();

        long result = designs.stream()
            .mapToLong(design -> countCombinations(design, 0, root, sizeMap, noMemo, yesMemo))
            .sum();

        return String.valueOf(result);
    }

    private long countCombinations(String design, int offset, TrieNode root, int[] sizeMap, Set<String> noMemo,
        Map<String, Long> yesMemo) {
        if (design.length() == offset) {
            yesMemo.put(design, 1L);
            return 1;
        }
        String designPart = design.substring(offset);
        if (noMemo.contains(designPart)) {
            return 0;
        }
        if (yesMemo.containsKey(designPart)) {
            return yesMemo.get(designPart);
        }

        long result = 0L;

        int[] sizeMap2 = Arrays.copyOf(sizeMap, sizeMap.length);
        int biggestPatternSize = root.findStartingPatterns(design, offset, sizeMap2);

        for (int s = biggestPatternSize; s > 0; s--) {
            if (sizeMap2[s] == 1) {
                result += countCombinations(design, offset + s, root, sizeMap, noMemo, yesMemo);
            }
        }

        if (result == 0) {
            noMemo.add(designPart);
        } else {
            yesMemo.put(designPart, result);
        }

        return result;
    }

    private boolean isDesignPossible(String design, int offset, TrieNode root, int[] sizeMap, Set<String> noMemo) {
        if (design.length() == offset) {
            return true;
        }

        String designPart = design.substring(offset);
        if (noMemo.contains(designPart)) {
            return false;
        }

        int[] sizeMap2 = Arrays.copyOf(sizeMap, sizeMap.length);
        int biggestPatternSize = root.findStartingPatterns(design, offset, sizeMap2);

        for (int s = biggestPatternSize; s > 0; s--) {
            if (sizeMap2[s] == 1 && isDesignPossible(design, offset + s, root, sizeMap, noMemo)) {
                return true;
            }
        }

        noMemo.add(designPart);
        return false;
    }


    public static class TrieNode {

        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean isWord;

        public void insert(String word) {
            TrieNode current = this;

            for (int i = 0; i < word.length(); i++) {
                current = current.children.computeIfAbsent(word.charAt(i), c -> new TrieNode());
            }
            current.isWord = true;
        }

        public int findStartingPatterns(String design, int offset, int[] sizeMap) {
            TrieNode current = this;
            int size = 0;
            while (offset + size < design.length()
                && (current = current.children.get(design.charAt(offset + size))) != null) {
                size++;
                if (current.isWord) {
                    sizeMap[size] = 1;
                }
            }
            return size;
        }
    }
}
