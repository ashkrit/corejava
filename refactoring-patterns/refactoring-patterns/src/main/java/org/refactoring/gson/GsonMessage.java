package org.refactoring.gson;

import com.google.gson.Gson;

public class GsonMessage {

    public static <T> T from(String value, Class<T> t) {
        return new Gson().fromJson(value, t);
    }
}
