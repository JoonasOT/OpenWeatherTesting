package fi.tuni.prog3.API;

import fi.tuni.prog3.API.OpenWeather.WeatherMap;
import fi.tuni.prog3.security.Key;

import fi.tuni.prog3.API.OpenWeather.WeatherMap.Callables.OpenStreetMapCallable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class API {
    private final Key key;
    public API(API_Factory factory) {
        key = factory.getKey();
    }
    public Optional<Response> call(iCallable callable) {
        try {
            String url_ = addArgs(callable.url(), callable.args());

            URL url = URI.create(API.addKey(url_, key)).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try {
                con.setRequestProperty("User-Agent", ((OpenStreetMapCallable) callable).userAgent());
            } catch (Exception ignored) {}


            Response response = new Response(con);
            con.disconnect();
            return Optional.of(response);
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }
    private static String addKey(String url, Key key) {
        return url.replace("{API key}", key.getKey());
    }
    private static String addArgs(String url, Map<String, String> args) {
        if (args.isEmpty()) return url;
        var arg = args.keySet().stream().findFirst().get();
        return addArgs(url.replace(arg, args.get(arg)), args.keySet().stream().filter(a -> !Objects.equals(a, arg))
                .collect(Collectors.toMap(a -> a, args::get)));
    }
}
