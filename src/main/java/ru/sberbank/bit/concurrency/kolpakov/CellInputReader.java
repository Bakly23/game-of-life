package ru.sberbank.bit.concurrency.kolpakov;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.*;

public class CellInputReader {

    private int numberOfTries = -1;
    private int fieldSize = -1;

    public long[][] readFile(String inputFile) {
        Path inputPath = Paths.get(inputFile);
        long[][] result;
        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String firstLine = reader.readLine();
            fieldSize = Integer.parseInt(firstLine.split(" ")[0]);
            numberOfTries = Integer.parseInt(firstLine.split(" ")[1]);
            int widthInLongs = calcLineSizeInLongs(fieldSize);
            result = new long[widthInLongs][fieldSize + 2];
            for (int i = 0; i < fieldSize; i++) {
                char[] buffer = new char[fieldSize];
                reader.read(buffer, 0, fieldSize);
                for (int j = 0; j < widthInLongs; j++) {
                    result[j][i + 1] = readLongFromColumnWithIndex(buffer, j);
                }
                reader.skip(2); //skip \r\n
            }
            for (int i = 0; i < widthInLongs; i++) {
                result[i][0] = result[i][fieldSize];
                result[i][fieldSize + 1] = result[i][1];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private long readLongFromColumnWithIndex(char[] buffer, int j) {
        int startIndex = j * WIDTH_OF_COLUMN - 1;
        if (j == 0) {
            return (readLongFrom(buffer, 0, Math.min(buffer.length, 63)) >>> 1) | (readVal(buffer, buffer.length - 1) << 63);
        } else if (j == calcLineSizeInLongs(buffer.length) - 1) {
            int widthOfLastColumn = buffer.length - startIndex;
            return readLongFrom(buffer, startIndex, buffer.length) | (readVal(buffer, 0) << (Long.SIZE - widthOfLastColumn - 1));
        } else {
            return readLongFrom(buffer, startIndex, startIndex + 64);
        }
    }

    private long readVal(char[] buffer, int i) {
        if (buffer[i] == ONE) {
            return 1L;
        } else if (buffer[i] == ZERO) {
            return 0L;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private long readLongFrom(char[] buffer, int start, int finish) {
        long result = 0L;
        for (int i = start; i < finish; i++) {
            result |= (readVal(buffer, i) << (start + 64 - i - 1));
        }
        return result;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public int getFieldSize() {
        return fieldSize;
    }
}
