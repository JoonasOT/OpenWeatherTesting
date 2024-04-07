package fi.tuni.prog3.API.OpenWeather;

import com.google.gson.Gson;
import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.OpenWeather.JSON_OBJs.*;

import java.util.List;
import java.util.Map;

public class WeatherForecast {
    private record Stats(double temp, double feels_like, double temp_min, double temp_max, int pressure, int sea_level,
                         int grnd_level, int humidity, double temp_kf){};
    private record Wind(double speed, int deg, double gust){};
    private record PartOfDay(String pod){};
    private record WeatherState(long dt, Stats main, List<Weather> weather, Clouds clouds, Wind wind, int visibility,
                                double pop, Map<String, Double> rain, PartOfDay sys, String dt_txt){};
    private record CityStats(long id, String name, Coord coord, String country, long population, int timezone,
                             long sunrise, long sunset){};
    public record JSON_OBJ(String cod, int message, int cnt, List<WeatherState> list,  CityStats city){};
    public static JSON_OBJ fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JSON_OBJ.class);
    }
    public static class Methods {
        public static final String WEATHER_LAT_LON = "weather-forecast:lat-long";
        public static final String WEATHER_CITY_NAME = "weather-forecast:city-name";
    }
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast?q={city name}&appid={API key}";
    }
    public static class Callables {
        public static API.callable WeatherLatLon(double lat, double lon) {
            return new API.callable(WeatherForecast.Methods.WEATHER_LAT_LON,
                    Map.of("{lat}", Double.toString(lat), "{lon}", Double.toString(lon)));
        }
        public static API.callable WeatherCityName(String cityName) {
            return new API.callable(WeatherForecast.Methods.WEATHER_CITY_NAME,
                    Map.of("{city name}", cityName));
        }
    }
}
