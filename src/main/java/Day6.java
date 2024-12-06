import java.util.Arrays;

public class Day6 extends Day {

    public static void main(String[] args) {
        Day6 day = new Day6();  // https://adventofcode.com/2024/day/6

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(41, day.part1(sample));
        assertEquals(5531, day.part1(full));

        assertEquals(6, day.part2(sample));
        assertEquals(2165, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        int[] guard = findGuardPosition(map);
        int[][] visits = new int[map.length][map[0].length];

        checkForLoop(map, visits, guard[0], guard[1], 0, false);

        int result = Arrays.stream(visits)
            .mapToInt(line -> Arrays.stream(line).map(Integer::signum).sum())
            .sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        int[] guard = findGuardPosition(map);
        int[][] visits = new int[map.length][map[0].length];

        int result = checkForLoop(map, visits, guard[0], guard[1], 0, true);

        return String.valueOf(result);
    }

    private int[] findGuardPosition(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '^') {
                    return new int[]{x, y};
                }
            }
        }
        throw new IllegalStateException("Guard not found");
    }

    private int[][] copyVisits(int[][] visits, int[][] visitsCopy) {
        for (int i = 0; i < visits.length; i++) {
            System.arraycopy(visits[i], 0, visitsCopy[i], 0, visits[i].length);
        }
        return visitsCopy;
    }

    private int turnRight(int dir) {
        return (dir + 1) % 4;
    }

    private int checkForLoop(char[][] map, int[][] visits, int x, int y, int dir, boolean tryObstruction) {
        int x2;
        int y2;
        int result = 0;
        int[][] visitsCopy = tryObstruction
            ? new int[visits.length][visits[0].length]
            : null;
        while (true) {
            visits[y][x] |= (1 << dir);
            switch (dir) {
                case 0: // up
                    x2 = x;
                    y2 = y - 1;
                    if (y2 < 0) {
                        return result;
                    }
                    break;
                case 1: // right
                    x2 = x + 1;
                    y2 = y;
                    if (x2 >= map[0].length) {
                        return result;
                    }
                    break;
                case 2: // down
                    x2 = x;
                    y2 = y + 1;
                    if (y2 >= map.length) {
                        return result;
                    }
                    break;
                case 3: // left
                    x2 = x - 1;
                    y2 = y;
                    if (x2 < 0) {
                        return result;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown direction");
            }

            if (map[y2][x2] == '#') {
                dir = (dir + 1) % 4;
            } else {
                if ((visits[y2][x2] & 1 << dir) > 0) {
                    return result + 1;
                }
                if (tryObstruction && visits[y2][x2] == 0) {
                    map[y2][x2] = '#';
                    result += checkForLoop(map, copyVisits(visits, visitsCopy), x, y, turnRight(dir), false);
                    map[y2][x2] = '.';
                }
                x = x2;
                y = y2;
            }
        }
    }
}
