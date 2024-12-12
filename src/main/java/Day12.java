import java.util.HashSet;
import java.util.Set;

public class Day12 extends Day {

    public static void main(String[] args) {
        Day12 day = new Day12();  // https://adventofcode.com/2024/day/0

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(140, day.part1(sample2));
        assertEquals(1930, day.part1(sample));
        assertEquals(1467094, day.part1(full));

        assertEquals(0, day.part2(sample));
        assertEquals(0, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int AREA = 0;
    private static final int PERIMETER = 1;

    @Override
    public String part1(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        Set<Integer> processed = new HashSet<>(map.length * map[0].length);
        int result = 0;
        int[] region;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if ((region = measureRegion(map, x, y, processed)) != null) {
                    result += region[AREA] * region[PERIMETER];
                }
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        return "0";
    }

    private int[] measureRegion(char[][] map, int x, int y, Set<Integer> processed) {
        if (processed.contains(toPointId(x, y))) {
            return null;
        }
        int[] region = new int[2];
        measureRegion(map, x, y, processed, map[y][x], region);
        return region;
    }

    private void measureRegion(char[][] map, int x, int y, Set<Integer> processed, char plant, int[] region) {
        if (processed.contains(toPointId(x, y))) {
            return;
        }
        region[AREA]++;
        processed.add(toPointId(x, y));

        // up
        if (y > 0 && map[y - 1][x] == plant) {
            measureRegion(map, x, y - 1, processed, plant, region);
        } else {
            region[PERIMETER]++;
        }

        // right
        if (x < map[0].length - 1 && map[y][x + 1] == plant) {
            measureRegion(map, x + 1, y, processed, plant, region);
        } else {
            region[PERIMETER]++;
        }

        // down
        if (y < map.length - 1 && map[y + 1][x] == plant) {
            measureRegion(map, x, y + 1, processed, plant, region);
        } else {
            region[PERIMETER]++;
        }

        // left
        if (x > 0 && map[y][x - 1] == plant) {
            measureRegion(map, x - 1, y, processed, plant, region);
        } else {
            region[PERIMETER]++;
        }
    }

    private static int toPointId(int x, int y) {
        return x * 1000 + y;
    }
}
