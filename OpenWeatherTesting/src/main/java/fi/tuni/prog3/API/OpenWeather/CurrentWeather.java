package fi.tuni.prog3.API.OpenWeather;

import com.google.gson.Gson;
import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.OpenWeather.JSON_OBJs.*;

import java.util.List;
import java.util.Map;

public class CurrentWeather {
    private record StatsCurrent(double temp, int pressure, int humidity, double temp_min, double temp_max){};
    private record Wind(double speed, int deg){};
    private record SysInfo(int type, int id, double message, String country, long sunrise, long sunset){};
    public record JSON_OBJ(Coord coord, List<Weather> weather, String base, StatsCurrent main, int visibility, Wind wind,
                           Clouds clouds, long dt, SysInfo sys, long id, String name, int cod){};
    public static JSON_OBJ fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JSON_OBJ.class);
    }
    public static class Methods {
        public static final String WEATHER_LAT_LON = "weather-current:lat-long";
        public static final String WEATHER_CITY_NAME = "weather-current:city-name";
    }
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
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
}
