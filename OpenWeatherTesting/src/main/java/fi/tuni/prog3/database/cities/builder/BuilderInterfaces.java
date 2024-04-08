package fi.tuni.prog3.database.cities.builder;

import fi.tuni.prog3.database.cities.Cities;

public class BuilderInterfaces {

    public interface SetOptionalFields {
        SetOptionalFields setOptimisedCityListLocationTo(String what);
        SetOptionalFields setFallbackCityListLocationTo(String what);
        Cities build();
    }
    public interface SetDatabaseFileLocation {
        SetOptionalFields setDatabaseLocation(String what);
    }
    public interface SetLocation {
        SetDatabaseFileLocation setLocation(String what);
    }
}
