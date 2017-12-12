package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameOfLifeImpl implements GameOfLife {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final CellInputReader reader;
    private final GameOfLifeFieldTransformer transformer;
    private final GameOfLifeFieldWriter writer;

    public GameOfLifeImpl(int numberOfThreads) {
        reader = new CellInputReader(numberOfThreads);
        transformer = new GameOfLifeFieldTransformer(numberOfThreads);
        writer = new GameOfLifeFieldWriter();
    }

    public static void main(String[] args) {

    }

    @Override
    public List<String> play(String inputFile) {
        GameOfLifeField result = new GameOfLifeField(reader.read(inputFile), reader.getFieldSize());
        int n = reader.getNumberOfTries();
        for (int i = 0; i < n; i++) {
            if(i * 20 % n == 0) {
                log.info("{}/{} steps were played", i, n);
            }
            result = transformer.generateNextField(result);
        }
        return writer.write(result);
    }
}
