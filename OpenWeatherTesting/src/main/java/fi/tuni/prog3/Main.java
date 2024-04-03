package fi.tuni.prog3;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.OpenWeather;
import fi.tuni.prog3.API.Response;
import fi.tuni.prog3.security.Key;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");

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