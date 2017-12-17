package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

/**
 * Created by Mintas on 12/10/2017.
 */
public class GameOfLifeTest {
    GameOfLife gameOfLife = new GameOfLifeImpl(4);

    @Test
    public void testGame() throws Exception {
        //testOneGame("src/main/resources/input.txt", "src/main/resources/output.txt");
        testOneGame("src/main/resources/input100.txt", "src/main/resources/output100.txt");
        //testOneGame("src/main/resources/input1000.txt", "src/main/resources/output1000.txt");
    }

    private void testOneGame(String inputFile, String expectedOutputFile) throws IOException {
        List<String> result = gameOfLife.play(inputFile);
        Files.write(Paths.get("result100.txt"), result.stream().collect(joining("\n")).getBytes());
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
