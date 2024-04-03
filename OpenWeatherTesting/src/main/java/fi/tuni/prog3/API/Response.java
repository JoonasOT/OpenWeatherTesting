package fi.tuni.prog3.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Response {
    private int statusCode;
    private boolean ok = true;
    private StringBuilder data = new StringBuilder();
    public Response(HttpURLConnection connection) {
        try {
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            ok = false;
        }

        getData(connection);
    }
    private void getData(HttpURLConnection connection) {
        try (var dataStream = new BufferedReader(new InputStreamReader(CallWasOK() ?
                                                        connection.getInputStream() :
                                                        connection.getErrorStream()))){
            String inputLine;
            while ((inputLine = dataStream.readLine()) != null) {
                data.append(inputLine);
            }
        } catch (Exception ignored) {}
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
        return data.toString();
    }
}
