package fi.tuni.prog3.API;

import java.net.MalformedURLException;
import java.util.Map;

public interface API_Factory {
    public Map<String, String> getURLs();
    public API construct() throws MalformedURLException;
}
