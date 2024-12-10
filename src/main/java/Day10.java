import java.util.HashSet;
import java.util.Set;

public class Day10 extends Day {

    public static void main(String[] args) {
        Day10 day = new Day10();  // https://adventofcode.com/2024/day/10

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(36, day.part1(sample));
        assertEquals(607, day.part1(full));

        assertEquals(81, day.part2(sample));
        assertEquals(1384, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        int[][] map = parseMap(input);

        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 0) {
                    result += calculateScore(map, x, y, new HashSet<>());
                }
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        int[][] map = parseMap(input);

        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 0) {
                    result += calculateScore(map, x, y, null);
                }
            }
        }

        return String.valueOf(result);
    }

    private static int[][] parseMap(String input) {
        char[][] charMap = input.lines().map(String::toCharArray).toArray(char[][]::new);
        int[][] map = new int[charMap.length][charMap[0].length];
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                map[y][x] = charMap[y][x] - '0';
            }
        }
        return map;
    }

    private int calculateScore(int[][] map, int x, int y, Set<Integer> visitedPeaks) {
        if (map[y][x] == 9) {
            if (visitedPeaks == null) {
                // check for unique trails
                return 1;
            }
            int coordinatesId = toCoordinatesId(x, y);
            if (!visitedPeaks.contains(coordinatesId)) {
                visitedPeaks.add(coordinatesId);
                return 1;
            }
            return 0;
        }

        int result = 0;
        int nextHeight = map[y][x] + 1;

        // up
        if (y > 0 && map[y - 1][x] == nextHeight) {
            result += calculateScore(map, x, y - 1, visitedPeaks);
        }

        // right
        if (x < map[y].length - 1 && map[y][x + 1] == nextHeight) {
            result += calculateScore(map, x + 1, y, visitedPeaks);
        }

        // down
        if (y < map.length - 1 && map[y + 1][x] == nextHeight) {
            result += calculateScore(map, x, y + 1, visitedPeaks);
        }

        // left
        if (x > 0 && map[y][x - 1] == nextHeight) {
            result += calculateScore(map, x - 1, y, visitedPeaks);
        }

        return result;
    }

    private int toCoordinatesId(int x, int y) {
        return x * 100 + y;
    }
}
