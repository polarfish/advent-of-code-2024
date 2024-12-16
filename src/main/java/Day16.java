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

    @Override
    public String part1(String input) {
        int result = solve(input, true);
        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        int result = solve(input, false);
        return String.valueOf(result);
    }

    private int solve(String input, boolean returnFirstBestPath) {
        char[][] map = input.lines().map(String::toCharArray).toArray(char[][]::new);

        Queue<State> paths = new PriorityQueue<>(Comparator.comparing(State::score));

        Map<Integer, Integer> visited = new HashMap<>(input.length());
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'S') {
                    State startingState = new State(x, y, 1, 0, null);
                    paths.add(startingState);
                    visited.put(startingState.directedCoordId(), 0);
                    break;
                }
            }
        }

        int bestScore = -1;
        Set<Integer> allBestPathsVisited = new HashSet<>();
        while (!paths.isEmpty()) {
            State s = paths.poll();

            if (map[s.y()][s.x()] == 'E') {
                if (bestScore == -1 || bestScore == s.score()) {
                    bestScore = s.score();
                    if (returnFirstBestPath) {
                        return s.score();
                    } else {
                        State curr = s;
                        while (curr != null) {
                            allBestPathsVisited.add(curr.coordId());
                            curr = curr.prev();
                        }
                    }
                }
                continue;
            }

            if (s.canGoForward(map, visited)) {
                processState(s.goForward(), visited, paths);
            }
            if (s.canTurnRight(visited)) {
                processState(s.turnRight(), visited, paths);
            }
            if (s.canTurnLeft(visited)) {
                processState(s.turnLeft(), visited, paths);
            }
        }

        return allBestPathsVisited.size();
    }

    private static void processState(State s2, Map<Integer, Integer> visited, Queue<State> paths) {
        paths.add(s2);
        visited.put(s2.directedCoordId(), s2.score());
    }

    record State(int x, int y, int dir, int score, State prev) {

        public static final int[] RIGHT_TURN = new int[]{1, 2, 3, 0};
        public static final int[] LEFT_TURN = new int[]{3, 0, 1, 2};

        public boolean canGoForward(char[][] map, Map<Integer, Integer> visited) {
            int x2 = nextX();
            int y2 = nextY();
            if (map[y2][x2] == '#') {
                return false;
            }
            int directedCoordId = directedCoordId(x2, y2, dir);
            Integer visitedScore = visited.get(directedCoordId);
            return visitedScore == null || visitedScore >= score + 1;
        }

        public State goForward() {
            return new State(nextX(), nextY(), dir, score + 1, this);
        }

        public boolean canTurnRight(Map<Integer, Integer> visited) {
            int dir2 = RIGHT_TURN[dir];
            int directedCoordId = directedCoordId(x, y, dir2);
            Integer visitedScore = visited.get(directedCoordId);
            return visitedScore == null || visitedScore >= score + 1000;
        }

        public State turnRight() {
            return new State(x, y, RIGHT_TURN[dir], score + 1000, this);
        }

        public boolean canTurnLeft(Map<Integer, Integer> visited) {
            int dir2 = LEFT_TURN[dir];
            int directedCoordId = directedCoordId(x, y, dir2);
            Integer visitedScore = visited.get(directedCoordId);
            return visitedScore == null || visitedScore >= score + 1000;
        }


        public State turnLeft() {
            return new State(x, y, LEFT_TURN[dir], score + 1000, this);
        }

        public int nextX() {
            return x + (dir == 1 ? 1 : dir == 3 ? -1 : 0);
        }

        public int nextY() {
            return y + (dir == 2 ? 1 : dir == 0 ? -1 : 0);
        }

        public int coordId() {
            return x * 1000 + y;
        }

        public int directedCoordId() {
            return directedCoordId(x, y, dir);
        }

        private static int directedCoordId(int x, int y, int dir) {
            return x * 10_000 + y * 10 + dir;
        }

    }

}
