package org.refactoring;

import java.util.Arrays;

public enum PlayType {
    COMEDY("comedy"),
    TRAGEDY("tragedy");

    public final String typeName;

    PlayType(String typeName) {
        this.typeName = typeName;
    }

    public static PlayType toType(String name) {
        return Arrays.stream(values())
                .filter(f -> f.typeName.equalsIgnoreCase(name))
                .findFirst()
                .get();
    }

}
