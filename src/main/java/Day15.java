import java.util.stream.IntStream;

public class Day15 extends Day {

    public static void main(String[] args) {
        Day15 day = new Day15();  // https://adventofcode.com/2024/day/15

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String sample3 = readFile("%s_sample3.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(10092, day.part1(sample));
        assertEquals(2028, day.part1(sample2));
        assertEquals(1294459, day.part1(full));

        assertEquals(9021, day.part2(sample));
        assertEquals(618, day.part2(sample3));
        assertEquals(1319212, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    @Override
    public String part1(String input) {
        String[] split = input.split("\n\n");
        int[][] map = parseMap(split[0]);
        int[] moves = parseMoves(split[1]);

        int robotX = -1;
        int robotY = -1;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                }
            }
        }

        for (int dir : moves) {
            int x = robotX;
            int y = robotY;
            int step = 0;
            while (true) {
                step++;
                x = nextX(x, dir);
                y = nextY(y, dir);
                if (map[y][x] == '.') {
                    map[robotY][robotX] = '.';
                    if (step > 1) {
                        map[y][x] = 'O';
                        map[robotY = nextY(robotY, dir)][robotX = nextX(robotX, dir)] = '@';
                    } else {
                        map[robotY = y][robotX = x] = '@';
                    }
                    break;
                } else if (map[y][x] == '#') {
                    break;
                }
            }
        }

        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 'O') {
                    result += y * 100 + x;
                }
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        String[] split = input.split("\n\n");
        int[][] map = parseWideMap(split[0]);
        int[] moves = parseMoves(split[1]);

        int robotX = -1;
        int robotY = -1;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                }
            }
        }

        for (int dir : moves) {
            if (canMove(map, robotX, robotY, dir)) {
                move(map, robotX, robotY, dir);
                robotX = nextX(robotX, dir);
                robotY = nextY(robotY, dir);
            }
        }

        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '[') {
                    result += y * 100 + x;
                }
            }
        }

        return String.valueOf(result);
    }

    private int[][] parseMap(String mapString) {
        return mapString.lines().map(s -> s.chars().toArray()).toArray(int[][]::new);
    }

    private int[][] parseWideMap(String mapString) {
        return mapString.lines().map(line -> line.chars()
            .flatMap(c -> switch (c) {
                    case '#' -> IntStream.of('#', '#');
                    case '.' -> IntStream.of('.', '.');
                    case 'O' -> IntStream.of('[', ']');
                    case '@' -> IntStream.of('@', '.');
                    default -> throw new IllegalStateException("Unexpected value: " + c);
                }
            ).toArray()
        ).toArray(int[][]::new);
    }

    private int[] parseMoves(String movesString) {
        return movesString.chars()
            .filter(c -> c != '\n')
            .map(c -> switch (c) {
                case '^' -> UP;
                case '>' -> RIGHT;
                case 'v' -> DOWN;
                case '<' -> LEFT;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            }).toArray();
    }

    private int nextX(int x, int dir) {
        return x + (dir == RIGHT ? 1 : dir == LEFT ? -1 : 0);
    }

    private int nextY(int y, int dir) {
        return y + (dir == DOWN ? 1 : dir == UP ? -1 : 0);
    }

    private boolean canMove(int[][] map, int x, int y, int dir) {
        int x2 = nextX(x, dir);
        int y2 = nextY(y, dir);

        if (map[y2][x2] == '.') {
            return true;
        }

        if (dir == UP || dir == DOWN) {
            if (map[y2][x2] == '[') {
                return canMove(map, x2, y2, dir) && canMove(map, x2 + 1, y2, dir);
            } else if (map[y2][x2] == ']') {
                return canMove(map, x2 - 1, y2, dir) && canMove(map, x2, y2, dir);
            }
        } else if (dir == RIGHT && map[y2][x2] == '[') {
            return canMove(map, x2 + 1, y2, dir);
        } else if (dir == LEFT && map[y2][x2] == ']') {
            return canMove(map, x2 - 1, y2, dir);
        }

        return false;
    }

    private void move(int[][] map, int x, int y, int dir) {
        int x2 = nextX(x, dir);
        int y2 = nextY(y, dir);

        if (dir == UP || dir == DOWN) {
            if (map[y2][x2] == '[') {
                move(map, x2, y2, dir);
                move(map, x2 + 1, y2, dir);
            } else if (map[y2][x2] == ']') {
                move(map, x2 - 1, y2, dir);
                move(map, x2, y2, dir);
            }
        } else if (dir == RIGHT && map[y2][x2] == '[') {
            move(map, x2 + 1, y2, dir);
            map[y2][x2 + 1] = map[y2][x2];
        } else if (dir == LEFT && map[y2][x2] == ']') {
            move(map, x2 - 1, y2, dir);
            map[y2][x2 - 1] = map[y2][x2];
        }

        map[y2][x2] = map[y][x];
        map[y][x] = '.';
    }

}
