package org.example.explore.purchase;

import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ProductLoader {

    public static void main(String[] args) throws IOException {
        var file = args[0];
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        RadixTree<PurchaseCollections> products = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        RadixTree<PurchaseCollections> brand = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        RadixTree<PurchaseCollections> category = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        Predicate<Purchase> productFilter = p -> !p.product.trim().isBlank();

        Path path = Paths.get(file);
        try (var lines = getLines(path)) {

            var purchaseStream = toPurchaseStream(format, lines, productFilter);

            purchaseStream
                    .forEach(p -> {
                        PurchaseCollections ep = createIfMissing(products, p);
                        ep.append(p);
                        products.put(p.product, ep);
                    });

            Comparator<LocalDate> dc = LocalDate::compareTo;

            var min = toPurchaseStream(format, getLines(path), productFilter)
                    .parallel()
                    .map(Purchase::purchaseDate)
                    .min(dc)
                    .get();

            var max = toPurchaseStream(format, getLines(path), productFilter)
                    .parallel()
                    .map(Purchase::purchaseDate)
                    .max(dc)
                    .get();


            System.out.printf("%s->%s \n", min, max);
            var days = 7;
            IntStream
                    .iterate(0, i -> i + days)
                    .mapToObj(min::plusDays)
                    .takeWhile(date -> date.isBefore(max))
                    //.limit(5)
                    .parallel()
                    .map(date -> {
                        var start = date.minusDays(days);
                        var end = date;
                        System.out.printf("Range (%s, %s) \n", start, end);
                        var items = toPurchaseStream(format, getLines(path), productFilter)
                                .filter(product -> product.purchaseDate.isAfter(start))
                                .filter(product -> product.purchaseDate.isBefore(end))
                                .sorted(Comparator.comparing(p -> p.purchaseDate))
                                .toList();
                        return new PurchaseCollections(items);
                    })
                    .filter(pc -> !pc.purchases.isEmpty())
                    .forEach(collection -> {
                        var key = collection.purchases.stream()
                                .map(p -> p.product + "(" + p.purchaseDate + ")")
                                .collect(Collectors.joining(" "));
                        products.put(key, collection);
                    });

            System.out.println("******");
            PrettyPrinter.prettyPrint((PrettyPrintable) products, System.out);

            var scanner = new Scanner(System.in);
            List<Function<String, Iterable<CharSequence>>> searchStrategy = List.of(
                    products::getClosestKeys
            );

            while (true) {
                var query = scanner.nextLine();
                System.out.printf("Result for query %s%n", query);


                searchStrategy.forEach(
                        strategy -> search(() -> strategy.apply(query), products)
                );


                System.out.println("Next Search");

            }


        }
    }

    private static Stream<String> getLines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Purchase> toPurchaseStream(DateTimeFormatter format,
                                                     Stream<String> lines,
                                                     Predicate<Purchase> productFilter) {
        return lines
                .filter(line -> !line.startsWith("1970-"))
                .filter(line -> line.endsWith("1515915625442940349"))
                .map(line -> line.split(","))
                .skip(1)

                .filter(value -> value.length == 8)

                .map(row -> parseAsProduct(format, row))
                .filter(productFilter);
    }

    private static PurchaseCollections createIfMissing(RadixTree<PurchaseCollections> products, Purchase p) {
        PurchaseCollections ep = products.getValueForExactKey(p.product);
        if (ep == null) {
            ep = new PurchaseCollections(new ArrayList<>());
        }
        return ep;
    }

    private static void search(Supplier<Iterable<CharSequence>> strategy, RadixTree<PurchaseCollections> index) {
        System.out.println("*****");
        strategy
                .get()
                .forEach(k -> System.out.printf("%s -> %s\n", k, index.getValueForExactKey(k)));
        System.out.println("*****");
    }

    private static Purchase parseAsProduct(DateTimeFormatter format, String[] row) {
        LocalDate purchaseDate = LocalDateTime.parse(row[0], format).toLocalDate();
        String productName = row[4];
        String brand = row[5];
        double price = Double.parseDouble(row[6]);
        String category = productName.split("\\.")[0];
        return Purchase.of(purchaseDate, category, productName, brand, price);
    }

    record Purchase(LocalDate purchaseDate, String category, String product, String brand, double price) {
        public static Purchase of(LocalDate purchaseDate, String category, String product, String brand, double price) {
            return new Purchase(purchaseDate, category, product, brand, price);
        }
    }

    record PurchaseCollections(List<Purchase> purchases) {

        public void append(Purchase purchase) {
            purchases.add(purchase);
        }

        @Override
        public String toString() {
            return "PurchaseCollections{" +
                    "purchases=" + purchases.size() +
                    '}';
        }
    }


}
