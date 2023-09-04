package offers;

import com.google.gson.Gson;
import offers.OffersApps.OffersPayload.PerkRequests;
import offers.OffersApps.OffersPayload.PerkRequests.PageRequest;
import offers.OffersApps.OffersPayload.PerkRequests.PerkArguments;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class OffersApps {

    public static void main(String[] args) throws IOException {

        var api = "https://www.visa.com.sg/gateway/api/portal/portal/perks/";

        var pageRequest = new PageRequest(0, 700);
        var perkArgs = new PerkArguments("U");
        var perkRequests = new PerkRequests("", "OFFERS", "en_sg", pageRequest, perkArgs);
        var payload = new OffersPayload("www.visa.com.sg", List.of(perkRequests));

        String payloadText = new Gson().toJson(payload);


        HttpURLConnection connection = openConnection(api, payloadText);
        Consumer<String> responseConsumer = System.out::print;
        Consumer<String> errorConsumer = System.out::print;

        send(connection, payloadText, responseConsumer, errorConsumer);

    }

    private static void send(HttpURLConnection connection, String payloadText, Consumer<String> responseConsumer, Consumer<String> errorConsumer) throws IOException {
        try (var out = connection.getOutputStream()) {
            out.write(payloadText.getBytes());
            try (var error = connection.getErrorStream(); var in = connection.getInputStream()) {
                read(error, errorConsumer);
                read(in, responseConsumer);
            }
        }
    }

    private static int read(InputStream stream, Consumer<String> consumer) throws IOException {
        int bytesRead = 0;
        byte[] data = new byte[1024];
        int ch;
        if (stream != null) {
            while ((ch = stream.read(data)) != -1) {
                consumer.accept(new String(data, 0, ch));
                bytesRead += ch;
            }
        }
        return bytesRead;
    }

    private static HttpURLConnection openConnection(String api, String payloadText) throws IOException {
        URL url = new URL(api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Length", Integer.toString(payloadText.length()));
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    public record OffersPayload(String siteId, List<PerkRequests> perkTypeRequests) {


        public record PerkRequests(String requestIdentifier,
                                   String perkType,
                                   String locale,
                                   PageRequest pageRequest,
                                   PerkArguments perkArguments) {

            public record PageRequest(int index, int limit) {

            }

            public record PerkArguments(String offerType) {

            }

        }

    }


}
