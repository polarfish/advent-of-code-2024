import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Day16 extends Day {

    public static void main(String[] args) {
        Day16 day = new Day16();  // https://adventofcode.com/2024/day/16

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(7036, day.part1(sample));
        assertEquals(11048, day.part1(sample2));
        assertEquals(89460, day.part1(full));

        assertEquals(45, day.part2(sample));
        assertEquals(64, day.part2(sample2));
        assertEquals(504, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    public static final int X = 0;
    public static final int Y = 1;
    public static final int DIR = 2;
    public static final int SCORE = 3;

    @Override
    public String part1(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        Queue<int[]> paths = new PriorityQueue<>(input.length(), Comparator.comparing(step -> step[SCORE]));
        Set<Integer> visited = new HashSet<>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    paths.add(new int[]{x, y, 3, 0, 1});
                    break;
                }
            }
        }

        int[] s = null;
        while (!paths.isEmpty()) {
            s = paths.poll();
            int x = s[X];
            int y = s[Y];
            int dir = s[DIR];
            visited.add(toDirectedCoordId(x, y, dir));
            int score = s[SCORE];
            if (map[y][x] == 'E') {
                break;
            }

            int x2, y2, dir2;

            // forward
            x2 = nextX(x, dir);
            y2 = nextY(y, dir);
            if (map[y2][x2] != '#' && !visited.contains(toDirectedCoordId(x2, y2, dir))) {
                paths.add(new int[]{x2, y2, dir, score + 1});
            }

            // right
            dir2 = RIGHT_TURN[dir];
            if (map[y][x] != '#' && !visited.contains(toDirectedCoordId(x, y, dir2))) {
                paths.add(new int[]{x, y, dir2, score + 1000});
            }

            // left
            dir2 = LEFT_TURN[dir];
            if (map[y][x] != '#' && !visited.contains(toDirectedCoordId(x, y, dir2))) {
                paths.add(new int[]{x, y, dir2, score + 1000});
            }
        }

        int result = s != null ? s[SCORE] : 0;

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        Queue<Holder> paths = new PriorityQueue<>(input.length(),
            Comparator.comparing(holder -> holder.state()[SCORE]));

        Map<Integer, Integer> visited = new HashMap<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    paths.add(new Holder(new int[]{x, y, 3, 0}, Set.of(toCoordId(x, y))));
                    break;
                }
            }
        }

        int bestScore = -1;
        Set<Integer> allBestPathsVisited = new HashSet<>();
        while (!paths.isEmpty()) {
            Holder h = paths.poll();
            int[] s = h.state();
            int x = s[X];
            int y = s[Y];
            int dir = s[DIR];
            int score = s[SCORE];
            Set<Integer> stepHistory = h.history();

            if (map[y][x] == 'E') {
                if (bestScore == -1 || bestScore == score) {
                    bestScore = score;
                    allBestPathsVisited.addAll(stepHistory);
                }
                continue;
            }

            int x2, y2, dir2, score2;
            int[] s2;

            // forward
            x2 = nextX(x, dir);
            y2 = nextY(y, dir);
            score2 = score + 1;
            if (map[y2][x2] != '#') {
                Integer visitedScore = visited.get(toDirectedCoordId(x2, y2, dir));
                s2 = new int[]{x2, y2, dir, score2};
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(new Holder(s2, extendSet(stepHistory, toCoordId(x2, y2))));
                    visited.put(toDirectedCoordId(x2, y2, dir), score2);
                }
            }

            // right
            dir2 = RIGHT_TURN[dir];
            score2 = score + 1000;
            if (map[y][x] != '#') {
                Integer visitedScore = visited.get(toDirectedCoordId(x, y, dir2));
                s2 = new int[]{x, y, dir2, score2};
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(new Holder(s2, extendSet(stepHistory, toCoordId(x, y))));
                    visited.put(toDirectedCoordId(x, y, dir2), score2);
                }
            }

            // left
            dir2 = LEFT_TURN[dir];
            score2 = score + 1000;
            if (map[y][x] != '#') {
                Integer visitedScore = visited.get(toDirectedCoordId(x, y, dir2));
                s2 = new int[]{x, y, dir2, score2};
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(new Holder(s2, extendSet(stepHistory, toCoordId(x, y))));
                    visited.put(toDirectedCoordId(x, y, dir2), score2);
                }
            }
        }

        return String.valueOf(allBestPathsVisited.size());

    }

    record Holder(int[] state, Set<Integer> history) {

    }

    public static final int[] RIGHT_TURN = new int[]{1, 2, 3, 0};
    public static final int[] LEFT_TURN = new int[]{3, 0, 1, 2};

    public int nextX(int x, int dir) {
        return x + (dir == 1 ? 1 : dir == 3 ? -1 : 0);
    }

    public int nextY(int y, int dir) {
        return y + (dir == 2 ? 1 : dir == 0 ? -1 : 0);
    }

    public static int toCoordId(int x, int y) {
        return x * 1000 + y;
    }

    public static int toDirectedCoordId(int x, int y, int dir) {
        return x * 10_000 + y * 10 + dir;
    }

    private static Set<Integer> extendSet(Set<Integer> set, Integer i) {
        Set<Integer> result = new HashSet<>(set);
        result.add(i);
        return result;
    }
}
