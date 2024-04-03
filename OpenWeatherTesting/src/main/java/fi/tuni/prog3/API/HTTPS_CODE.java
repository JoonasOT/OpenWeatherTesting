package fi.tuni.prog3.API;

public enum HTTPS_CODE {
    INFO (100, 199),
    SUCCESS (200, 299),
    REDIRECTION (300, 399),
    CLIENT_ERROR (400, 499),
    SERVER_ERROR (500, 599);

    public final int min;
    public final int max;
    HTTPS_CODE(int min, int max) {
        this.min = min;
        this.max = max;
    }
    public static HTTPS_CODE getCode(int val) throws RuntimeException {
        for( var code : HTTPS_CODE.values() ) {
            if( code.min <= val && val <= code.max ) {
                return code;
            }
        }
        throw new RuntimeException("HTTPS code '" + Integer.toString(val) + "' is invalid!");
    }
}
