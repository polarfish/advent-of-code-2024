import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Day23 extends Day {

    public static void main(String[] args) {
        Day23 day = new Day23();  // https://adventofcode.com/2024/day/23

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(7, day.part1(sample));
        assertEquals(1163, day.part1(full));

        assertEquals("co,de,ka,ta", day.part2(sample));
        assertEquals("bm,bo,ee,fo,gt,hv,jv,kd,md,mu,nm,wx,xh", day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Map<String, Set<String>> map = parseInput(input);

        Set<String> result = new HashSet<>();
        for (Entry<String, Set<String>> e : map.entrySet()) {
            String from = e.getKey();
            List<String> conns = new ArrayList<>(e.getValue());
            for (int i = 0; i < conns.size() - 1; i++) {
                for (int j = i; j < conns.size(); j++) {

                    String ci = conns.get(i);
                    String cj = conns.get(j);

                    if (ci.equals(cj)) {
                        continue;
                    }

                    if (map.containsKey(ci) && map.get(ci).contains(cj)
                        && map.containsKey(cj) && map.get(cj).contains(ci)
                        && (from.startsWith("t") || cj.startsWith("t") || ci.startsWith("t"))) {

                        String[] strings = {from, ci, cj};
                        Arrays.sort(strings);
                        String join = String.join(",", strings);
                        result.add(join);
                    }
                }
            }
        }

        return String.valueOf(result.size());
    }

    @Override
    public String part2(String input) {
        Map<String, Set<String>> map = parseInput(input);

        int maxPossibleGroup = map.values().stream().mapToInt(Set::size).max().orElse(0);

        Set<String> checked = new HashSet<>();
        Set<String> memo = new HashSet<>();
        List<String> best = new ArrayList<>();
        List<String> result = map.keySet().stream()
            .takeWhile(s -> best.size() < maxPossibleGroup)
            .map(computer -> {
                checked.add(computer);
                List<String> res = dfs(map, List.of(computer), checked, memo);
                if (res.size() > best.size()) {
                    best.clear();
                    best.addAll(res);
                }
                return res;
            })
            .max(Comparator.comparingInt(List::size))
            .orElse(Collections.emptyList());

        return String.join(",", result);
    }

    private static Map<String, Set<String>> parseInput(String input) {
        Map<String, Set<String>> map = new HashMap<>();
        input.lines().map(line -> line.split("-")).forEach(s -> {
            map.computeIfAbsent(s[0], k -> new HashSet<>()).add(s[1]);
            map.computeIfAbsent(s[1], k -> new HashSet<>()).add(s[0]);
        });
        return map;
    }

    private List<String> dfs(Map<String, Set<String>> map, List<String> group, Set<String> checked, Set<String> memo) {
        if (group.size() == 13) {
            return group;
        }

        Set<String> connections = new HashSet<>(map.get(group.getFirst()));
        for (int i = 1; i < group.size(); i++) {
            connections.retainAll(map.get(group.get(i)));
        }

        List<String> result = connections.stream()
            .filter(not(checked::contains))
            .map(conn -> {
                List<String> next = new ArrayList<>(group);
                next.add(conn);
                next.sort(String::compareTo);
                return memo.add(String.join(",", next))
                    ? dfs(map, next, checked, memo)
                    : Collections.<String>emptyList();
            })
            .max(Comparator.comparingInt(List::size))
            .orElse(emptyList());

        return result.size() > group.size() ? result : group;
    }

}
