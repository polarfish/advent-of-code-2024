import java.util.List;
import java.util.stream.IntStream;

public class Day4 extends Day {

    public static void main(String[] args) {
        Day4 day = new Day4();  // https://adventofcode.com/2024/day/4

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(18, day.part1(sample));
        assertEquals(2336, day.part1(full));

        assertEquals(9, day.part2(sample));
        assertEquals(1831, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    public static final char[] XMAS = "XMAS".toCharArray();

    @Override
    public String part1(String input) {
        char[][] m = buildMap(input);

        int result = 0;
        for (int y = 0; y < m.length; y++) {
            for (int x = 0; x < m[y].length; x++) {
                result += tryMatch(m, x, y);
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        char[][] map = buildMap(input);
        int result = 0;
        for (int y = 1; y < map.length - 1; y++) {
            for (int x = 1; x < map[y].length - 1; x++) {
                result +=
                    map[y][x] == 'A'
                        && (map[y - 1][x - 1] == 'M' && map[y + 1][x + 1] == 'S'
                        || map[y - 1][x - 1] == 'S' && map[y + 1][x + 1] == 'M')
                        && (map[y - 1][x + 1] == 'M' && map[y + 1][x - 1] == 'S'
                        || map[y - 1][x + 1] == 'S' && map[y + 1][x - 1] == 'M')
                        ? 1 : 0;
            }
        }
        return String.valueOf(result);
    }


    private int tryMatch(char[][] map, int x, int y) {
        return IntStream.range(0, 8).map(dir -> tryMatch(map, x, y, dir)).sum();
    }

    private int tryMatch(char[][] map, int x, int y, int direction) {
        for (int i = 0; i < 4; i++) {
            if (map[y][x] != XMAS[i]) {
                return 0;
            }
            if (map[y][x] == 'S') {
                return 1;
            }
            y += switch (direction) {
                case 0, 1, 7 -> -1;
                case 3, 4, 5 -> 1;
                default -> 0;
            };
            x += switch (direction) {
                case 1, 2, 3 -> 1;
                case 5, 6, 7 -> -1;
                default -> 0;
            };
            if (x < 0 || x >= map[0].length || y < 0 || y >= map.length) {
                return 0;
            }
        }

        return 1;
    }

    private char[][] buildMap(String input) {
        List<String> lines = input.lines().toList();
        char[][] map = new char[lines.size()][lines.getFirst().length()];
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                map[y][x] = line.charAt(x);
            }
        }
        return map;
    }

}
