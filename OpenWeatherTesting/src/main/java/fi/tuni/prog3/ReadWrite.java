package fi.tuni.prog3;

import java.io.*;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class ReadWrite {
    public static boolean write(String where, String what) {
        try(var bw = new BufferedWriter(new FileWriter(where))) {
            bw.write(what);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }
    public static boolean write(String where, byte[] what) {
        try(var fs = new FileOutputStream(where)) {
            fs.write(what);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }
    public static Optional<String> read(String where) {
        StringBuilder content = new StringBuilder();
        try (var bf = new BufferedReader(new FileReader(where))) {
            String line;
            while ( (line = bf.readLine()) != null ) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return Optional.empty();
        }
        return Optional.of(content.substring(0, content.length() - 1));
    }
    public static Optional<String> readGZ(String where) {
        StringBuilder content = new StringBuilder();
        try (var bf = new BufferedReader(
                      new InputStreamReader(
                      new GZIPInputStream(
                      new FileInputStream(where))))) {
            String line;
            while ( (line = bf.readLine()) != null ) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return Optional.empty();
        }
        return Optional.of(content.toString());
    }
}
