package org.refactoring;

import java.util.List;


public class Plays {

    @Override
    public String toString() {
        return "Plays{" +
                "plays=" + plays +
                '}';
    }

    public final List<Play> plays;

    public Plays(List<Play> plays) {
        this.plays = plays;
    }

    public static class Play {

        @Override
        public String toString() {
            return "Play{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public final String name;
        public final String type;

        public Play(String name, String type) {
            this.name = name;
            this.type = type;
        }


    }
}
