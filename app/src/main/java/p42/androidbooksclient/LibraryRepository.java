package p42.androidbooksclient;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LibraryRepository {
    private final MutableLiveData<ArrayList<Book>> booksLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Author>> authorsLiveData = new MutableLiveData<>();

    public void loadDataFromJson(final String jsonString) {
        final ArrayList<Author> parsedAuthors = new ArrayList<>();
        final ArrayList<Book> parsedBooks = new ArrayList<>();

        try {
            final JSONObject rootObject = new JSONObject(jsonString);
            final JSONArray authorsArray = rootObject.getJSONArray("authors");

            for (int i = 0; i < authorsArray.length(); i++) {
                final JSONObject authorJson = authorsArray.getJSONObject(i);

                final int authorId = authorJson.getInt("id");
                final String firstname = authorJson.getString("firstname");
                final String lastname = authorJson.getString("lastname");

                final ArrayList<Book> authorBooks = new ArrayList<>();
                final JSONArray booksArray = authorJson.getJSONArray("books");

                for (int j = 0; j < booksArray.length(); j++) {
                    final JSONObject bookJson = booksArray.getJSONObject(j);

                    final int bookId = bookJson.getInt("id");
                    final String title = bookJson.getString("title");

                    final Integer publicationYear = bookJson.has("publication_year") && !bookJson.isNull("publication_year")
                            ? bookJson.getInt("publication_year")
                            : null;

                    final int bookAuthorId = bookJson.getInt("authorId");

                    final ArrayList<Tag> bookTags = new ArrayList<>();
                    final JSONArray tagsArray = bookJson.getJSONArray("tags");

                    for (int k = 0; k < tagsArray.length(); k++) {
                        final JSONObject tagJson = tagsArray.getJSONObject(k);
                        final int tagId = tagJson.getInt("id");
                        final String tagName = tagJson.getString("name");

                        bookTags.add(new Tag(tagId, tagName));
                    }

                    final Book newBook = new Book(bookId, title, publicationYear, bookAuthorId, bookTags);
                    authorBooks.add(newBook);
                    parsedBooks.add(newBook);
                }

                parsedAuthors.add(new Author(authorId, firstname, lastname, authorBooks));
            }

            authorsLiveData.setValue(parsedAuthors);
            booksLiveData.setValue(parsedBooks);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadDataFromJson: ", e);
        }
    }

    public MutableLiveData<ArrayList<Book>> fetchBooks() {
        return booksLiveData;
    }

    public MutableLiveData<ArrayList<Author>> fetchAuthors() {
        return authorsLiveData;
    }

    public void deleteBook(final int bookId) {
        // Logique de suppression d'un livre et mise à jour du LiveData
    }
}
