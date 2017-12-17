package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

/**
 * Created by Mintas on 12/10/2017.
 */
public class GameOfLifeTest {
    public static final int NUMBER_OF_THREADS = 4;
    public static final String INPUT = "src/main/resources/input100.txt";
    public static final String OUTPUT = "src/main/resources/output100.txt";
    GameOfLife gameOfLife = new GameOfLifeImpl(NUMBER_OF_THREADS);

    @Test
    public void testGame() throws Exception {
        testOneGame(INPUT, OUTPUT);
    }

    private void testOneGame(String inputFile, String expectedOutputFile) throws FileNotFoundException {
        List<String> result = gameOfLife.play(inputFile);
        assertEquals(readFile(expectedOutputFile), result);
    }

    private static List<String> readFile(String fileName) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();

        Scanner scan = new Scanner(new File(fileName));
        while (scan.hasNextLine()) {
            lines.add(scan.nextLine());
        }
        scan.close();

        return lines;
    }
}
