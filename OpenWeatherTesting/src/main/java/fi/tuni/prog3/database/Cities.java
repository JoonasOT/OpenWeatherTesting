package fi.tuni.prog3.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.tuni.prog3.ReadWrite;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Cities implements Database<List<Cities.City>> {
    public interface SetOptionalFields {
        SetOptionalFields setOptimisedCityListLocationTo(String what);
        Cities build();
    }
    public interface SetCityListLocation {
        SetOptionalFields setCityListTo(String what);
    }
    public interface SetLocation {
        SetCityListLocation setLocation(String what);
    }
    public static class Builder implements SetOptionalFields, SetCityListLocation, SetLocation{
        private String location;
        private String cityListLocation;
        private String optimizedCityListLocation = "";


        @Override
        public SetOptionalFields setOptimisedCityListLocationTo(String what) {
            optimizedCityListLocation = what;
            return this;
        }

        @Override
        public SetOptionalFields setCityListTo(String what) {
            cityListLocation = what;
            return this;
        }

        @Override
        public SetCityListLocation setLocation(String what) {
            location = what;
            return this;
        }
        @Override
        public Cities build() {
            return new Cities(this);
        }
    }
    public static int MAX_CITIES_RETURNED = 5;
    public record City(String name, String countryCode) {};
    public record CityJSON(int id, String name, String state, String country, HashMap<String, Double> coord) {}
    private final String location;
    private final String cityListLocation;
    private final String cityListOptimisedLocation;
    private final City[] cityArr;
    public Cities(Builder builder) {
        location = builder.location;
        cityListLocation = builder.cityListLocation;
        cityListOptimisedLocation = builder.optimizedCityListLocation;

        var attemptOptimised = ReadWrite.read(cityListOptimisedLocation);

        if (attemptOptimised.isPresent()) {
            Gson gson = new Gson();
            List<String> lines = attemptOptimised.get().lines().toList();
            cityArr = new City[lines.size()];
            for (int i : IntStream.range(0, cityArr.length).toArray()) {
                cityArr[i] = gson.fromJson(lines.get(i), City.class);
            }
        } else {

            CityJSON[] cities = getCitiesInitial();

            if(cities == null) {
                System.err.println("Cities is null! -> Gson conversion failed!");
                cityArr = null;
                return;
            }
            Set<City> citySet = Arrays.stream(cities).map(c -> new City(c.name(), c.country())).collect(Collectors.toSet());
            cityArr = new City[citySet.size()];
            int i = 0;
            for (City c : citySet) {
                cityArr[i] = c;
                i++;
            }
            if (!saveOptimisedCityList()) {
                System.err.println("Was un able to save the optimised version of the city list!");
            }
        }
    }
    @Override
    public Optional<List<City>> get(String query) {
        {
            List<City> init = Arrays.stream(cityArr).filter(city -> city.name.equalsIgnoreCase(query)).toList();

            if (!init.isEmpty()) {
                return Optional.of(init);
            }
        }
        record CityWithWeight(City city, double weight){};

        double[] result = addCountryBias(calculateEditDistance(query));

        CityWithWeight[] intermediate = new CityWithWeight[result.length];
        for (int i : IntStream.range(0, result.length).toArray()) {
            intermediate[i] = new CityWithWeight(cityArr[i], result[i]);
        }
        Arrays.sort(intermediate, Comparator.comparingDouble(CityWithWeight::weight));

        var max = Math.min(MAX_CITIES_RETURNED, cityArr.length);

        return Optional.of(Arrays.stream(intermediate).map(CityWithWeight::city).toList().subList(0, max));
    }
    private CityJSON[] getCitiesInitial() {
        String content;
        {
            var tmp = ReadWrite.readGZ(cityListLocation);
            if (tmp.isPresent()) content = tmp.get();
            else { System.err.println("Unable to get city.list.json"); return null; }
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(content, CityJSON[].class);
    }
    private boolean saveOptimisedCityList() {
        Gson gson = new Gson();
        StringBuilder content = new StringBuilder();
        for (City c : cityArr) {
            content.append(gson.toJson(c)).append("\n");
        }
        return ReadWrite.write(cityListOptimisedLocation, content.substring(0, content.length() - 1));
    }
    private record EditDistanceTask(String want, String attempt) implements Callable<Integer> {
        private int compareCharIgnoreCase(char a, char b) {
            return Character.compare(Character.toUpperCase(a), Character.toUpperCase(b));
        }

        @Override
        public Integer call() {
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
    }
    private int[] calculateEditDistance(String word) {
        List<EditDistanceTask> tasks = Arrays.stream(cityArr).map(city -> new EditDistanceTask(city.name, word)).toList();
        List<Future<Integer>> futures;
        try {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            futures = executor.invokeAll(tasks);
            int[] res = futures.stream().mapToInt(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).toArray();
            executor.shutdown();
            return res;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private double[] addCountryBias(int[] original) {
        return IntStream.range(0, original.length).mapToDouble(
                i -> (double)original[i] / (location.equalsIgnoreCase(cityArr[i].countryCode) ? 2.0 : 1.0)
                ).toArray();
    }
}
