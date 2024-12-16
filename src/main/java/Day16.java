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
        String sample3 = readFile("%s_sample3.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(7036, day.part1(sample));
        assertEquals(11048, day.part1(sample2));
        assertEquals(21148, day.part1(sample3));
        assertEquals(89460, day.part1(full));

        assertEquals(45, day.part2(sample));
        assertEquals(64, day.part2(sample2));
        assertEquals(149, day.part2(sample3));
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
                    paths.add(new int[]{x, y, 1, 0, 1});
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
            visited.add(State.directedCoordId(x, y, dir));
            int score = s[SCORE];
            if (map[y][x] == 'E') {
                break;
            }

            int x2, y2, dir2;

            // forward
            x2 = nextX(x, dir);
            y2 = nextY(y, dir);
            if (map[y2][x2] != '#' && !visited.contains(State.directedCoordId(x2, y2, dir))) {
                paths.add(new int[]{x2, y2, dir, score + 1});
            }

            // right
            dir2 = RIGHT_TURN[dir];
            if (map[y][x] != '#' && !visited.contains(State.directedCoordId(x, y, dir2))) {
                paths.add(new int[]{x, y, dir2, score + 1000});
            }

            // left
            dir2 = LEFT_TURN[dir];
            if (map[y][x] != '#' && !visited.contains(State.directedCoordId(x, y, dir2))) {
                paths.add(new int[]{x, y, dir2, score + 1000});
            }
        }

        int result = s != null ? s[SCORE] : 0;

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        Queue<State> paths = new PriorityQueue<>(input.length(), Comparator.comparing(State::score));

        Map<Integer, Integer> visited = new HashMap<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    paths.add(new State(x, y, 3, 0, null));
                    break;
                }
            }
        }

        int bestScore = -1;
        Set<Integer> allBestPathsVisited = new HashSet<>();
        while (!paths.isEmpty()) {
            State s = paths.poll();
            int x = s.x();
            int y = s.y();
            int dir = s.dir();
            int score = s.score();

            if (map[y][x] == 'E') {
                if (bestScore == -1 || bestScore == score) {
                    bestScore = score;
                    State curr = s;
                    while (curr != null) {
                        allBestPathsVisited.add(curr.coordId());
                        curr = curr.prev();
                    }
                }
                continue;
            }

            int x2, y2, dir2, score2;
            State newState;

            // forward
            x2 = nextX(x, dir);
            y2 = nextY(y, dir);
            score2 = score + 1;
            if (map[y2][x2] != '#') {
                Integer visitedScore = visited.get(State.directedCoordId(x2, y2, dir));
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(newState = new State(x2, y2, dir, score2, s));
                    visited.put(newState.directedCoordId(), score2);
                }
            }

            // right
            dir2 = RIGHT_TURN[dir];
            score2 = score + 1000;
            if (map[y][x] != '#') {
                Integer visitedScore = visited.get(State.directedCoordId(x, y, dir2));
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(newState = new State(x, y, dir2, score2, s));
                    visited.put(newState.directedCoordId(), score2);
                }
            }

            // left
            dir2 = LEFT_TURN[dir];
            score2 = score + 1000;
            if (map[y][x] != '#') {
                Integer visitedScore = visited.get(State.directedCoordId(x, y, dir2));
                if (visitedScore == null || visitedScore >= score2) {
                    paths.add(newState = new State(x, y, dir2, score2, s));
                    visited.put(newState.directedCoordId(), score2);
                }
            }
        }

        return String.valueOf(allBestPathsVisited.size());

    }

    record State(int x, int y, int dir, int score, State prev) {

        public int coordId() {
            return coordId(x, y);
        }

        public int coordId(int x, int y) {
            return x * 1000 + y;
        }

        public int directedCoordId() {
            return directedCoordId(x, y, dir);
        }

        public static int directedCoordId(int x, int y, int dir) {
            return x * 10_000 + y * 10 + dir;
        }
    }

    public static final int[] RIGHT_TURN = new int[]{1, 2, 3, 0};
    public static final int[] LEFT_TURN = new int[]{3, 0, 1, 2};

    public int nextX(int x, int dir) {
        return x + (dir == 1 ? 1 : dir == 3 ? -1 : 0);
    }

    public int nextY(int y, int dir) {
        return y + (dir == 2 ? 1 : dir == 0 ? -1 : 0);
    }


}
