package org.refactoring.gson;

import com.google.gson.Gson;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GsonMessage {

    public static <T> T from(String value, Class<T> t) {
        return new Gson().fromJson(value, t);
    }

    public static <T> T fromClasspath(String location, Class<T> t) {

        try {
            URI path = GsonMessage.class.getResource(location).toURI();
            String data = new String(Files.readAllBytes(Paths.get(path)));
            return new Gson().fromJson(data, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
