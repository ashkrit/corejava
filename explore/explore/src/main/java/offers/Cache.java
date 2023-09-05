package offers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cache {

    public static Map<String, Map<String, String>> cache = new HashMap<>();

    public static Map<String, String> cache(String group) {
        return cache.computeIfAbsent(group, Cache::_load);
    }

    private static Map<String, String> _load(String group) {
        try {
            System.out.println("Loading " + group);
            var in = Cache.class.getResource("/lookup/" + group + ".tsv");
            Path location = Paths.get(in.toURI());
            try (var lines = Files.lines(location)) {
                return lines.skip(1)
                        .map(String::trim)
                        .filter(line -> line.length() > 0)
                        .map(row -> row.split(";"))
                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
