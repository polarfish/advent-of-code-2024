import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
        Map<String, Set<String>> map = new HashMap<>();

        input.lines().map(line -> line.split("-")).forEach(s -> {
            map.computeIfAbsent(s[0], k -> new HashSet<>()).add(s[1]);
            map.computeIfAbsent(s[1], k -> new HashSet<>()).add(s[0]);
        });

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
        Map<String, Set<String>> map = new HashMap<>();

        HashSet<String> seen = new HashSet<>();
        Queue<List<String>> q = new ArrayDeque<>();

        input.lines().map(line -> line.split("-")).forEach(s -> {
            Arrays.sort(s);
            q.add(Arrays.asList(s));
            map.computeIfAbsent(s[0], k -> new HashSet<>()).add(s[1]);
            map.computeIfAbsent(s[1], k -> new HashSet<>()).add(s[0]);
        });

        List<String> biggestGroup = new ArrayList<>();
        while (!q.isEmpty()) {
            List<String> group = q.poll();
            Set<String> intersection = group.stream().map(map::get)
                .reduce((s, s2) -> s.stream().filter(s2::contains).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
            intersection.forEach(e -> {
                List<String> next = new ArrayList<>(group);
                next.add(e);

                String token = String.join(",", next);
                if (seen.contains(token)) {
                    return;
                }
                seen.add(token);

                next.sort(String::compareTo);
                q.add(next);
                if (next.size() > biggestGroup.size()) {
                    biggestGroup.clear();
                    biggestGroup.addAll(next);
                }
            });
        }

        return String.join(",", biggestGroup);
    }

}
