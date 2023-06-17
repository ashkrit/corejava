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
        public final String playID;

        public Play(String name, String type, String playID) {
            this.name = name;
            this.type = type;
            this.playID = playID;
        }


        public PlayType playType() {
            return PlayType.toType(type);
        }


    }
}
