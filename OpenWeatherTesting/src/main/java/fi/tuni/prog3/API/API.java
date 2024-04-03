package fi.tuni.prog3.API;

import fi.tuni.prog3.Key;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;

public class API {
    private Key key;
    private URL url;
    public API(Key API_key, String API_url) throws MalformedURLException {
        key = API_key;
        url = URI.create(String.format(API_url, key.getKey())).toURL();
    }
    public Response call(String method) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        Response response = new Response(con);
        con.disconnect();
        return response;
    }
}