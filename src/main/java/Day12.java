public class Day12 extends Day {

    public static void main(String[] args) {
        Day12 day = new Day12();  // https://adventofcode.com/2024/day/12

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String sample3 = readFile("%s_sample3.txt".formatted(day.name()));
        String sample4 = readFile("%s_sample4.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(1930, day.part1(sample));
        assertEquals(140, day.part1(sample2));
        assertEquals(1467094, day.part1(full));

        assertEquals(1206, day.part2(sample));
        assertEquals(80, day.part2(sample2));
        assertEquals(236, day.part2(sample3));
        assertEquals(368, day.part2(sample4));
        assertEquals(881182, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int AREA = 0;
    private static final int PERIMETER = 1;
    private static final int SIDES = 2;

    @Override
    public String part1(String input) {
        char[][] map = parseMap(input);

        boolean[][] visited = new boolean[map.length][map[0].length];
        int result = 0;
        int[] region;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if ((region = measureRegion(map, x, y, visited)) != null) {
                    result += region[AREA] * region[PERIMETER];
                }
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        char[][] map = parseMap(input);

        boolean[][] visited = new boolean[map.length][map[0].length];
        int result = 0;
        int[] region;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if ((region = measureRegion(map, x, y, visited)) != null) {
                    result += region[AREA] * region[SIDES];
                }
            }
        }

        return String.valueOf(result);
    }

    private static char[][] parseMap(String input) {
        return input.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    private int[] measureRegion(char[][] map, int x, int y, boolean[][] visited) {
        if (visited[y][x]) {
            return null;
        }
        int[] region = new int[3];
        measureRegion(map, x, y, visited, map[y][x], region);
        return region;
    }

    private void measureRegion(char[][] map, int x, int y, boolean[][] visited, char plant, int[] region) {
        if (visited[y][x]) {
            return;
        }
        region[AREA]++;
        visited[y][x] = true;

        // up
        if (y > 0 && map[y - 1][x] == plant) {
            measureRegion(map, x, y - 1, visited, plant, region);
        } else {
            region[PERIMETER]++;
            boolean sideContinuation = x > 0 && map[y][x - 1] == plant && (y == 0 || map[y - 1][x - 1] != plant);
            if (!sideContinuation) {
                region[SIDES]++;
            }
        }

        // right
        if (x < map[0].length - 1 && map[y][x + 1] == plant) {
            measureRegion(map, x + 1, y, visited, plant, region);
        } else {
            region[PERIMETER]++;
            boolean sideContinuation = y > 0 && map[y - 1][x] == plant && (x == map[0].length - 1 || map[y - 1][x + 1] != plant);
            if (!sideContinuation) {
                region[SIDES]++;
            }
        }

        // down
        if (y < map.length - 1 && map[y + 1][x] == plant) {
            measureRegion(map, x, y + 1, visited, plant, region);
        } else {
            region[PERIMETER]++;
            boolean sideContinuation = x < map[0].length - 1 && map[y][x + 1] == plant && (y == map.length - 1 || map[y + 1][x + 1] != plant);
            if (!sideContinuation) {
                region[SIDES]++;
            }
        }

        // left
        if (x > 0 && map[y][x - 1] == plant) {
            measureRegion(map, x - 1, y, visited, plant, region);
        } else {
            region[PERIMETER]++;
            boolean sideContinuation = y < map.length - 1 && map[y + 1][x] == plant && (x == 0 || map[y + 1][x - 1] != plant);
            if (!sideContinuation) {
                region[SIDES]++;
            }
        }
    }

}
