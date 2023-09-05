package offers;

import offers.Offers.PerksGroups.Offer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Offers {
    public List<PerksGroups> perksGroups;

    public List<Offer> offers() {
        return perksGroups
                .stream()
                .flatMap(r -> r.offers().stream())
                .toList();
    }


    public static class PerksGroups {
        public int resultCount;
        public List<Map<String, Object>> perks;

        public List<Offer> offers() {
            return perks.stream().map(Offer::new).toList();
        }


        public static class Offer {

            private final Map<String, Object> input;

            public Offer(Map<String, Object> input) {
                this.input = input;
            }


            public String sourceId() {
                return (String) input.get("sourceId");
            }

            public String sourceType() {
                return (String) input.get("sourceType");
            }

            public String shortDescription() {
                return (String) input.get("shortDescription");
            }

            public String title() {
                return (String) input.get("title");
            }

            public String merchantName() {
                return (String) input.get("merchantName");
            }

            public LocalDate startDate() {
                return _toLocalDate("startDate");
            }

            public LocalDate endDate() {
                return _toLocalDate("endDate");
            }

            public List<String> tags() {
                return asList(input, "tags");
            }

            private List<String> asList(Map<String, Object> row, String tags) {
                return (List<String>) row.get(tags);
            }

            public List<String> categories() {
                Map<String, String> cache = Cache.cache("category");
                return
                        asList(metaData(), "categories")
                                .stream()
                                .map(value -> cache.getOrDefault(value.trim(), value.trim()))
                                .toList();
            }

            public List<String> cardProductTypes() {

                Map<String, String> cache = Cache.cache("card_types");
                return
                        asList(metaData(), "cardProductTypes")
                                .stream()
                                .map(value -> cache.getOrDefault(value.trim(), value.trim()))
                                .toList();
            }

            public List<String> cardPaymentTypes() {

                Map<String, String> cache = Cache.cache("payment_type");
                return
                        asList(metaData(), "cardPaymentTypes")
                                .stream()
                                .map(value -> cache.getOrDefault(value.trim(), value.trim()))
                                .toList();
            }

            public List<String> redemptionChannels() {

                Map<String, String> cache = Cache.cache("redem_channel");
                return
                        asList(metaData(), "redemptionChannels")
                                .stream()
                                .map(value -> cache.getOrDefault(value.trim(), value.trim()))
                                .toList();
            }

            public List<String> redemptionCountries() {

                Map<String, String> cache = Cache.cache("country");
                return
                        asList(metaData(), "redemptionCountries")
                                .stream()
                                .map(value -> cache.getOrDefault(value.trim(), value.trim()))
                                .toList();
            }

            public List<String> businessSegments() {
                return asList(metaData(), "businessSegments");
            }

            public Map<String, Object> customAttributes() {
                Map<String, Object> meta = metaData();
                return (Map<String, Object>) meta.get("customAttributes");
            }

            private Map<String, Object> metaData() {
                return (Map<String, Object>) input.get("metaData");
            }

            private LocalDate _toLocalDate(String name) {
                Double value = (Double) input.get(name);
                var time = Instant.ofEpochMilli(value.longValue());
                return LocalDate.ofInstant(time, ZoneId.systemDefault());
            }

            public String asString() {

                return String.join("\t",
                        sourceId(),
                        shortDescription(),
                        title(),
                        merchantName(),
                        startDate().toString(),
                        endDate().toString(),
                        String.join(",", tags()),
                        String.join(",", categories()),
                        String.join(",", cardProductTypes()),
                        String.join(",", cardPaymentTypes()),
                        String.join(",", redemptionChannels()),
                        String.join(",", redemptionCountries()),
                        String.join(",", businessSegments()),
                        (String) customAttributes().get("programName"),
                        (String) customAttributes().get("offerCopy")

                );

            }
        }

    }
}
