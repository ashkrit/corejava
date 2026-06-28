package com.org;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveCharacterTextSplitter {
    private final List<String> separators;
    private final int chunkSize;
    private final int chunkOverlap;
    private final boolean keepSeparator;

    public RecursiveCharacterTextSplitter(List<String> separators, int chunkSize, int chunkOverlap, boolean keepSeparator) {
        this.separators = new ArrayList<>(separators);
        this.separators.add(""); // Add empty string as the final separator
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.keepSeparator = keepSeparator;
    }

    public List<String> splitText(String text) {
        List<String> finalChunks = new ArrayList<>();
        recursiveSplit(text, 0, finalChunks);
        return mergeSmallerChunks(finalChunks);
    }

    private void recursiveSplit(String text, int separatorIndex, List<String> finalChunks) {
        String separator = separators.get(separatorIndex);
        if (separator.isEmpty()) {
            // Base case: split by character, but try to keep full sentences
            splitByCharacter(text, finalChunks);
            return;
        }

        List<TextChunk> splits = splitOnSeparator(text, separator, keepSeparator);
        for (TextChunk split : splits) {
            if (split.text.length() <= chunkSize) {
                finalChunks.add(split.text);
            } else {
                recursiveSplit(split.text, separatorIndex + 1, finalChunks);
            }
        }
    }

    private void splitByCharacter(String text, List<String> finalChunks) {
        Pattern sentencePattern = Pattern.compile(".*?[.!?]\\s+", Pattern.DOTALL);
        Matcher sentenceMatcher = sentencePattern.matcher(text);

        int lastEnd = 0;
        StringBuilder currentChunk = new StringBuilder();

        while (sentenceMatcher.find()) {
            String sentence = sentenceMatcher.group();
            if (currentChunk.length() + sentence.length() > chunkSize) {
                if (currentChunk.length() > 0) {
                    finalChunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
                if (sentence.length() > chunkSize) {
                    // If a single sentence is longer than chunkSize, split it
                    for (int i = 0; i < sentence.length(); i += chunkSize - chunkOverlap) {
                        int end = Math.min(sentence.length(), i + chunkSize);
                        finalChunks.add(sentence.substring(i, end).trim());
                    }
                } else {
                    currentChunk.append(sentence);
                }
            } else {
                currentChunk.append(sentence);
            }
            lastEnd = sentenceMatcher.end();
        }

        // Add any remaining text
        if (lastEnd < text.length()) {
            currentChunk.append(text.substring(lastEnd));
        }

        if (currentChunk.length() > 0) {
            finalChunks.add(currentChunk.toString().trim());
        }
    }

    private List<TextChunk> splitOnSeparator(String text, String separator, boolean keepSeparator) {
        List<TextChunk> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(Pattern.quote(separator));
        Matcher matcher = pattern.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            if (lastEnd != matcher.start()) {
                result.add(new TextChunk(text.substring(lastEnd, matcher.start()), false));
            }
            if (keepSeparator) {
                result.add(new TextChunk(matcher.group(), true));
            }
            lastEnd = matcher.end();
        }

        if (lastEnd != text.length()) {
            result.add(new TextChunk(text.substring(lastEnd), false));
        }

        return result;
    }

    private List<String> mergeSmallerChunks(List<String> chunks) {
        List<String> mergedChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String chunk : chunks) {
            if (currentChunk.length() + chunk.length() + 1 > chunkSize) {
                if (currentChunk.length() > 0) {
                    mergedChunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
                if (chunk.length() > chunkSize) {
                    mergedChunks.add(chunk.trim());
                    continue;
                }
            }
            if (currentChunk.length() > 0) {
                currentChunk.append(" ");
            }
            currentChunk.append(chunk);
        }

        if (currentChunk.length() > 0) {
            mergedChunks.add(currentChunk.toString().trim());
        }

        return mergedChunks;
    }

    private static class TextChunk {
        final String text;
        final boolean isSeparator;

        TextChunk(String text, boolean isSeparator) {
            this.text = text;
            this.isSeparator = isSeparator;
        }
    }

    public static void main(String[] args) {
        List<String> separators = Arrays.asList("\n\n", "\n", " ");
        RecursiveCharacterTextSplitter splitter = new RecursiveCharacterTextSplitter(separators, 100, 20, false);

        String text = "This is a sample text. It has multiple sentences. Some are short. Others are longer and more complex.\n\n" +
                "This is a new paragraph. It also contains multiple sentences. We want to ensure that sentences are kept intact whenever possible.\n" +
                "However, if a sentence is too long, it may need to be split. This is a very long sentence that exceeds the chunk size and demonstrates how the splitter handles such cases while still trying to maintain readability and context.";

        List<String> chunks = splitter.splitText(text);

        System.out.println("Chunks:");
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Chunk " + (i + 1) + ": " + chunks.get(i));
        }
    }
}