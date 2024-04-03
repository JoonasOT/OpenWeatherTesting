package fi.tuni.prog3.API;

import fi.tuni.prog3.security.Key;

import java.net.MalformedURLException;
import java.util.Map;

public interface API_Factory {
    public Map<String, String> getURLs();
    public Key getKey();
    public API construct() throws MalformedURLException;
}
