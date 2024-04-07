package fi.tuni.prog3.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class Response {
    private int statusCode;
    private boolean ok = true;
    private String data = "";
    private byte[] bytes = null;
    public Response(HttpURLConnection connection) {
        try {
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            ok = false;
        }

        getData(connection);
    }
    private void getData(HttpURLConnection connection) {

        try (var stream = CallWasOK() ? connection.getInputStream() : connection.getErrorStream()){
            bytes = stream.readAllBytes();
            data = new String(bytes, StandardCharsets.UTF_8).replace("\n", "");
        } catch (Exception ignored) {}
    }
    public byte[] getAllBytes() {
        return bytes;
    }

    public boolean ConnectionIsOk() {
        return ok;
    }
    public boolean CallWasOK() {
        return HTTPS_CODE.getCode(statusCode) == HTTPS_CODE.SUCCESS;
    }

    public int getStatus() {
        return statusCode;
    }

    public String getData() {
        return data;
    }
}
