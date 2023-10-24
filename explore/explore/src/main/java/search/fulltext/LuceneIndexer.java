package search.fulltext;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LuceneIndexer {

    public static void main(String[] args) throws Exception {
        String index = args[0];
        String files = args[1];


        Path indexDir = Paths.get(index);
        Directory directory = FSDirectory.open(indexDir);
        IndexWriter writer = createIndex(directory);

        AtomicInteger counter = new AtomicInteger();
        Stream<String> lines = Files.lines(Path.of(files));


        lines
                .forEach(line -> writeToIndex(writer, counter, line));

        writer.close();

        System.out.println("Written at " + index + " Docs " + counter);


    }

    private static IndexWriter createIndex(Directory directory) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, config);
    }

    private static void writeToIndex(IndexWriter writer, AtomicInteger counter, String line) {
        try {
            Document doc = new Document();
            doc.add(new TextField("content", line, Field.Store.YES));
            writer.addDocument(doc);
            counter.incrementAndGet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
