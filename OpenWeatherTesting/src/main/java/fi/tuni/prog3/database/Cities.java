package fi.tuni.prog3.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.tuni.prog3.ReadWrite;
import fi.tuni.prog3.utils.EditDistance;

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
    private int[] calculateEditDistance(String word) {
        String[] words = (String[]) Arrays.stream(cityArr).map(City::name).toArray();
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
