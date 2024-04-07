package fi.tuni.prog3.API.OpenWeather;

import fi.tuni.prog3.API.iCallable;

import java.util.Map;

import static fi.tuni.prog3.API.OpenWeather.WeatherMap.URLs.OSM_MAP;
import static fi.tuni.prog3.API.OpenWeather.WeatherMap.URLs.WEATHER_MAP;

public class WeatherMap {
    public enum WeatherLayer {
        CLOUDS, PRECIPITATION, PRESSURE, WIND, TEMP;
        @Override
        public String toString() {
            return name().toLowerCase() + "_new";
        }
    }
    public static class URLs {
        public static final String WEATHER_MAP = "https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={API key}";
        public static final String OSM_MAP = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
    }
    public static class Callables {
        public record WeatherMapCallable(WeatherLayer layer, int z, double lat, double log) implements iCallable {
            @Override public String url() { return WEATHER_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{layer}", layer.toString(),
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }
        }
        public record OpenStreetMapCallable(String userAgent, int z, double lat, double log) implements iCallable {
            @Override public String url() { return OSM_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }

        }

    }

    private static int longitudeToX(double log, int z) {
        return (int) (Math.pow(2, z) * ((log + 180.0) / 360));
    }
    private static int latitudeToY(double lat, int z) {
        return (int) (Math.pow(2, z) / 2 * (1 - Math.log(Math.tan(lat * Math.PI/180.0) + 1.0 / Math.cos(lat * Math.PI / 180.0)) / Math.PI));
    }
}
