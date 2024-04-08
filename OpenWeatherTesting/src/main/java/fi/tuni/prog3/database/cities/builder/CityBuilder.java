package fi.tuni.prog3.database.cities.builder;

import fi.tuni.prog3.database.cities.Cities;
import fi.tuni.prog3.database.cities.builder.BuilderInterfaces.*;

public class CityBuilder implements SetOptionalFields, SetDatabaseFileLocation, SetLocation {
    private String location;
    private String databaseLocation;
    private String cityListLocation;
    private String optimizedCityListLocation;


    @Override
    public SetOptionalFields setOptimisedCityListLocationTo(String what) {
        optimizedCityListLocation = what;
        return this;
    }

    @Override
    public SetOptionalFields setFallbackCityListLocationTo(String what) {
        cityListLocation = what;
        return this;
    }

    @Override
    public SetOptionalFields setDatabaseLocation(String what) {
        databaseLocation = what;
        return this;
    }

    @Override
    public SetDatabaseFileLocation setLocation(String what) {
        location = what;
        return this;
    }

    @Override
    public Cities build() {
        return new Cities(this);
    }

    public String getLocation() {
        return location;
    }
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    public String getCityListLocation() {
        return cityListLocation;
    }

    public String getOptimizedCityListLocation() {
        return optimizedCityListLocation;
    }

}
