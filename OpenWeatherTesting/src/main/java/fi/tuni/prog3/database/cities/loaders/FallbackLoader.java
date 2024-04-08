package fi.tuni.prog3.database.cities.loaders;

import com.google.gson.Gson;
import fi.tuni.prog3.ReadWrite;
import fi.tuni.prog3.database.cities.Cities;

import fi.tuni.prog3.database.cities.Cities.City;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FallbackLoader implements CitiesLoader {
    private static FallbackLoader INSTANCE;
    private FallbackLoader() {}
    public static FallbackLoader GetInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FallbackLoader();
        }
        return INSTANCE;
    }
    private record CityJSON(int id, String name, String state, String country, HashMap<String, Double> coord) {}
    private static City[] cities;
    private static State state = State.IDLE;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static Future<Boolean> cityCountFuture;
    private static Future<Boolean> cityOptimisedFuture;
    @Override
    public void waitForReady() {
        try {
            cityCountFuture.get(1000, TimeUnit.MICROSECONDS);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        try {
            cityOptimisedFuture.get(10000, TimeUnit.MICROSECONDS);
        } catch (Exception e){
            System.err.println(e.getMessage());
        };
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void load(String fileLocation) throws RuntimeException {
        String[] locations = fileLocation.split(":");
        if (locations.length != 3) throw new RuntimeException("Invalid file locations given to Fallback loader!");

        String fallbackLocation = locations[0];
        String optimisedCitiesLocation = locations[1];
        String cityCountLocation = locations[2];

        state = State.LOADING;
        String content;
        {
            var tmp = ReadWrite.readGZ(fallbackLocation);
            if (tmp.isPresent()) content = tmp.get();
            else {
                System.err.println("Unable to get fallback city list");
                throw new RuntimeException("File not found!");
            }
        }
        Gson gson = new Gson();
        CityJSON[] cities = gson.fromJson(content, CityJSON[].class);

        if(cities == null) {
            System.err.println("Cities is null! -> Gson conversion failed!");
            throw new RuntimeException("Fallback city list corrupted!");
        }

        Set<Cities.City> citySet = Arrays.stream(cities).map(c -> new Cities.City(c.name(), c.country())).collect(Collectors.toSet());
        FallbackLoader.cities = new City[citySet.size()];
        int i = 0;
        for (Cities.City c : citySet) {
            FallbackLoader.cities[i] = c;
            i++;
        }
        final int finalRow = i;
        state = State.READY;

        cityCountFuture = executor.submit(() -> ReadWrite.write(cityCountLocation, Integer.toString(finalRow)));
        cityOptimisedFuture = executor.submit(() -> {
            StringBuilder outContent = new StringBuilder();
            for (City c : FallbackLoader.cities) {
                outContent.append(gson.toJson(c)).append("\n");
            }
            return ReadWrite.write(optimisedCitiesLocation, outContent.substring(0, outContent.length() - 1));
        });
    }
    @Override
    public City[] getCities() {
        return cities;
    }

    @Override
    public void close() {
        waitForReady();
        executor.shutdown();
    }
}
