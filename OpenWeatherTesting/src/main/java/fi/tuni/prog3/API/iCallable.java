package fi.tuni.prog3.API;

import java.util.HashMap;
import java.util.Map;

public interface iCallable {
    public static Map<String, String> NO_ARGS = new HashMap<>();
    public String url();
    public Map<String, String> args();
}
