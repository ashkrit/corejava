package search.fulltext;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class LuceneSearcher {

    public static void main(String[] args) throws Exception {
        String index = args[0];


        Path indexDir = Paths.get(index);
        Directory directory = FSDirectory.open(indexDir);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("content", analyzer);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String queryString = scanner.nextLine();

            Query query = parser.parse(queryString);


            TopDocs results = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : results.scoreDocs) {

                var doc = searcher.doc(scoreDoc.doc);
                var exp = searcher.explain(query, scoreDoc.doc);
                //System.out.println(exp.toString());
                System.out.printf("%s \n", doc.get("content"));
            }
        }


        //reader.close();

    }
}
