package socket.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockPriceProvider {

    private static String patten = "(.*)(<strong>)(.*)(</strong>)(.*)";

    private static BufferedReader toReader(URL url) throws IOException {
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    private static URL toEndPoint(String ticker) throws MalformedURLException {
        return new URL("https://www.shareinvestor.com/prices/searchbox_prices_f.html?counter=" + ticker);
    }

    private static Optional<Double> parsePrice(Matcher matcher) {
        return Optional.of(Double.parseDouble(matcher.group(3)));
    }

    public static Optional<Double> getPrice(String stockSymbol) {

        try (var reader = toReader(toEndPoint(stockSymbol))) {

            var priceLine = reader.lines()
                    .filter(x -> x.indexOf("sic_lastdone") > -1)
                    .findFirst()
                    .get();

            return toPrice(priceLine);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<Double> toPrice(String priceLine) {
        var matcher = Pattern.compile(patten).matcher(priceLine);
        return matcher.matches() ? parsePrice(matcher) : Optional.empty();
    }
}
