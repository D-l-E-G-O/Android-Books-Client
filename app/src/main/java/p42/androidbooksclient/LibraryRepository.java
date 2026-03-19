package p42.androidbooksclient;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    private final API service = retrofit.create(API.class);

    private void loadDataFromJson(final JSONArray booksArray) {
        final ArrayList<Book> allBooks = new ArrayList<>();
        // Maps pour regrouper les livres par auteur et éviter les doublons d'auteurs
        final Map<Integer, ArrayList<Book>> authorBooksMap = new HashMap<>();
        final Map<Integer, JSONObject> authorJsonMap = new HashMap<>();

        try {
            for (int i = 0; i < booksArray.length(); i++) {
                final JSONObject bookJson = booksArray.getJSONObject(i);

                // 1. Parser le Livre
                final int bookId = bookJson.getInt("id");
                final String title = bookJson.getString("title");
                final Integer pubYear = bookJson.has("publication_year") && !bookJson.isNull("publication_year")
                        ? bookJson.getInt("publication_year") : null;
                final int bookAuthorId = bookJson.getInt("authorId");

                final ArrayList<Tag> bookTags = new ArrayList<>();
                final JSONArray tagsArray = bookJson.optJSONArray("tags");
                if (tagsArray != null) {
                    for (int k = 0; k < tagsArray.length(); k++) {
                        JSONObject t = tagsArray.getJSONObject(k);
                        bookTags.add(new Tag(t.getInt("id"), t.getString("name")));
                    }
                }

                final Book book = new Book(bookId, title, pubYear, bookAuthorId, bookTags);
                allBooks.add(book);

                // 2. Parser l'Auteur imbriqué
                if (bookJson.has("author") && !bookJson.isNull("author")) {
                    final JSONObject authorJson = bookJson.getJSONObject("author");
                    final int aId = authorJson.getInt("id");

                    if (!authorBooksMap.containsKey(aId)) {
                        authorBooksMap.put(aId, new ArrayList<>());
                        authorJsonMap.put(aId, authorJson);
                    }
                    // On ajoute ce livre à la liste de cet auteur
                    authorBooksMap.get(aId).add(book);
                }
            }

            // 3. Créer la liste finale des auteurs uniques avec leurs livres respectifs
            final ArrayList<Author> allAuthors = new ArrayList<>();
            for (Integer aId : authorJsonMap.keySet()) {
                final JSONObject aJson = authorJsonMap.get(aId);
                allAuthors.add(new Author(
                        aId,
                        aJson.getString("firstname"),
                        aJson.getString("lastname"),
                        authorBooksMap.get(aId)
                ));
            }

            // Mise à jour des LiveData
            booksLiveData.postValue(allBooks);
            authorsLiveData.postValue(allAuthors);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadDataFromJson Error", e);
        }
    }

    public MutableLiveData<ArrayList<Book>> fetchBooks() {
        return booksLiveData;
    }

    public MutableLiveData<ArrayList<Author>> fetchAuthors() {
        return authorsLiveData;
    }

    public void fetchDataFromAPI() {
        // Appelle /books?include=author
        Call<ResponseBody> myRequest = service.getData("author");
        myRequest.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray res = new JSONArray(response.body().string());
                        loadDataFromJson(res);
                    } catch (IOException | JSONException e) {
                        Log.e("fetchDataFromAPI", "Parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("fetchDataFromAPI", "onFailure: ", throwable);
            }
        });
    }

    public void addBook(final String title, final int publicationYear, final int authorId) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("publication_year", publicationYear);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Call<ResponseBody> myRequest = service.addBook(body);

        myRequest.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray res = new JSONArray(response.body().string());
                        int bookId = res.getInt(0);
                        Book newBook = new Book(bookId, title, publicationYear, authorId, new ArrayList<>());
                        Objects.requireNonNull(booksLiveData.getValue()).add(newBook);
                    } catch (IOException | JSONException e) {
                        Log.e("addBook", "Parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("addBook", "onFailure: ", throwable);
            }
        });
    }

    public void deleteBook(final int bookId) {
        Objects.requireNonNull(booksLiveData.getValue()).removeIf(book -> (book.getId() == bookId));
    }

    public void addAuthor(final String firstname, final String lastname) {
        JSONObject json = new JSONObject();
        try {
            json.put("firstname", firstname);
            json.put("lastname", lastname);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Call<ResponseBody> myRequest = service.addAuthor(body);
        myRequest.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray res = new JSONArray(response.body().string());
                        int authorId = res.getInt(0);
                        Author newAuthor = new Author(authorId, firstname, lastname, new ArrayList<>());
                        Objects.requireNonNull(authorsLiveData.getValue()).add(newAuthor);
                    } catch (IOException | JSONException e) {
                        Log.e("addAuthor", "Parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("addAuthor", "onFailure: ", throwable);
            }
        });
    }

    public void deleteAuthor(final int authorId) {
        Objects.requireNonNull(authorsLiveData.getValue()).removeIf(author -> (author.getId() == authorId));
        Objects.requireNonNull(booksLiveData.getValue()).removeIf(book -> (book.getAuthorId() == authorId));
    }
}
