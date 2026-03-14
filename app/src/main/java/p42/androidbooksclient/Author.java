package p42.androidbooksclient;

import java.util.ArrayList;

public class Author {
    private final int id;
    private final String firstname;
    private final String lastname;
    private final ArrayList<Book> books;

    public Author(final int id, final String firstname, final String lastname, final ArrayList<Book> books) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.books = books;
    }

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public ArrayList<Book> getBooks() { return books; }
}
