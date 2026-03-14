package p42.androidbooksclient;

import java.util.ArrayList;

public class Book {
    private final int id;
    private final String title;
    private final Integer publication_year;
    private final int authorId;
    private final ArrayList<Tag> tags;

    public Book(final int id, final String title, final Integer publication_year, final int authorId, final ArrayList<Tag> tags) {
        this.id = id;
        this.title = title;
        this.publication_year = publication_year;
        this.authorId = authorId;
        this.tags = tags;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Integer getPublicationYear() { return publication_year; }
    public int getAuthorId() { return authorId; }
    public ArrayList<Tag> getTags() { return tags; }
}
