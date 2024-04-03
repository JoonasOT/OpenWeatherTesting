package fi.tuni.prog3;

import fi.tuni.prog3.API.API;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");

        Key ApiKey_OW;
        try {
            ApiKey_OW = new Key("ApiKeys/OpenWeather");
        }
        catch (IOException e) {
            System.err.println("Key threw an error!");
            e.printStackTrace(System.err);
            return;
        }

        API OpenWeatherAPI;
        try {
            OpenWeatherAPI = new API(ApiKey_OW, "http://api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid=%s");
            System.out.println(OpenWeatherAPI.call("GET"));
        } catch (IOException e) {
            System.err.println("Api call failed!");
            return;
        }

    }
}