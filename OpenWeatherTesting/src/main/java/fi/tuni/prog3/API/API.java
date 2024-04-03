package fi.tuni.prog3.API;

import fi.tuni.prog3.security.Key;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class API {
    public record callable(String method, Map<String, String> args) {}
    public static String addKey(String url, Key key) {
        return url.replace("{API key}", key.getKey());
    }
    public static HashMap<String, String> NO_ARGS = new HashMap<>();
    private final HashMap<String, String> urls;
    public API(API_Factory factory) {
        urls = new HashMap<>(factory.getURLs());
    }
    public Response call(callable callable) throws IOException {
        String url_ = urls.get(callable.method);
        for (var arg : callable.args.keySet()) {
            url_ = url_.replace(arg, callable.args.get(arg));
        }
        URL url = URI.create(url_).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        Response response = new Response(con);
        con.disconnect();
        return response;
    }
}
