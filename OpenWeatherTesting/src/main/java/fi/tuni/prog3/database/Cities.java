package fi.tuni.prog3.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.tuni.prog3.ReadWrite;
import fi.tuni.prog3.utils.EditDistance;

import java.io.*;
import java.net.*;
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
    public static String OPEN_WEATHER_BULK_CITY_URL = "https://bulk.openweathermap.org/sample/city.list.json.gz";
    public record City(String name, String countryCode) {};
    private final String location;
    private final String cityListLocation;
    private final String cityListOptimisedLocation;
    private City[] cityArr;
    private final boolean wasAbleToLoad;
    public Cities(Builder builder) {
        location = builder.location;
        cityListLocation = builder.cityListLocation;
        cityListOptimisedLocation = builder.optimizedCityListLocation;

        var attemptOptimised = ReadWrite.read(cityListOptimisedLocation);

        if (attemptOptimised.isPresent() && loadFromOptimised(attemptOptimised.get())) {
            wasAbleToLoad = true;
        } else {
            wasAbleToLoad = loadFromFallback(true);
        }
    }
    @Override
    public Optional<List<City>> get(String query) {
        if (!wasAbleToLoad) return Optional.empty();

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
    private boolean loadFromOptimised(String content) {
        Gson gson = new Gson();
        List<String> lines = content.lines().toList();
        cityArr = new City[lines.size()];

        for (int i : IntStream.range(0, cityArr.length).toArray()) {
            cityArr[i] = gson.fromJson(lines.get(i), City.class);
        }
        return true;
    }
    private boolean loadFromFallback(boolean isInitialAttempt) {
        record CityJSON(int id, String name, String state, String country, HashMap<String, Double> coord) {}

        String content;
        {
            var tmp = ReadWrite.readGZ(cityListLocation);
            if (tmp.isPresent()) content = tmp.get();
            else { System.err.println("Unable to get city.list.json"); return getCityListFromOpenWeatherBulk(); }
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        CityJSON[] cities = gson.fromJson(content, CityJSON[].class);

        if(cities == null) {
            System.err.println("Cities is null! -> Gson conversion failed!");
            cityArr = null;
            return isInitialAttempt && getCityListFromOpenWeatherBulk();
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
        return true;
    }
    private boolean getCityListFromOpenWeatherBulk() {
        try {
            URL url = URI.create(OPEN_WEATHER_BULK_CITY_URL).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() / 100 != 2) return false;

            var fs = new FileOutputStream(cityListLocation);
            fs.write(con.getInputStream().readAllBytes());
            fs.close();

            con.disconnect();
        } catch (IOException e) {
            return false;
        }

        return loadFromFallback(false);
    }
    private boolean saveOptimisedCityList() {
        Gson gson = new Gson();
        StringBuilder content = new StringBuilder();
        for (City c : cityArr) {
            content.append(gson.toJson(c)).append("\n");
        }
        return ReadWrite.write(cityListOptimisedLocation, content.substring(0, content.length() - 1));
    }
    private int[] calculateEditDistance(String word) {
        String[] words = new String[cityArr.length];
        Arrays.stream(cityArr).map(City::name).toList().toArray(words);
        try {
            return EditDistance.calculateDistancesParallel(words, word);
        } catch (RuntimeException e) {
            return EditDistance.calculateDistances(words, word);
        }
    }
    private double[] addCountryBias(int[] original) {
        return IntStream.range(0, original.length).mapToDouble(
                i -> (double)original[i] / (location.equalsIgnoreCase(cityArr[i].countryCode) ? 2.0 : 1.0)
                ).toArray();
    }
}
