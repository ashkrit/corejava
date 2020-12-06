package query.timeseries;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Data Source : https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page
 * <p>
 * NY - https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2020-01.csv
 */
public class NYTaxiRides {

    public static void main(String[] args) throws Exception {

        String fileToRead = args[0];

        Stream<String> lines = Files
                .lines(Paths.get(fileToRead));

        lines.limit(10).forEach(System.out::println);

    }
}
