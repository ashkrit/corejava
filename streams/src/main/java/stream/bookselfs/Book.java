package stream.bookselfs;

import java.time.Year;
import java.util.List;

public class Book {
    final String title;
    final List<String> authors;
    final int[] pageCounts;
    final Topic topic;
    final Year pubDate;
    final double height;


    public Book(String title, List<String> authors, int[] pageCounts, Year pubDate, double height, Topic topic) {
        this.title = title;
        this.authors = authors;
        this.pageCounts = pageCounts;
        this.topic = topic;
        this.pubDate = pubDate;
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public int[] getPageCounts() {
        return pageCounts;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public Topic getTopic() {
        return topic;
    }

    public Year getPubDate() {
        return pubDate;
    }
}
