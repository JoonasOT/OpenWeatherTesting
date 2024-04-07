package fi.tuni.prog3.API.OpenWeather.callables;

import java.util.Map;

public class ZipCodeCallable extends BaseCallable {
    public ZipCodeCallable(String url, int zipCode) {
        super(url, Map.of("{zip code}", Integer.toString(zipCode)));
    }
    public ZipCodeCallable addCountryCode(String code) {
        args.put("{zip code}", args.get("{zip code}") + "," + code);
        return this;
    }
}
