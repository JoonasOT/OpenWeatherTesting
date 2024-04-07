package fi.tuni.prog3.database;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

public class MaxMindGeoIP2 implements Database<MaxMindGeoIP2.GeoLocation> {
    public record GeoLocation(Country country, City city) {}
    File database;

    public MaxMindGeoIP2(String location) {
        database = new File(location);
    }

    @Override
    public Optional<GeoLocation> get(String ip) {
        try {
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            CityResponse res = reader.city(InetAddress.getByName(ip));
            return Optional.of(new GeoLocation(res.getCountry(), res.getCity()));
        }
        catch (IOException | GeoIp2Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return Optional.empty();
        }
    }
}
