package com.org;

import java.util.ArrayList;
import java.util.List;

public class RecursiveCharacterTextSplitter2 {
    private final int chunkSize;
    private final int chunkOverlap;

    public RecursiveCharacterTextSplitter2(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    public List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        // Normalize whitespace
        text = text.replaceAll("\\s+", " ").trim();

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end);

            // If the chunk exceeds the limit, truncate it to the last space
            if (end < text.length() && text.charAt(end) != ' ') {
                int lastSpaceIndex = chunk.lastIndexOf(' ');
                if (lastSpaceIndex != -1) {
                    chunk = chunk.substring(0, lastSpaceIndex);
                    end = start + lastSpaceIndex;
                }
            }

            chunks.add(chunk);
            start = end - chunkOverlap;  // Move start index forward with overlap
        }

        return chunks;
    }

    public static void main(String[] args) {
        String text = "This is a sample text that will be split into smaller chunks. "
                + "The purpose of this text is to demonstrate how the RecursiveCharacterTextSplitter works. "
                + "It should be able to handle long paragraphs and split them into manageable sizes "
                + "while maintaining some overlap to preserve context.";

        RecursiveCharacterTextSplitter2 splitter = new RecursiveCharacterTextSplitter2(100, 20);
        List<String> chunks = splitter.splitText(text);

        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Chunk " + (i + 1) + ":\n" + chunks.get(i) + "\n");
        }
    }
}
