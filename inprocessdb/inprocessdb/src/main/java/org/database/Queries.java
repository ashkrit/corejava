package org.database;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Queries {

    public List<Query> query = new ArrayList<>();

    public static class Query {
        public String name;
        public String description;
        public String query;

        //toString  JSON
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    //toString  JSON
    public String toString() {
        return new Gson().toJson(this);
    }

}
