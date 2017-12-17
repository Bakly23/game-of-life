package ru.sberbank.bit.concurrency.kolpakov;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;

public class GameOfLifeFieldTransformer {
    private final ExecutorService executorService;
    private final int numberOfThreads;

    public GameOfLifeFieldTransformer(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public GameOfLifeField generateNextField(GameOfLifeField currentField) {
        int fieldSize = currentField.getSize();
        int lineSizeInLongs = calcLineSizeInLongs(fieldSize);
        int numberOfUsedThreads = getNumberOfUsedThreads(lineSizeInLongs);
        try {
            run(getFutures(numberOfUsedThreads, lineSizeInLongs, currentField, (i, field) -> field.calcColumn(i)));
            run(getFutures(numberOfUsedThreads, lineSizeInLongs, currentField, (i, field) -> field.finalize(i)));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return new GameOfLifeField(new long[lineSizeInLongs][fieldSize + 2], fieldSize, currentField.getField());
    }

    private void run(CompletableFuture[] futures) throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(futures)
                .handleAsync((result, throwable) -> {
                    if (throwable != null) {
                        throw new RuntimeException(throwable);
                    } else {
                        return result;
                    }
                }, executorService)
                .get();
    }

    private CompletableFuture[] getFutures(int numberOfUsedThreads, int lineSizeInLongs, GameOfLifeField field,
                                           BiConsumer<Integer, GameOfLifeField> gameOfLifeAction) {
        return IntStream.range(0, numberOfUsedThreads)
                .mapToObj(runnableIndex -> CompletableFuture.runAsync(() -> {
                    int fromIndex = calcFromIndex(runnableIndex, lineSizeInLongs);
                    int toIndex = calcFromIndex(runnableIndex + 1, lineSizeInLongs);
                    for (int i = fromIndex; i < toIndex; i++) {
                        gameOfLifeAction.accept(i, field);
                    }
                }, executorService))
                .toArray(CompletableFuture[]::new);
    }

    private int getNumberOfUsedThreads(int lineSizeInLongs) {
        return Math.min(numberOfThreads, lineSizeInLongs);
    }

    private int calcFromIndex(int runnableIndex, int lineSizeInLongs) {
        int numberOfUsedThreads = getNumberOfUsedThreads(lineSizeInLongs);
        int tmpFromLineIndex = lineSizeInLongs / numberOfUsedThreads * runnableIndex;
        return Math.min(lineSizeInLongs, tmpFromLineIndex);
    }
}
