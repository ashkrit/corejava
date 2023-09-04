package offers;

import com.google.gson.Gson;
import offers.OffersApps.OffersPayload.PerkRequests;
import offers.OffersApps.OffersPayload.PerkRequests.PageRequest;
import offers.OffersApps.OffersPayload.PerkRequests.PerkArguments;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class OffersApps {

    public static void main(String[] args) throws IOException, InterruptedException {

        var api = "https://www.visa.com.sg/gateway/api/portal/portal/perks/";

        var pageRequest = new PageRequest(0, 700);
        var perkArgs = new PerkArguments("U");
        var perkRequests = new PerkRequests("", "OFFERS", "en_sg", pageRequest, perkArgs);
        var payload = new OffersPayload("www.visa.com.sg", List.of(perkRequests));


        HttpResponse<String> response = new RpcClient().send(api, payload);

        var offers = new Gson().fromJson(response.body(), Offers.class);


        //System.out.println(offers);

        String blob = offers
                .offers()
                .stream()
                .map(Offers.PerksGroups.Offer::asString)
                .collect(Collectors.joining("\n"));

        Files.write(Paths.get("/Users/ashkrit/_tmp/offers/ap_offer.tsv"), blob.getBytes());


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
