import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Day20 extends Day {

    public static void main(String[] args) {
        Day20 day = new Day20();  // https://adventofcode.com/2024/day/20

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(0, day.part1(sample));
        assertEquals(1317, day.part1(full));

        assertEquals(0, day.part2(sample));
        assertEquals(982474, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int X = 0;
    private static final int Y = 1;

    @Override
    public String part1(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        int startX = -1;
        int startY = -1;
        int endX = -1;
        int endY = -1;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    map[startY = y][startX = x] = '.';
                } else if (map[y][x] == 'E') {
                    map[y][x] = '.';
                    map[endY = y][endX = x] = '.';
                }
            }
        }

        List<Step> path = new ArrayList<>();
        Step curr;
        path.add(curr = new Step(startX, startY, 0));
        Step[][] visited = new Step[map.length][map[0].length];
        visited[startY][startX] = curr;
        while (!(curr.x() == endX && curr.y() == endY)) {
            int x = curr.x();
            int y = curr.y();

            // up
            if (y > 0 && map[y - 1][x] != '#' && visited[y - 1][x] == null) {
                path.add(curr = new Step(x, y - 1, curr.time() + 1));
                visited[y - 1][x] = curr;
                continue;
            }

            // right
            if (x < map[0].length - 1 && map[y][x + 1] != '#' && visited[y][x + 1] == null) {
                path.add(curr = new Step(x + 1, y, curr.time() + 1));
                visited[y][x + 1] = curr;
                continue;
            }

            // down
            if (y < map.length - 1 && map[y + 1][x] != '#' && visited[y + 1][x] == null) {
                path.add(curr = new Step(x, y + 1, curr.time() + 1));
                visited[y + 1][x] = curr;
                continue;
            }

            // left
            if (x < map[0].length - 1 && map[y][x - 1] != '#' && visited[y][x - 1] == null) {
                path.add(curr = new Step(x - 1, y, curr.time() + 1));
                visited[y][x - 1] = curr;
                continue;
            }

            throw new IllegalStateException("Broken track");
        }

        Map<Integer, Integer> shortcuts = new HashMap<>();
        Step hackyStep;
        int savedSteps;
        for (Step step : path) {
            int x = step.x();
            int y = step.y();

            // up
            if (y > 1 && map[y - 1][x] == '#' && (hackyStep = visited[y - 2][x]) != null
                && (savedSteps = hackyStep.time() - step.time() - 2) > 0) {
                shortcuts.merge(savedSteps, 1, Integer::sum);
            }

            // right
            if (x < map[0].length - 2 && map[y][x + 1] == '#' && (hackyStep = visited[y][x + 2]) != null
                && (savedSteps = hackyStep.time() - step.time() - 2) > 0) {
                shortcuts.merge(savedSteps, 1, Integer::sum);
            }

            // down
            if (y < map.length - 2 && map[y + 1][x] == '#' && (hackyStep = visited[y + 2][x]) != null
                && (savedSteps = hackyStep.time() - step.time() - 2) > 0) {
                shortcuts.merge(savedSteps, 1, Integer::sum);
            }

            // right
            if (x > 1 && map[y][x - 1] == '#' && (hackyStep = visited[y][x - 2]) != null
                && (savedSteps = hackyStep.time() - step.time() - 2) > 0) {
                shortcuts.merge(savedSteps, 1, Integer::sum);
            }
        }

        int result = shortcuts.entrySet().stream()
            .filter(e -> e.getKey() >= 100)
            .mapToInt(Entry::getValue)
            .sum();

        return String.valueOf(result);
    }


    @Override
    public String part2(String input) {
        char[][] map = parseMap(input);

        int result = calculateShortcuts(map, 20);

        return String.valueOf(result);
    }

    private static char[][] parseMap(String input) {
        return input.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    private static int calculateShortcuts(char[][] map, int shortcutMaxSize) {
        int[] start = {-1, -1};
        int[] end = {-1, -1};
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    map[start[Y] = y][start[X] = x] = '.';
                } else if (map[y][x] == 'E') {
                    map[y][x] = '.';
                    map[end[Y] = y][end[X] = x] = '.';
                }
            }
        }

        List<int[]> path = new ArrayList<>();
        int[] curr;
        int[] prev = {-1, -1};
        path.add(curr = start);
        while (!(curr[X] == end[X] && curr[Y] == end[Y])) {
            int x = curr[X];
            int y = curr[Y];

            // up
            if (y > 0 && map[y - 1][x] == '.' && !(x == prev[X] && y - 1 == prev[Y])) {
                prev = curr;
                path.add(curr = new int[]{x, y - 1});
                continue;
            }

            // right
            if (x < map[0].length - 1 && map[y][x + 1] != '#' && !(x + 1 == prev[X] && y == prev[Y])) {
                prev = curr;
                path.add(curr = new int[]{x + 1, y});
                continue;
            }

            // down
            if (y < map.length - 1 && map[y + 1][x] != '#' && !(x == prev[X] && y + 1 == prev[Y])) {
                prev = curr;
                path.add(curr = new int[]{x, y + 1});
                continue;
            }

            // left
            if (x < map[0].length - 1 && map[y][x - 1] != '#' && !(x - 1 == prev[X] && y == prev[Y])) {
                prev = curr;
                path.add(curr = new int[]{x - 1, y});
                continue;
            }

            throw new IllegalStateException("Broken track");
        }

        int result = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            for (int j = i + 1; j < path.size(); j++) {
                int[] from = path.get(i);
                int[] to = path.get(j);
                int manhattanDistance = Math.abs(from[X] - to[X]) + Math.abs(from[Y] - to[Y]);
                int trackDistance = j - i;
                if (manhattanDistance <= shortcutMaxSize && trackDistance - manhattanDistance >= 100) {
                    result++;
                }
            }
        }
        return result;
    }

    record Step(int x, int y, int time) {

    }
}
