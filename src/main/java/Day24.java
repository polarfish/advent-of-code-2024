import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Day24 extends Day {

    public static void main(String[] args) {
        Day24 day = new Day24();  // https://adventofcode.com/2024/day/24

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(2024, day.part1(sample));
        assertEquals(45923082839246L, day.part1(full));

        assertEquals("jgb,rkf,rrs,rvc,vcg,z09,z20,z24", day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        String[] split = input.split("\n\n");
        long[] ini = parseInitialValues(split);
        Device device = parseDeviceLogic(split);

        return String.valueOf(device.run(ini[0], ini[1]));
    }

    @Override
    public String part2(String input) {
        String[] split = input.split("\n\n");
        Device device = parseDeviceLogic(split);

        List<String> result = new ArrayList<>();
        for (Entry<String, List<String>> e : device.gates().entrySet()) {
            String output = e.getKey();
            String op1 = e.getValue().getFirst();
            String operator = e.getValue().get(1);
            String op2 = e.getValue().getLast();

            if ((
                output.startsWith("z") && !operator.equals("XOR") && !output.equals("z45")
            ) || (
                operator.equals("XOR")
                    && !output.matches("([xyz]).*")
                    && !op1.matches("([xyz]).*")
                    && !op2.matches("([xyz]).*")
            ) || (
                operator.equals("AND")
                    && !"x00".equals(op1)
                    && !"x00".equals(op2)
                    && device.gates().values().stream()
                    .anyMatch(
                        g -> (g.getFirst().equals(output) || g.getLast().equals(output)) && !g.get(1)
                            .equals("OR"))
            ) || (
                operator.equals("XOR")
                    && device.gates().values().stream()
                    .anyMatch(
                        g -> (g.getFirst().equals(output) || g.getLast().equals(output)) && g.get(1).equals("OR"))
            )) {
                result.add(output);
            }
        }

        Collections.sort(result);

        return String.join(",", result);
    }

    private static Device parseDeviceLogic(String[] split) {
        Device device = new Device(new HashMap<>(), new HashMap<>());
        split[1].lines().forEach(line -> {
            String[] s = line.split(" ");
            device.addGate(s[4], s[0], s[1], s[2]);
        });
        return device;
    }

    private static long[] parseInitialValues(String[] split) {
        long x = 0L;
        long y = 0L;
        for (String line : split[0].split("\n")) {
            String[] s = line.split(": ");

            int value = parseInt(s[1]);

            if (value == 1) {
                if (s[0].startsWith("x")) {
                    x |= 1L << parseInt(s[0].substring(1));
                } else {
                    y |= 1L << parseInt(s[0].substring(1));
                }
            }
        }
        return new long[]{x, y};
    }

    record Device(Map<String, List<String>> gates, Map<String, String> jumpers) {

        public void addGate(String output, String wire1, String operation, String wire2) {
            gates.put(output, List.of(wire1, operation, wire2));
        }

        public long run(long x, long y) {
            long res = 0L;
            for (int i = 63; i >= 0; i--) {
                res = (res << 1) + getValueFor(x, y, "z%02d".formatted(i), 0);
            }

            return res;
        }

        int getValueFor(long x, long y, String wire, int depth) {
            if (depth > 100) {
                return 0;
            }
            if (wire.startsWith("x")) {
                return (int) ((x >> parseInt(wire.substring(1))) & 1);
            } else if (wire.startsWith("y")) {
                return (int) ((y >> parseInt(wire.substring(1))) & 1);
            } else {
                List<String> gate = jumpers.containsKey(wire)
                    ? gates.get(jumpers.get(wire))
                    : gates.get(wire);
                if (gate == null) {
                    return 0;
                }
                return activate(x, y, gate, depth + 1);
            }
        }

        int activate(long x, long y, List<String> gate, int depth) {
            return switch (gate.get(1)) {
                case "AND" -> getValueFor(x, y, gate.getFirst(), depth) & getValueFor(x, y, gate.getLast(), depth);
                case "OR" -> getValueFor(x, y, gate.getFirst(), depth) | getValueFor(x, y, gate.getLast(), depth);
                case "XOR" -> getValueFor(x, y, gate.getFirst(), depth) ^ getValueFor(x, y, gate.getLast(), depth);
                default -> throw new IllegalStateException("Unexpected value: " + gate.get(1));
            };
        }
    }

}
