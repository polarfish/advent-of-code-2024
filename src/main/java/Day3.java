import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends Day {

    public static void main(String[] args) {
        Day3 day = new Day3();  // https://adventofcode.com/2024/day/3

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(161, day.part1(sample));
        assertEquals(174960292, day.part1(full));

        assertEquals(48, day.part2(sample2));
        assertEquals(56275602, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        Matcher matcher = pattern.matcher(input);
        int result = 0;
        while (matcher.find()) {
            result += parseInt(matcher.group(1)) * parseInt(matcher.group(2));
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
        Matcher matcher = pattern.matcher(input);
        int result = 0;
        boolean doMultiply = true;
        while (matcher.find()) {
            switch (matcher.group().charAt(2)) {
                case 'l': // mul
                    if (doMultiply) {
                        result += parseInt(matcher.group(1)) * parseInt(matcher.group(2));
                    }
                    break;
                case 'n': // don't
                    doMultiply = false;
                    break;
                default: // do
                    doMultiply = true;
            }

        }

        return String.valueOf(result);
    }
}
