package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.getOneBitAtPosition;

public class GameOfLifeFieldTransformer {
    public static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);
    private final ExecutorService executorService;
    private final int numberOfThreads;

    public GameOfLifeFieldTransformer(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        log.info("number of used threads for transformations is {}", numberOfThreads);
    }

    public GameOfLifeField generateNextField(GameOfLifeField currentField) {
        int fieldSize = currentField.getSize();
        int lineSizeInLongs = calcLineSizeInLongs(fieldSize);
        final long[] buffer = currentField.getOldField();
        int numberOfUsedThreads = Math.min(numberOfThreads, lineSizeInLongs);
        CompletableFuture[] transformers = IntStream.range(0, numberOfUsedThreads)
                .mapToObj(runnableIndex -> CompletableFuture.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        int fromIndex = calcFromIndex(runnableIndex);
                        int toIndex = calcFromIndex(runnableIndex + 1);
                        for (int i = fromIndex; i < toIndex; i++) {
                            buffer[i] = calcNewLong(currentField, i);
                        }
                    }

                    private long calcNewLong(GameOfLifeField currentField, int i) {
                        long result = 0;
                        int line = i % fieldSize;
                        int startColumn = (i / fieldSize) * Long.SIZE;
                        for (int j = 0; j < calcBitsToOperate(i); j++) {
                            long newValue = currentField.calc(line, startColumn + j);
                            if (newValue == 1) {
                                result |= getOneBitAtPosition(j);
                            }
                        }
                        return result;
                    }

                    private int calcBitsToOperate(int i) {
                        int endColumn = (i / fieldSize) * Long.SIZE + Long.SIZE;
                        return endColumn > fieldSize
                                ? fieldSize % Long.SIZE
                                : Long.SIZE;
                    }

                    private int calcFromIndex(int runnableIndex) {
                        int tmpFromLineIndex = lineSizeInLongs / numberOfUsedThreads * runnableIndex;
                        int fromLineIndex = Math.min(lineSizeInLongs, tmpFromLineIndex);
                        return fromLineIndex * fieldSize;
                    }
                }, executorService))
                .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(transformers)
                    .handleAsync((result, throwable) -> {
                        if(throwable != null) {
                            throw new RuntimeException(throwable);
                        } else {
                            return result;
                        }
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return new GameOfLifeField(buffer, fieldSize, currentField.getField());
    }
}
