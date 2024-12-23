import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Day22 extends Day {

    public static void main(String[] args) {
        Day22 day = new Day22();  // https://adventofcode.com/2024/day/22

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(37327623, day.part1(sample));
        assertEquals(14082561342L, day.part1(full));

        assertEquals(23, day.part2(sample2));
        assertEquals(1568, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        long result = input.lines().mapToLong(Long::parseLong)
            .map(n -> {
                for (int i = 0; i < 2000; i++) {
                    n = (n ^ (n << 6)) & 16777215;
                    n = (n ^ (n >> 5)) & 16777215;
                    n = (n ^ (n << 11)) & 16777215;
                }
                return n;
            })
            .sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        List<Integer> seq = new LinkedList<>();
        Map<Marker, Integer> totals = new HashMap<>();

        input.lines().mapToLong(Long::parseLong)
            .forEach(n -> {
                long n2 = n;
                int p, p2;
                Map<Marker, Integer> currents = new HashMap<>(3000);
                for (int i = 0; i < 2000; i++, n = n2) {
                    n2 = (n2 ^ (n2 << 6)) & 16777215;
                    n2 = (n2 ^ (n2 >> 5)) & 16777215;
                    n2 = (n2 ^ (n2 << 11)) & 16777215;

                    p = (int) (n % 10);
                    p2 = (int) (n2 % 10);

                    seq.add(p2 - p);
                    if (seq.size() == 4) {

                        Marker marker = new Marker(seq.get(0), seq.get(1), seq.get(2), seq.get(3));

                        if (!currents.containsKey(marker)) {
                            currents.put(marker, p2);
                        }
                        seq.removeFirst();
                    }
                }

                currents.forEach((marker, price) -> totals.merge(marker, price, Integer::sum));
            });

        int result = totals.values().stream().mapToInt(i -> i).max().orElse(0);

        return String.valueOf(result);
    }

    record Marker(int d1, int d2, int d3, int d4) {

    }
}