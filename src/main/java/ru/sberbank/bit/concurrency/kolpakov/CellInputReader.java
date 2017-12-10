package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.convertToLong;

public class CellInputReader {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final ExecutorService executorService;
    private final int numberOfThreads;
    private int sizeOfReadField = -1;

    public CellInputReader(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public long[] read(char[] cellInput) {
        int fieldSize = calcFieldSize(cellInput);
        int bufferSize = calcBufferSize(fieldSize);
        log.info("Field size of cell input to read is {}, buffer size {}", fieldSize, bufferSize);
        final long[] buffer = new long[bufferSize];
        int numberOfUsedThreads = Math.min(numberOfThreads, fieldSize);
        log.info("Number of used threads to read cell input {}", numberOfUsedThreads);
        IntStream.range(0, numberOfUsedThreads)
                .parallel()
                .mapToObj(i -> new Runnable() {
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
                })
                .map(executorService::submit)
                .forEach(future -> {
                    try {
                        future.get();
                        log.info("thread finished reading its part of chars");
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
        executorService.shutdown();
        try {
            executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sizeOfReadField = fieldSize;
        return buffer;
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

    public int getSizeOfReadField() {
        if (sizeOfReadField == -1) {
            throw new IllegalStateException("nothing was read yet");
        } else {
            return sizeOfReadField;
        }
    }
}
