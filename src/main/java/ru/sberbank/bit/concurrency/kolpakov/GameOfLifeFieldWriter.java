package ru.sberbank.bit.concurrency.kolpakov;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ONE;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ZERO;

public class GameOfLifeFieldWriter {

    public List<String> write(GameOfLifeField field) {
        return IntStream.range(0, field.getSize())
                .parallel()
                .mapToObj(i -> {
                    char[] line = new char[field.getSize()];
                    for (int j = 0; j < field.getSize(); j++) {
                        long value = field.getValue(i, j);
                        if (value == 1L) {
                            line[j] = ONE;
                        } else if (value == 0L) {
                            line[j] = ZERO;
                        } else {
                            throw new RuntimeException("Unexpected value");
                        }
                    }
                    return new String(line);
                })
                .collect(Collectors.toList());
    }
}
