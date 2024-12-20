import static java.lang.Integer.parseInt;

import java.util.List;
import java.util.regex.Pattern;

public class Day14 extends Day {

    public static void main(String[] args) {
        Day14 day = new Day14();  // https://adventofcode.com/2024/day/14

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(12, day.part1(sample));
        assertEquals(230686500, day.part1(full));

        assertEquals(7672, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final Pattern ROBOT_PATTERN = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
    private static final int PX = 0;
    private static final int PY = 1;
    private static final int VX = 2;
    private static final int VY = 3;

    @Override
    public String part1(String input) {
        List<int[]> robots = parseRobots(input);
        int w = robots.size() < 100 ? 11 : 101;
        int h = robots.size() < 100 ? 7 : 103;

        robots.forEach(r -> moveRobot(r, w, h, 100));
        int result = calculateSafetyFactor(robots, w, h);

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        List<int[]> robots = parseRobots(input);
        int w = robots.size() < 100 ? 11 : 101;
        int h = robots.size() < 100 ? 7 : 103;

        int initialSafetyFactor = calculateSafetyFactor(robots, w, h);
        int safetyFactor;
        int s = 0;
        do {
            s++;
            robots.forEach(r -> moveRobot(r, w, h, 1));
            safetyFactor = calculateSafetyFactor(robots, w, h);
        } while (Math.abs(initialSafetyFactor - safetyFactor) < 130_000_000);

        return String.valueOf(s);
    }

    private static void moveRobot(int[] r, int w, int h, int seconds) {
        r[PX] = ((r[PX] + r[VX] * seconds) % w + w) % w;
        r[PY] = ((r[PY] + r[VY] * seconds) % h + h) % h;
    }

    private static List<int[]> parseRobots(String input) {
        return ROBOT_PATTERN.matcher(input)
            .results()
            .map(r -> new int[]{
                parseInt(r.group(1)),
                parseInt(r.group(2)),
                parseInt(r.group(3)),
                parseInt(r.group(4)),
            })
            .toList();
    }

    private static int calculateSafetyFactor(List<int[]> robots, int w, int h) {
        int[] quadrants = new int[5];

        int index;
        for (int[] r : robots) {
            if (r[PY] == h / 2 || r[PX] == w / 2) {
                index = 0;
            } else if (r[PY] < h / 2) {
                index = r[PX] < w / 2 ? 1 : 2;
            } else {
                index = r[PX] < w / 2 ? 3 : 4;
            }
            quadrants[index]++;
        }

        return quadrants[1] * quadrants[2] * quadrants[3] * quadrants[4];
    }

}
