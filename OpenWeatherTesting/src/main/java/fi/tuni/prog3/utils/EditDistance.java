package fi.tuni.prog3.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class EditDistance {
    public static int compareCharIgnoreCase(char a, char b) {
        return Character.compare(Character.toUpperCase(a), Character.toUpperCase(b));
    }
    public static int calculateEditDistance(String want, String attempt) {
        if (Math.min(want.length(), attempt.length()) == 0) {
            return Math.max(want.length(), attempt.length());
        }

        int[][] matrix = new int[want.length() + 1][attempt.length() + 1];

        for (int i : IntStream.range(1, want.length() + 1).toArray()) matrix[i][0] = i;
        for (int i : IntStream.range(1, attempt.length() + 1).toArray()) matrix[0][i] = i;
        matrix[0][0] = 0;

        for (int x : IntStream.range(0, want.length()).toArray()) {
            for (int y : IntStream.range(0, attempt.length()).toArray()) {
                int min = IntStream.of(matrix[x][y], matrix[x + 1][y], matrix[x][y + 1]).min().getAsInt();
                matrix[x + 1][y + 1] = compareCharIgnoreCase(want.charAt(x), attempt.charAt(y)) == 0 ? min : min + 1;
            }
        }
        return matrix[want.length()][attempt.length()];
    }
    private record Task(String want, String attempt) implements Callable<Integer> {
        @Override
        public Integer call() {
            return calculateEditDistance(want, attempt);
        }
    }
    public static int[] calculateDistances(String[] words, String attempt) {
        return Arrays.stream(words).mapToInt(w -> calculateEditDistance(w, attempt)).toArray();
    }
    public static int[] calculateDistancesParallel(String[] words, String attempt) {
        List<Task> tasks = Arrays.stream(words).map(word -> new Task(word, attempt)).toList();
        List<Future<Integer>> futures;
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            futures = executor.invokeAll(tasks);
            return futures.stream().mapToInt(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).toArray();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
