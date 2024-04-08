package fi.tuni.prog3.database.cities;

public class Params {
    public static final class FileStructure {
        public static String DatabaseLocation = "./db/Cities";
        public static String CITIES_OPTIMISED = "/cities_optimised_load";
        public static String CITY_COUNT = "/CityCount";
        public static String CITY_FALLBACK = "/city.list.json.gz";
        public static String CITY_FALLBACK_URL = "https://bulk.openweathermap.org/sample/city.list.json.gz";
    }
}
