import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day9 extends Day {

    public static void main(String[] args) {
        Day9 day = new Day9();  // https://adventofcode.com/2024/day/9

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(1928, day.part1(sample));
        assertEquals(60, day.part1(sample2));
        assertEquals(6288599492129L, day.part1(full));

        assertEquals(2858, day.part2(sample));
        assertEquals(132, day.part2(sample2));
        assertEquals(6321896265143L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        int[] diskMap = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            diskMap[i] = input.charAt(i) - '0';
        }
        int i = 0;
        int iFile;
        int j = input.length() - 1;
        int jFile = diskMap[j];
        int memoryAddress = 0;
        long checksum = 0;
        top:
        do {
            if (i % 2 == 0) {
                iFile = diskMap[i];
                while (iFile-- > 0) {
                    checksum += (long) memoryAddress++ * toFileID(i);
                }
            } else {
                int iSpaces = diskMap[i];
                while (iSpaces-- > 0) {
                    checksum += (long) memoryAddress++ * toFileID(j);
                    if (--jFile == 0) {
                        if (--j == i) {
                            break top;
                        }
                        jFile = diskMap[--j];
                    }
                }
            }
        } while (++i < j);

        while (jFile-- > 0) {
            checksum += (long) memoryAddress++ * toFileID(j);
        }

        return String.valueOf(checksum);
    }

    static class Space {

        final int size;
        int freeSize;
        private final List<FileBlocks> files = new ArrayList<>();

        public Space(int size) {
            this.size = size;
            this.freeSize = size;
        }

        public void addFile(FileBlocks file) {
            files.add(file);
            freeSize -= file.size();
        }

        public List<FileBlocks> files() {
            return files;
        }

        public int freeSize() {
            return freeSize;
        }

    }

    static final class FileBlocks {

        private final int id;
        private final int size;
        private boolean moved = false;

        FileBlocks(int id, int size) {
            this.id = id;
            this.size = size;
        }

        public int id() {
            return id;
        }

        public int size() {
            return size;
        }

        public boolean isMoved() {
            return moved;
        }

        public void setMoved(boolean moved) {
            this.moved = moved;
        }
    }

    @Override
    public String part2(String input) {
        int[] diskMap = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            diskMap[i] = input.charAt(i) - '0';
        }

        List<Space> spaces = IntStream.range(0, diskMap.length)
            .filter(i -> i % 2 == 1)
            .map(i -> diskMap[i])
            .mapToObj(Space::new)
            .toList();

        List<FileBlocks> files = IntStream.range(0, diskMap.length)
            .filter(i -> i % 2 == 0)
            .mapToObj(i -> new FileBlocks(i / 2, diskMap[i]))
            .toList();

        for (int i = 0; i < files.size(); i++) {
            FileBlocks file = files.get(files.size() - i - 1);
            for (int j = 0; j < spaces.size() - i; j++) {
                Space space = spaces.get(j);
                if (space.freeSize() >= file.size) {
                    space.addFile(file);
                    file.setMoved(true);
                    break;
                }
            }
        }

        int memoryAddress = 0;
        long checksum = 0;
        for (int i = 0; i < diskMap.length; i++) {
            if (i % 2 == 0) {
                // file
                FileBlocks file = files.get(i / 2);
                if (file.isMoved()) {
                    memoryAddress += file.size();
                    continue;

                }
                for (int j = 0; j < file.size(); j++) {
                    checksum += (long) memoryAddress++ * file.id();
                }
            } else {
                Space space = spaces.get((i - 1) / 2);
                for (FileBlocks file : space.files) {
                    for (int j = 0; j < file.size(); j++) {
                        checksum += (long) memoryAddress++ * file.id();
                    }
                }

                memoryAddress += space.freeSize();
            }
        }

        return String.valueOf(checksum);
    }

    private int toFileID(int index) {
        return index / 2;
    }
}
