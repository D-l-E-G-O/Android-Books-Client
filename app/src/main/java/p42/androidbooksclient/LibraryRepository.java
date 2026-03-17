package p42.androidbooksclient;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LibraryRepository {
    private final MutableLiveData<ArrayList<Book>> booksLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Author>> authorsLiveData = new MutableLiveData<>();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .build();

    private void loadBooksFromJson(final JSONObject json) {
        final ArrayList<Book> parsedBooks = new ArrayList<>();

        try {
            final JSONArray booksArray = json.getJSONArray("");

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
                parsedBooks.add(newBook);
            }

        booksLiveData.setValue(parsedBooks);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadBooksFromJson: ", e);
        }
    }

    private void loadAuthorsFromJson(final JSONObject json) {
        final ArrayList<Author> parsedAuthors = new ArrayList<>();

        try {
            final JSONArray authorsArray = json.getJSONArray("");

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
                }

                parsedAuthors.add(new Author(authorId, firstname, lastname, authorBooks));
            }

            authorsLiveData.setValue(parsedAuthors);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadAuthorsFromJson: ", e);
        }
    }

    private void loadDataFromJson(final String jsonString) {
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

    public void fetchBooksFromAPI() {
        API service = retrofit.create(API.class);
        Call<ResponseBody> myRequest = service.getBooks();
        myRequest.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject res = new JSONObject(response.body().string());
                        loadBooksFromJson(res);
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("fetchBooksFromAPI", "onFailure: ", throwable);
            }
        });
    }

    public void fetchAuthorsFromAPI() {
        API service = retrofit.create(API.class);
        Call<ResponseBody> myRequest = service.getAuthors();
        myRequest.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject res = new JSONObject(response.body().string());
                        loadAuthorsFromJson(res);
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("fetchAuthorsFromAPI", "onFailure: ", throwable);
            }
        });
    }

    public void deleteBook(final int bookId) {
        // Logique de suppression d'un livre et mise à jour du LiveData
    }
}
