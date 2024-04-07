package fi.tuni.prog3.database;

import java.util.Optional;

public interface Database <T> {
    public Optional<T> get(String what);
}
