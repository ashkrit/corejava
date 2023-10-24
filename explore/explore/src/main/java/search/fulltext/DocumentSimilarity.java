package search.fulltext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static com.medallia.word2vec.Word2VecModel.trainer;

public class DocumentSimilarity {

    public static void main(String[] args) throws Exception {
        String file = args[0];


        Path path = Paths.get(file);

        List<String> tokens = Files.lines(path)
                .map(String::toLowerCase)
                .map(line -> line.split(" "))
                .flatMap(Stream::of)
                .toList();


        Word2VecModel model = trainer()
                .type(NeuralNetworkType.SKIP_GRAM)
                .setMinVocabFrequency(1)
                //.setWindowSize(5)
                .useHierarchicalSoftmax()
                .setNumIterations(1)
                .train(Iterables.partition(tokens, 1000));


        List<DocVector> documentsVector = Files.lines(path)
                .map(String::toLowerCase)
                .map(tok -> {
                    Searcher searcher = model.forSearch();
                    return new DocVector(tok, mean(tok.split(" "), searcher));
                }).toList();


        Scanner scanner = new Scanner(System.in);
        System.out.println("Ask Questions");
        while (true) {
            String query = scanner.nextLine();
            double[] searchVector = mean(query.toLowerCase().split(" "), model.forSearch());

            Comparator<MatchResult> comparator = Comparator.comparingDouble(m -> m.distance);
            documentsVector
                    .stream()
                    .map(doc -> new MatchResult(doc, calculateCosineSimilarity(searchVector, doc.vector)))
                    .sorted(comparator.reversed())
                    .limit(10)
                    .forEach(v -> {
                        System.out.println(v.doc.value);
                    });
        }


    }

    record MatchResult(DocVector doc, double distance) {
    }

    record DocVector(String value, double[] vector) {
    }

    public static double calculateCosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0; // Handle the case of zero vector
        } else {
            return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        }
    }

    private static double[] mean(String[] tokens, Searcher searcher) {
        double[] vector = new double[0];
        int matchCount = 0;

        for (String s : tokens) {
            if (!searcher.contains(s)) {
                System.out.println("Miss " + s);
                continue;
            }
            try {
                ImmutableList<Double> d = searcher.getRawVector(s);
                if (vector.length == 0) {
                    vector = new double[d.size()];
                }
                matchCount++;
                for (int index = 0; index < d.size(); index++) {
                    vector[index] = d.get(index);
                }

            } catch (Searcher.UnknownWordException e) {
                throw new RuntimeException(e);
            }
        }

        //mean
        for (int index = 0; index < vector.length; index++) {
            vector[index] = vector[index] / matchCount;
        }


        return vector;
    }

}
