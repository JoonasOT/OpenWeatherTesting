package fi.tuni.prog3;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.IP_Getter;
import fi.tuni.prog3.API.OpenWeather.OpenWeather;
import fi.tuni.prog3.database.Database;

import java.util.List;

import fi.tuni.prog3.database.cities.Cities.City;
import fi.tuni.prog3.database.cities.builder.CityBuilder;
import fi.tuni.prog3.database.MaxMindGeoIP2;
import fi.tuni.prog3.database.MaxMindGeoIP2.GeoLocation;
import fi.tuni.prog3.API.OpenWeather.WeatherMap.Callables.*;
import fi.tuni.prog3.API.OpenWeather.WeatherMap.WeatherLayer;

public class Main {
    public static void main(String[] args) {
        // Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");


        API IP_API = new IP_Getter.factory().construct();
        API OpenWeatherAPI = new OpenWeather.factory().construct();

        Database<List<City>> cities = new CityBuilder()
                                            .setLocation("FI")
                                            .setDatabaseLocation("./db/Cities")
                                            .build();
        var r = cities.get("seinäjoki");
        r.ifPresent(System.out::println);
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

        // var weather_res = OpenWeatherAPI.call(new WeatherForecastCityNameCallable(city));
        // weather_res.ifPresent(response -> System.out.println(response.getData()));
        var map_res = OpenWeatherAPI.call(new WeatherMapCallable(WeatherLayer.PRECIPITATION, 12, 61.49911, 23.78712));
        map_res.ifPresent(response -> System.out.println(response.getData()));
        map_res.ifPresent(response -> ReadWrite.write("weather.png", response.getAllBytes()));

        map_res = OpenWeatherAPI.call(new OpenStreetMapCallable("GitHub-JoonasOT-OpenWeatherTesting/2.0", 12, 61.49911, 23.78712));
        map_res.ifPresent(response -> System.out.println(response.getData()));
        map_res.ifPresent(response -> ReadWrite.write("map.png", response.getAllBytes()));
    }
}