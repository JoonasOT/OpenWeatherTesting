package fi.tuni.prog3.API;

import fi.tuni.prog3.security.Key;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class IP_Getter {
    public static class Methods {
        public static final String IP_AWS = "ip:amazonaws";
        public static final String IP_HAZ = "ip:icanhazip";
        public static final String IP_MY_EXTERN = "ip:myexternalip";
        public static final String IP_ECHO = "ip:ipecho";
    }
    private static class URLs {
        private static final String IP_AWS = "https://checkip.amazonaws.com/";
        private static final String IP_HAZ = "https://ipv4.icanhazip.com/";
        private static final String IP_MY_EXTERN = "https://myexternalip.com/raw";
        private static final String IP_ECHO = "https://ipecho.net/plain";
    }
    public static class Callables {
        public static API.callable IP_AWS() {
            return new API.callable(Methods.IP_AWS, API.NO_ARGS);
        }
        public static API.callable IP_HAZIP() {
            return new API.callable(Methods.IP_HAZ, API.NO_ARGS);
        }
        public static API.callable IP_MY_EXTERNAL() {
            return new API.callable(Methods.IP_MY_EXTERN, API.NO_ARGS);
        }
        public static API.callable IP_ECHO() {
            return new API.callable(Methods.IP_ECHO, API.NO_ARGS);
        }
    }
    public static class factory implements API_Factory {
        private final Key key = new Key();
        private final HashMap<String, String> urls = new HashMap<>();
        public factory() {
            // Add all the methods
            urls.put(Methods.IP_AWS, URLs.IP_AWS);
            urls.put(Methods.IP_HAZ, URLs.IP_HAZ);
            urls.put(Methods.IP_MY_EXTERN, URLs.IP_MY_EXTERN);
            urls.put(Methods.IP_ECHO, URLs.IP_ECHO);
        }
        @Override
        public API construct() {
            return new API(this);
        }
        @Override
        public Map<String, String> getURLs() {
            return urls;
        }

        @Override
        public Key getKey() {
            return key;
        }
    }
}
