package bitfiddle;

import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cardinalty {


    public static void main(String[] args) {

        Map<String, Map<String, RoaringBitmap>> index = new HashMap<>();

        Arrays.asList(
                new Object[]{0, "GB", "Financials"},
                new Object[]{1, "DE", "Manufacturing"},
                new Object[]{2, "FR", "Agriculturals"},
                new Object[]{3, "FR", "Financials"},
                new Object[]{4, "GB", "Energies"}
        ).forEach(row -> {
            int id = Integer.parseInt(row[0].toString());

            getIndex(index, "country", row[1].toString())
                    .add(id);
            getIndex(index, "sector", row[2].toString()).add(id);

        });

        System.out.println(index);

        RoaringBitmap i1 = index.get("country").get("FR");
        RoaringBitmap i2 = index.get("country").get("GB");
        i1.or(i2);
        System.out.println(i1);

    }

    private static RoaringBitmap getIndex(Map<String, Map<String, RoaringBitmap>> index, String indexName, String columnVlaue) {
        Map<String, RoaringBitmap> columnIndex = index.computeIfAbsent(indexName, c -> new HashMap<>());
        return columnIndex.computeIfAbsent(columnVlaue, c -> new RoaringBitmap());
    }


}
