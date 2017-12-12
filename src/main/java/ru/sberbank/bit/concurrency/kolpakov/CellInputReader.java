package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.convertToLong;

public class CellInputReader {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final ExecutorService executorService;
    private final int numberOfThreads;
    private int numberOfTries = -1;
    private int fieldSize = -1;

    public CellInputReader(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public long[] read(String inputFile) {
        return read(readFile(inputFile));
    }

    public long[] read(char[] cellInput) {
        int fieldSize = calcFieldSize(cellInput);
        int bufferSize = calcBufferSize(fieldSize);
        log.info("Field size of cell input to read is {}, buffer size {}", fieldSize, bufferSize);
        final long[] buffer = new long[bufferSize];
        int numberOfUsedThreads = Math.min(numberOfThreads, fieldSize);
        log.info("Number of used threads to read cell input {}", numberOfUsedThreads);
        CompletableFuture[] readers = IntStream.range(0, numberOfUsedThreads)
                .mapToObj(i -> CompletableFuture.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        int fromLine = calcFromLineIndex(i);
                        int toLine = calcFromLineIndex(i + 1);
                        for (int currentLine = fromLine; currentLine < toLine; currentLine++) {
                            int lineSizeInLongs = calcLineSizeInLongs(fieldSize);
                            for (int currentLongInLine = 0; currentLongInLine < lineSizeInLongs; currentLongInLine++) {
                                int from = (currentLine * fieldSize) + (currentLongInLine << 6);
                                int to = Math.min(from + 64, (1 + currentLine) * fieldSize);
                                buffer[currentLongInLine * fieldSize + currentLine] = convertToLong(cellInput, from, to);
                            }
                        }
                    }

                    private int calcFromLineIndex(int i) {
                        int tmpIndex = fieldSize / numberOfUsedThreads * i;
                        return tmpIndex > fieldSize ? fieldSize : tmpIndex;
                    }
                }, executorService))
                .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(readers)
                    .handleAsync((result, throwable) -> {
                        if (throwable == null) {
                            return result;
                        } else {
                            throw new RuntimeException(throwable);
                        }
                    })
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    private char[] readFile(String inputFile) {
        Path inputPath = Paths.get(inputFile);
        char[] result = null;
        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String firstLine = reader.readLine();
            fieldSize = Integer.parseInt(firstLine.split(" ")[0]);
            numberOfTries = Integer.parseInt(firstLine.split(" ")[1]);
            result = new char[fieldSize * fieldSize];
            for (int i = 0; i < fieldSize; i++) {
                reader.read(result, i * fieldSize, fieldSize);
                reader.skip(2); //skip \r\n
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private int calcBufferSize(int fieldSize) {
        return calcLineSizeInLongs(fieldSize) * fieldSize;
    }

    private int calcFieldSize(char[] cellInput) {
        double tmpDoubleSize = Math.sqrt(cellInput.length);
        if (tmpDoubleSize != Math.rint(tmpDoubleSize)) {
            throw new IllegalArgumentException("width of field must be equal to a height of field");
        }
        return Double.valueOf(tmpDoubleSize).intValue();
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public int getFieldSize() {
        return fieldSize;
    }
}
