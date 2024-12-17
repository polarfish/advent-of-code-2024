import static java.lang.Long.parseLong;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day17 extends Day {

    public static void main(String[] args) {
        Day17 day = new Day17();  // https://adventofcode.com/2024/day/17

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals("4,6,3,5,6,3,5,2,1,0", day.part1(sample));
        assertEquals("7,3,5,7,5,7,4,3,0", day.part1(full));

        assertEquals(105734774294938L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final Pattern PROGRAM_PATTERN = Pattern.compile(
        "Register A: (\\d+)\\nRegister B: (\\d+)\\nRegister C: (\\d+)\\n\\nProgram: (.+)$");
    public static final int A = 0;
    public static final int B = 1;
    public static final int C = 2;

    @Override
    public String part1(String input) {
        Matcher matcher = PROGRAM_PATTERN.matcher(input);
        if (!matcher.find()) {
            return "0";
        }
        long[] registers = new long[]{
            parseLong(matcher.group(1)),
            parseLong(matcher.group(2)),
            parseLong(matcher.group(3))
        };
        int[] program = Arrays.stream(matcher.group(4).split(",")).mapToInt(Integer::parseInt).toArray();

        int[] out = new int[32];
        int len = runProgram(program, registers, out);

        return Arrays.stream(Arrays.copyOfRange(out, 0, len))
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(","));
    }

    @Override
    public String part2(String input) {
        Matcher matcher = PROGRAM_PATTERN.matcher(input);
        if (!matcher.find()) {
            return "0";
        }
        long[] registers = new long[3];
        int[] program = Arrays.stream(matcher.group(4).split(",")).mapToInt(Integer::parseInt).toArray();

        long result = backtrack(program, registers, new int[program.length], 0, program.length - 1);

        return String.valueOf(result);
    }

    private long backtrack(int[] program, long[] registers, int[] out, long a, int dig) {
        long res = -1;

        for (int i = 0; i < 8; i++) {
            registers[A] = a + i;
            registers[B] = 0L;
            registers[C] = 0L;
            runProgram(program, registers, out);
            if (out[0] == program[dig]) {
                if (dig == 0) {
                    return a + i;
                }

                long res2 = backtrack(program, registers, out, (a + i) * 8, dig - 1);
                if (res2 != -1) {
                    if (res == -1) {
                        res = res2;
                    } else {
                        res = Math.min(res, res2);
                    }
                }
            }
        }

        return res;
    }

    private int runProgram(int[] program, long[] registers, int[] out) {
        int len = 0;
        int ptr = 0;
        while (ptr < program.length) {
            int opcode = program[ptr++];
            int operand = program[ptr++];

            switch (opcode) {
                case 0: // adv
                    registers[A] = registers[A] / pow(2, combo(registers, operand), 1);
                    break;
                case 1: // bxl
                    registers[B] = registers[B] ^ operand;
                    break;
                case 2: // bst
                    registers[B] = combo(registers, operand) % 8;
                    break;
                case 3: // jnz
                    ptr = registers[A] == 0 ? ptr : operand;
                    break;
                case 4: // bxc
                    registers[B] = registers[B] ^ registers[C];
                    break;
                case 5: // out
                    out[len++] = (int) (combo(registers, operand) % 8);
                    break;
                case 6: // bdv
                    registers[B] = registers[A] / pow(2, combo(registers, operand), 1);
                    break;
                case 7: // cdv
                    registers[C] = registers[A] / pow(2, combo(registers, operand), 1);
                    break;
                default:
            }
        }
        return len;
    }

    private long combo(long[] registers, int operand) {
        if (operand > 3 && operand < 7) {
            return registers[operand - 4];
        }
        return operand;
    }

    private long pow(long n, long p, long accumulator) {
        if (p == 0) {
            return accumulator;
        } else {
            return pow(n, p - 1, accumulator * n);
        }
    }
}
