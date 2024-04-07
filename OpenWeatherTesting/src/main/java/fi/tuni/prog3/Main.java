package fi.tuni.prog3;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.IP_Getter;
import fi.tuni.prog3.API.OpenWeather;
import fi.tuni.prog3.API.Response;
import fi.tuni.prog3.database.Cities;
import fi.tuni.prog3.database.MaxMindGeoIP2;
import fi.tuni.prog3.database.Database;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Database cities = new Cities.Builder().setLocation("FI").setCityListTo("./db/Cities/city.list.alt.json.gz")
                                              .setOptimisedCityListLocationTo("./db/Cities/cities_optimised_load.alt")
                                              .build();
        var r = cities.get("seinäjoki");
        r.ifPresent(System.out::println);
        if(true) return;


        API IP_API = new IP_Getter.factory().construct();
        String IP = null;
        try {
            Response res = IP_API.call(IP_Getter.Callables.IP_AWS());
            IP = res.getData();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        if (IP != null) {
            Database GeoIP = new MaxMindGeoIP2("./db/GeoLite2-City_20240402/GeoLite2-City.mmdb");
            var res = GeoIP.get(IP);
            if (res.isPresent()) {
                System.out.println(res.get());
                return;
            }
        }


        // Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");


        API OpenWeatherAPI;
        try {
            OpenWeatherAPI = new OpenWeather.factory().construct();
            Response response = OpenWeatherAPI.call(OpenWeather.Callables.WeatherCityName("Tampere"));
            System.out.println(response.getData());
        } catch (IOException e) {
            System.err.println("Api call failed!");
            return;
        }

    }
}