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

    @Override
    public String part1(String input) {
        String[] split = input.split("\n\n");
        char[][] map = parseMap(split[0]);
        char[] moves = split[1].replaceAll("\n", "").toCharArray();
        int[] robot = locateRobot(map);
        int robotX = robot[0];
        int robotY = robot[1];

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

        int result = sumUpCoordinates(map, 'O');

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        String[] split = input.split("\n\n");
        char[][] map = parseWideMap(split[0]);
        char[] moves = split[1].replaceAll("\n", "").toCharArray();
        int[] robot = locateRobot(map);
        int robotX = robot[0];
        int robotY = robot[1];

        for (int dir : moves) {
            if (canMove(map, robotX, robotY, dir)) {
                move(map, robotX, robotY, dir);
                robotX = nextX(robotX, dir);
                robotY = nextY(robotY, dir);
            }
        }

        int result = sumUpCoordinates(map, '[');

        return String.valueOf(result);
    }

    private int sumUpCoordinates(char[][] map, char symbol) {
        int result = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == symbol) {
                    result += y * 100 + x;
                }
            }
        }
        return result;
    }

    private char[][] parseMap(String mapString) {
        return mapString.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    private char[][] parseWideMap(String mapString) {
        char[][] map = parseMap(mapString);
        char[][] wideMap = new char[map.length][map[0].length * 2];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                switch (map[y][x]) {
                    case '#':
                        wideMap[y][x * 2] = '#';
                        wideMap[y][x * 2 + 1] = '#';
                        break;
                    case '.':
                        wideMap[y][x * 2] = '.';
                        wideMap[y][x * 2 + 1] = '.';
                        break;
                    case 'O':
                        wideMap[y][x * 2] = '[';
                        wideMap[y][x * 2 + 1] = ']';
                        break;
                    case '@':
                        wideMap[y][x * 2] = '@';
                        wideMap[y][x * 2 + 1] = '.';
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + map[y][x]);
                }
            }
        }

        return wideMap;
    }

    private int[] locateRobot(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '@') {
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private int nextX(int x, int dir) {
        return x + (dir == '>' ? 1 : dir == '<' ? -1 : 0);
    }

    private int nextY(int y, int dir) {
        return y + (dir == 'v' ? 1 : dir == '^' ? -1 : 0);
    }

    private boolean canMove(char[][] map, int x, int y, int dir) {
        int x2 = nextX(x, dir);
        int y2 = nextY(y, dir);

        if (map[y2][x2] == '.') {
            return true;
        }

        if (dir == '^' || dir == 'v') {
            if (map[y2][x2] == '[') {
                return canMove(map, x2, y2, dir) && canMove(map, x2 + 1, y2, dir);
            } else if (map[y2][x2] == ']') {
                return canMove(map, x2 - 1, y2, dir) && canMove(map, x2, y2, dir);
            }
        } else if (dir == '>' && map[y2][x2] == '[') {
            return canMove(map, x2 + 1, y2, dir);
        } else if (dir == '<' && map[y2][x2] == ']') {
            return canMove(map, x2 - 1, y2, dir);
        }

        return false;
    }

    private void move(char[][] map, int x, int y, int dir) {
        int x2 = nextX(x, dir);
        int y2 = nextY(y, dir);

        if (dir == '^' || dir == 'v') {
            if (map[y2][x2] == '[') {
                move(map, x2, y2, dir);
                move(map, x2 + 1, y2, dir);
            } else if (map[y2][x2] == ']') {
                move(map, x2 - 1, y2, dir);
                move(map, x2, y2, dir);
            }
        } else if (dir == '>' && map[y2][x2] == '[') {
            move(map, x2 + 1, y2, dir);
            map[y2][x2 + 1] = map[y2][x2];
        } else if (dir == '<' && map[y2][x2] == ']') {
            move(map, x2 - 1, y2, dir);
            map[y2][x2 - 1] = map[y2][x2];
        }

        map[y2][x2] = map[y][x];
        map[y][x] = '.';
    }

}
