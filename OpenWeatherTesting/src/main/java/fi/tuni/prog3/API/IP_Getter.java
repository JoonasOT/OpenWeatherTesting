package fi.tuni.prog3.API;

import fi.tuni.prog3.security.Key;

import java.util.Map;

public class IP_Getter {
    private static class URLs {
        private static final String IP_AWS = "https://checkip.amazonaws.com/";
        private static final String IP_HAZ = "https://ipv4.icanhazip.com/";
        private static final String IP_MY_EXTERN = "https://myexternalip.com/raw";
        private static final String IP_ECHO = "https://ipecho.net/plain";
    }
    public static class Callables {
        public record IP_BASE_CALLABLE(String url, Map<String, String> args) implements iCallable {};
        public static iCallable IP_AWS() { return new IP_BASE_CALLABLE(URLs.IP_AWS, iCallable.NO_ARGS); }
        public static iCallable IP_HAZIP() {
            return new IP_BASE_CALLABLE(URLs.IP_HAZ, iCallable.NO_ARGS);
        }
        public static iCallable IP_MY_EXTERNAL() {
            return new IP_BASE_CALLABLE(URLs.IP_MY_EXTERN, iCallable.NO_ARGS);
        }
        public static iCallable IP_ECHO() {
            return new IP_BASE_CALLABLE(URLs.IP_ECHO, iCallable.NO_ARGS);
        }
    }
    public static class factory implements API_Factory {
        private final Key key = new Key();
        public factory() {}
        @Override
        public API construct() {
            return new API(this);
        }

        @Override
        public Key getKey() {
            return key;
        }
    }
}
