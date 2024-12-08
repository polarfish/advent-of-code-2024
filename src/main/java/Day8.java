import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 extends Day {

    public static void main(String[] args) {
        Day8 day = new Day8();  // https://adventofcode.com/2024/day/8

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(14, day.part1(sample));
        assertEquals(269, day.part1(full));

        assertEquals(34, day.part2(sample));
        assertEquals(949, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        char[][] map = buildMap(input);
        Map<Character, List<Antenna>> groups = locateAntennas(map)
            .stream()
            .collect(Collectors.groupingBy(Antenna::frequency));

        Set<Antinode> antinodes = placeAntinodes(map, groups, false);

        return String.valueOf(antinodes.size());
    }

    @Override
    public String part2(String input) {
        char[][] map = buildMap(input);
        Map<Character, List<Antenna>> groups = locateAntennas(map)
            .stream()
            .collect(Collectors.groupingBy(Antenna::frequency));

        Set<Antinode> antinodes = placeAntinodes(map, groups, true);

        return String.valueOf(antinodes.size());
    }

    private Set<Antinode> placeAntinodes(char[][] map, Map<Character, List<Antenna>> groups, boolean fullLine) {
        Set<Antinode> antinodes = new AntinodeHashSet(map[0].length, map.length);
        for (List<Antenna> group : groups.values()) {
            for (int i = 0; i < group.size() - 1; i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Antenna a1 = group.get(i);
                    Antenna a2 = group.get(j);
                    int deltaY = Math.abs(a1.y() - a2.y());
                    int deltaX = Math.abs(a1.x() - a2.x());

                    if (a1.x() > a2.x()) {
                        Antenna b = a1;
                        a1 = a2;
                        a2 = b;
                    }

                    int left = fullLine ? 0 : 1;
                    do {
                        left++;
                    } while (antinodes.add(new Antinode(
                        a2.x() - deltaX * left,
                        a2.y() < a1.y() ? a2.y() + deltaY * left : a2.y() - deltaY * left
                    )) && fullLine);

                    int right = fullLine ? 0 : 1;
                    do {
                        right++;
                    } while (antinodes.add(new Antinode(
                        a1.x() + deltaX * right,
                        a2.y() < a1.y() ? a1.y() - deltaY * right : a1.y() + deltaY * right
                    )) && fullLine);
                }
            }
        }
        return antinodes;
    }

    private char[][] buildMap(String input) {
        return input.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    private List<Antenna> locateAntennas(char[][] map) {
        List<Antenna> antennas = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '.') {
                    continue;
                }
                antennas.add(new Antenna(x, y, map[y][x]));
            }
        }
        return antennas;
    }

    record Antenna(int x, int y, char frequency) {

    }

    record Antinode(int x, int y) {

    }

    static class AntinodeHashSet extends HashSet<Antinode> {

        private final int width;
        private final int height;

        public AntinodeHashSet(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean add(Antinode a) {
            if (a.x() >= width || a.x() < 0 || a.y() >= height || a.y() < 0) {
                return false;
            }
            super.add(a);
            return true;
        }
    }
}
