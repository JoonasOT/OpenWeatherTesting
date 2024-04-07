package fi.tuni.prog3.API.OpenWeather;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.API_Factory;
import fi.tuni.prog3.security.Key;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenWeather {

    public static class factory implements API_Factory {
        private Key key = null;
        private final HashMap<String, String> urls = new HashMap<>();
        public factory() {
            // Get the key
            try {
                key = new Key("ApiKeys/OpenWeather");
            }
            catch (IOException e) {
                System.err.println("Key threw an error!");
                e.printStackTrace(System.err);
                return;
            }

            // Add all the methods
            urls.put(CurrentWeather.Methods.WEATHER_LAT_LON, CurrentWeather.URLs.WEATHER_LAT_LON);
            urls.put(CurrentWeather.Methods.WEATHER_CITY_NAME, CurrentWeather.URLs.WEATHER_CITY_NAME);

            urls.put(WeatherForecast.Methods.WEATHER_LAT_LON, WeatherForecast.URLs.WEATHER_LAT_LON);
            urls.put(WeatherForecast.Methods.WEATHER_CITY_NAME, WeatherForecast.URLs.WEATHER_CITY_NAME);
        }
        @Override
        public API construct() {
            return new API(this);
        }
        @Override
        public Map<String, String> getURLs() {
            return urls;
        }

        @Override
        public Key getKey() {
            return key;
        }
    }
}
