package fi.tuni.prog3.API;

import fi.tuni.prog3.security.Key;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class OpenWeather {
    public static class Methods {
        public static final String WEATHER_LAT_LON = "weather:lat-long";
        public static final String WEATHER_CITY_NAME = "weather:city-name";
    }
    private static class URLs {
        private static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}";
        private static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    }
    public static class Callables {
        public static API.callable WeatherLatLon(double lat, double lon) {
            return new API.callable(Methods.WEATHER_LAT_LON,
                    Map.of("{lat}", Double.toString(lat), "{lon}", Double.toString(lon)));
        }
        public static API.callable WeatherCityName(String cityName) {
            return new API.callable(Methods.WEATHER_CITY_NAME,
                    Map.of("{city name}", cityName));
        }
    }
    public static class factory implements API_Factory {
        private Key key = null;
        private HashMap<String, String> urls = new HashMap<>();
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
            urls.put(Methods.WEATHER_LAT_LON, API.addKey(URLs.WEATHER_LAT_LON, key));
            urls.put(Methods.WEATHER_CITY_NAME, API.addKey(URLs.WEATHER_CITY_NAME, key));
        }
        @Override
        public API construct() throws MalformedURLException {
            return new API(this);
        }
        @Override
        public Map<String, String> getURLs() {
            return urls;
        }
    }
}
