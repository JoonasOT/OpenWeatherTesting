package fi.tuni.prog3;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Key.encryptKey("secrets/OpenWeatherKey.json", "secrets/ApiKeys/OpenWeather");

        Key kOpenWeather;
        try {
            kOpenWeather = new Key("ApiKeys/OpenWeather");
        }
        catch (IOException e) {
            System.err.println("Key threw an error!");
            return;
        }
        System.out.println(kOpenWeather.getId() + " -> " + kOpenWeather.getKey());
    }
}