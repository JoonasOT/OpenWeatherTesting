package fi.tuni.prog3.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Key {
    public static String SECRET_LOCATION = "secrets/";
    private String id = "";
    private String key = "";
    private static String generatePassword(String gen) {
        byte[] ba = gen.getBytes();
        assert !gen.isEmpty();
        byte x = (byte) (gen.chars().reduce((l, r) -> l ^ r).getAsInt() / (int)(Math.pow(2, Byte.SIZE) - 1));
        for (int i : IntStream.range(0, ba.length).toArray()) ba[i] ^= x;
        return new String(ba);
    }
    public Key(String file) throws IOException, SecurityException {
        String decryptedString;
        try {
            var in = new FileInputStream(SECRET_LOCATION + file);
            var t = Files.readAttributes(Path.of(SECRET_LOCATION + file), BasicFileAttributes.class)
                    .lastModifiedTime();
            decryptedString = new String(Encryption.decryptAES256(in.readAllBytes(), generatePassword(
                                            t.toString().substring(0, 18))), UTF_8);
            in.close();
        } catch (RuntimeException e) {
            throw new SecurityException(e);
        }

        Gson gson = new Gson();
        Key fromJson = gson.fromJson(decryptedString, new TypeToken<Key>(){}.getType());
        id = fromJson.id;
        key = fromJson.key;
    }
    public Key() {
        id = "Unknown";
        key = "N/A";
    }
    public String getKey() { return key; }

    public String getId() { return id; }
    public static boolean encryptKey(String keyIn, String keyOut) {
        StringBuilder content = new StringBuilder();
        String tmp;
        try (var in = new BufferedReader(new FileReader(keyIn));
            var out = new FileOutputStream(keyOut)) {
            while ( (tmp = in.readLine()) != null ) {
                content.append(tmp);
            }

            byte[] encrypted_content = Encryption.encryptAES256(content.toString()
                                                .getBytes(StandardCharsets.UTF_8),
                                                generatePassword(
                                                        FileTime.fromMillis(System.currentTimeMillis()).toString()
                                                        .substring(0, 18)
                                                )
            );
            out.write(encrypted_content);
        } catch(IOException e) { return false; }
        return true;
    }
}
