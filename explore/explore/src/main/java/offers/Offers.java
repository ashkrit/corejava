package offers;

import offers.Offers.PerksGroups.Offer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

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
                return asList(metaData(), "categories");
            }

            public List<String> cardProductTypes() {
                return asList(metaData(), "cardProductTypes");
            }

            public List<String> cardPaymentTypes() {
                return asList(metaData(), "cardPaymentTypes");
            }

            public List<String> redemptionChannels() {
                return asList(metaData(), "redemptionChannels");
            }

            public List<String> redemptionCountries() {
                return asList(metaData(), "redemptionCountries");
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
        }

    }
}
