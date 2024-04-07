package fi.tuni.prog3;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.IP_Getter;
import fi.tuni.prog3.API.OpenWeather.OpenWeather;
import fi.tuni.prog3.database.Cities;
import fi.tuni.prog3.database.MaxMindGeoIP2;
import fi.tuni.prog3.database.Database;

import fi.tuni.prog3.API.OpenWeather.CurrentWeather.Callables.*;
import fi.tuni.prog3.API.OpenWeather.WeatherForecast.Callables.*;

import java.util.List;

import fi.tuni.prog3.database.Cities.City;
import fi.tuni.prog3.database.MaxMindGeoIP2.GeoLocation;

public class Main {
    public static void main(String[] args) {
        // Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");


        API IP_API = new IP_Getter.factory().construct();
        API OpenWeatherAPI = new OpenWeather.factory().construct();

        Database<List<City>> cities = new Cities.Builder()
                                                .setLocation("FI")
                                                .setCityListTo("./db/Cities/city.list.json.gz")
                                                .setOptimisedCityListLocationTo("./db/Cities/cities_optimised_load")
                                                .build();

        Database<GeoLocation> GeoIP = new MaxMindGeoIP2("./db/GeoLite2-City_20240402/GeoLite2-City.mmdb");


        String IP = null;
        String city = "";

        var IP_res = IP_API.call(IP_Getter.Callables.IP_AWS());
        if (IP_res.isPresent()) {
            IP = IP_res.get().getData();
            var res = GeoIP.get(IP);
            if (res.isPresent()) {
                city = res.get().city().getName();
            }
        }

        System.out.println(IP);
        System.out.println(city);
        var city_res = cities.get(city);
        city_res.ifPresent(System.out::println);

        var weather_res = OpenWeatherAPI.call(new WeatherForecastCityNameCallable(city));
        weather_res.ifPresent(response -> System.out.println(response.getData()));
    }
}