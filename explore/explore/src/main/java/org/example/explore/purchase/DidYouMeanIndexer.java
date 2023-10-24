package org.example.explore.purchase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DidYouMeanIndexer {


    public static void main(String[] args) throws Exception {

        String index = args[0];
        String spellIndex = args[0];

        Path indexDir = Paths.get(index);
        Directory directory = FSDirectory.open(indexDir);

        IndexReader reader = DirectoryReader.open(directory);

        String wordsFilePath = "path_to_text_file_containing_words";
        Path wordsFile = Paths.get(wordsFilePath);

        SpellChecker spellChecker = new SpellChecker(directory);
        Dictionary dictionary = new LuceneDictionary(reader, "contents");
        spellChecker.indexDictionary(dictionary, new IndexWriterConfig(),true);

        String queryTerm = "pract"; // Misspelled word
        int suggestionsNumber = 5;

        String[] suggestions = spellChecker.suggestSimilar(queryTerm, suggestionsNumber);
        System.out.println("Suggestions for '" + queryTerm + "':");
        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }

        spellChecker.close();

    }


}
