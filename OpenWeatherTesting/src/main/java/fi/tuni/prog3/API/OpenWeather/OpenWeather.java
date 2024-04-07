package fi.tuni.prog3.API.OpenWeather;

import fi.tuni.prog3.API.API;
import fi.tuni.prog3.API.API_Factory;
import fi.tuni.prog3.security.Key;

import java.io.IOException;

public class OpenWeather {
    public enum UNIT {
        STANDARD, METRIC, IMPERIAL;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    public enum LANG {
        AF, AL, AR, AZ, BG, CA, CZ, DA, DE, EL, EN, EU, FA, FI, FR, GL, HE, HI, HR, HU, ID, IT, JA, KR, LA, LT, MK, NO,
        NL, PL, PT, PT_BR, RO, RU, SV, SE, SK, SL, SP, ES, SR, TH, TR, UA, UK, VI, ZH_CN, ZH_TW, ZU;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static class factory implements API_Factory {
        private Key key = null;
        public factory() {
            // Get the key
            try {
                key = new Key("ApiKeys/OpenWeather");
            }
            catch (IOException e) {
                System.err.println("Key threw an error!");
                e.printStackTrace(System.err);
            }
        }
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
