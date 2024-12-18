import static java.lang.Integer.parseInt;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Day18 extends Day {

    public static void main(String[] args) {
        Day18 day = new Day18();  // https://adventofcode.com/2024/day/18

        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(322, day.part1(full));

        assertEquals("60,21", day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int GRID_SIZE = 71;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int STEPS = 2;

    @Override
    public String part1(String input) {
        int[][] map = new int[GRID_SIZE][GRID_SIZE];
        int[][] bytes = parseBytes(input);

        for (int i = 0; i < 1024; i++) {
            map[bytes[i][Y]][bytes[i][X]] = 1;
        }

        int[] pos = findWayOutBFS(map, new int[GRID_SIZE][GRID_SIZE]);

        return String.valueOf(pos == null ? 0 : pos[STEPS]);
    }

    @Override
    public String part2(String input) {
        int[][] map = new int[GRID_SIZE][GRID_SIZE];
        int[][] bytes = parseBytes(input);

        int[] blockingByte = null;
        int[][] visited = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < bytes.length; i++) {
            map[bytes[i][Y]][bytes[i][X]] = 1;
            if (i >= 1024 && findWayOutDFS(map, visited) == null) {
                blockingByte = bytes[i];
                break;
            }
        }

        return blockingByte == null ? "" : blockingByte[X] + "," + blockingByte[Y];
    }

    private static int[][] parseBytes(String input) {
        return input.lines().map(line -> {
            String[] split = line.split(",");
            return new int[]{
                parseInt(split[X]),
                parseInt(split[Y])
            };
        }).toArray(int[][]::new);
    }

    private static int[] findWayOutBFS(int[][] map, int[][] visited) {
        for (int[] line : visited) {
            Arrays.fill(line, 0);
        }
        int[] pos = new int[3];
        visited[pos[Y]][pos[X]] = 1;
        Queue<int[]> queue = new ArrayDeque<>(List.of(pos));
        while ((pos = queue.poll()) != null) {
            int x = pos[X];
            int y = pos[Y];
            if (x == 70 && y == 70) {
                return pos;
            }

            int x2, y2;

            // up
            y2 = y - 1;
            if (y2 >= 0 && map[y2][x] == 0 && visited[y2][x] == 0) {
                visited[y2][x] = 1;
                queue.add(new int[]{x, y2, pos[STEPS] + 1});
            }

            // right
            x2 = x + 1;
            if (x2 < GRID_SIZE && map[y][x2] == 0 && visited[y][x2] == 0) {
                visited[y][x2] = 1;
                queue.add(new int[]{x2, y, pos[STEPS] + 1});
            }

            // down
            y2 = y + 1;
            if (y2 < GRID_SIZE && map[y2][x] == 0 && visited[y2][x] == 0) {
                visited[y2][x] = 1;
                queue.add(new int[]{x, y2, pos[STEPS] + 1});
            }

            // left
            x2 = x - 1;
            if (x2 >= 0 && map[y][x2] == 0 && visited[y][x2] == 0) {
                visited[y][x2] = 1;
                queue.add(new int[]{x2, y, pos[STEPS] + 1});
            }

        }

        return null;
    }

    private static int[] findWayOutDFS(int[][] map, int[][] visited) {
        for (int[] line : visited) {
            Arrays.fill(line, 0);
        }
        visited[0][0] = 1;
        return findWayOutDFS(map, visited, 0, 0, 0);
    }

    private static int[] findWayOutDFS(int[][] map, int[][] visited, int x, int y, int steps) {
        if (x == 70 && y == 70) {
            return new int[]{x, y, steps};
        }

        int x2, y2;

        int[] pos;

        // right
        x2 = x + 1;
        if (x2 < GRID_SIZE && map[y][x2] == 0 && visited[y][x2] == 0) {
            visited[y][x2] = 1;
            pos = findWayOutDFS(map, visited, x2, y, steps + 1);
            if (pos != null) {
                return pos;
            }
        }

        // down
        y2 = y + 1;
        if (y2 < GRID_SIZE && map[y2][x] == 0 && visited[y2][x] == 0) {
            visited[y2][x] = 1;
            pos = findWayOutDFS(map, visited, x, y2, steps + 1);
            if (pos != null) {
                return pos;
            }
        }

        // left
        x2 = x - 1;
        if (x2 >= 0 && map[y][x2] == 0 && visited[y][x2] == 0) {
            visited[y][x2] = 1;
            pos = findWayOutDFS(map, visited, x2, y, steps + 1);
            if (pos != null) {
                return pos;
            }
        }

        // up
        y2 = y - 1;
        if (y2 >= 0 && map[y2][x] == 0 && visited[y2][x] == 0) {
            visited[y2][x] = 1;
            pos = findWayOutDFS(map, visited, x, y2, steps + 1);
            if (pos != null) {
                return pos;
            }
        }

        return null;
    }

}
