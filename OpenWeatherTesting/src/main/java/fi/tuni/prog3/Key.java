package fi.tuni.prog3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.AEADBadTagException;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Key {
    private static String password = "njeå £mwe8r 0qp9å0ë @$@}";
    public static String SECRET_LOCATION = "secrets/";
    private String id = null;
    private String key = null;

    public Key(String file) throws IOException, SecurityException {
        String decryptedString;
        try {
            var in = new FileInputStream(SECRET_LOCATION + file);
            decryptedString = new String(Encryption.decryptAES256(in.readAllBytes(), password), UTF_8);
            in.close();
        } catch (RuntimeException e) {
            throw new SecurityException(e);
        }

        Gson gson = new Gson();
        Key fromJson = gson.fromJson(decryptedString, new TypeToken<Key>(){}.getType());
        id = fromJson.id;
        key = fromJson.key;
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
                                                .getBytes(StandardCharsets.UTF_8), password);
            out.write(encrypted_content);
        } catch(IOException e) { return false; }
        return true;
    }
}
