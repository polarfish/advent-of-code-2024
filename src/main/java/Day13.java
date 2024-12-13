import static java.lang.Long.parseLong;

import java.util.List;
import java.util.regex.Pattern;

public class Day13 extends Day {

    public static void main(String[] args) {
        Day13 day = new Day13();  // https://adventofcode.com/2024/day/13

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(480, day.part1(sample));
        assertEquals(29517, day.part1(full)); // 14869 - too low

        assertEquals(875318608908L, day.part2(sample));
        assertEquals(103570327981381L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final Pattern MACHINE_PATTERN = Pattern.compile(
        "Button A: X\\+(\\d+), Y\\+(\\d+)\\nButton B: X\\+(\\d+), Y\\+(\\d+)\\nPrize: X=(\\d+), Y=(\\d+)");
    private static final int AX = 0;
    private static final int AY = 1;
    private static final int BX = 2;
    private static final int BY = 3;
    private static final int PX = 4;
    private static final int PY = 5;

    @Override
    public String part1(String input) {
        List<long[]> machines = parseMachines(input);

        long result = machines.stream()
            .mapToLong(this::solveMachine).sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        List<long[]> machines = parseMachines(input);

        long result = machines.stream()
            .peek(m -> {
                m[PX] += 10000000000000L;
                m[PY] += 10000000000000L;
            })
            .mapToLong(this::solveMachine).sum();

        return String.valueOf(result);
    }

    private static List<long[]> parseMachines(String input) {
        return MACHINE_PATTERN.matcher(input)
            .results()
            .map(r -> new long[]{
                parseLong(r.group(1)),
                parseLong(r.group(2)),
                parseLong(r.group(3)),
                parseLong(r.group(4)),
                parseLong(r.group(5)),
                parseLong(r.group(6))
            })
            .toList();
    }

    private long solveMachine(long[] m) {
        long det = m[AX] * m[BY] - m[BX] * m[AY];
        long a = (m[PX] * m[BY] - m[PY] * m[BX]) / det;
        long b = (m[PY] * m[AX] - m[PX] * m[AY]) / det;
        if (m[AX] * a + m[BX] * b == m[PX] && m[AY] * a + m[BY] * b == m[PY]) {
            return a * 3 + b;
        } else {
            return 0;
        }
    }

}
