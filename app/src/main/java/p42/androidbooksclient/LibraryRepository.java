package p42.androidbooksclient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LibraryRepository {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .build();

    private final API service = retrofit.create(API.class);

    private void loadDataFromJsons(final JSONArray authorsArray, final JSONArray booksArray, 
                                   final MutableLiveData<ArrayList<Author>> authorsLiveData, 
                                   final MutableLiveData<ArrayList<Book>> booksLiveData) {
        final ArrayList<Author> allAuthors = new ArrayList<>();
        final ArrayList<Book> allBooks = new ArrayList<>();
        final Map<Integer, Author> authorMap = new HashMap<>();

        try {
            for (int i = 0; i < authorsArray.length(); i++) {
                JSONObject authorJson = authorsArray.getJSONObject(i);
                int id = authorJson.getInt("id");
                String firstname = authorJson.getString("firstname");
                String lastname = authorJson.getString("lastname");
                Author author = new Author(id, firstname, lastname, new ArrayList<>());
                allAuthors.add(author);
                authorMap.put(id, author);
            }

            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject bookJson = booksArray.getJSONObject(i);
                int bookId = bookJson.getInt("id");
                String title = bookJson.getString("title");
                Integer pubYear = bookJson.has("publication_year") && !bookJson.isNull("publication_year")
                        ? bookJson.getInt("publication_year") : null;
                int authorId = bookJson.getInt("authorId");

                ArrayList<Tag> bookTags = new ArrayList<>();
                JSONArray tagsArray = bookJson.optJSONArray("tags");
                if (tagsArray != null) {
                    for (int k = 0; k < tagsArray.length(); k++) {
                        JSONObject t = tagsArray.getJSONObject(k);
                        bookTags.add(new Tag(t.getInt("id"), t.getString("name")));
                    }
                }

                Book book = new Book(bookId, title, pubYear, authorId, bookTags);
                allBooks.add(book);

                Author author = authorMap.get(authorId);
                if (author != null) {
                    author.getBooks().add(book);
                }
            }

            if (authorsLiveData != null) authorsLiveData.postValue(allAuthors);
            if (booksLiveData != null) booksLiveData.postValue(allBooks);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadDataFromJsons Error", e);
        }
    }

    public void fetchAuthors(final MutableLiveData<ArrayList<Author>> authorsLiveData) {
        service.getAuthors().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray authorsJson = new JSONArray(response.body().string());
                        ArrayList<Author> allAuthors = new ArrayList<>();
                        for (int i = 0; i < authorsJson.length(); i++) {
                            JSONObject a = authorsJson.getJSONObject(i);
                            allAuthors.add(new Author(a.getInt("id"), a.getString("firstname"), a.getString("lastname"), new ArrayList<>()));
                        }
                        authorsLiveData.postValue(allAuthors);
                    } catch (IOException | JSONException e) {
                        Log.e("fetchAuthors", "Error", e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("fetchAuthors", "onFailure", t);
            }
        });
    }

    public void fetchBooks(final MutableLiveData<ArrayList<Book>> booksLiveData, final MutableLiveData<ArrayList<Author>> authorsLiveData) {
        service.getAuthors().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> responseAuthors) {
                if (responseAuthors.isSuccessful() && responseAuthors.body() != null) {
                    try {
                        final JSONArray authorsJson = new JSONArray(responseAuthors.body().string());
                        service.getData("author").enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> responseBooks) {
                                if (responseBooks.isSuccessful() && responseBooks.body() != null) {
                                    try {
                                        JSONArray booksJson = new JSONArray(responseBooks.body().string());
                                        loadDataFromJsons(authorsJson, booksJson, authorsLiveData, booksLiveData);
                                    } catch (IOException | JSONException e) {
                                        Log.e("fetchBooks", "Error", e);
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                Log.e("fetchBooks", "Books onFailure", t);
                            }
                        });
                    } catch (IOException | JSONException e) {
                        Log.e("fetchBooks", "Authors error", e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("fetchBooks", "Authors onFailure", t);
            }
        });
    }

    public void fetchTags(final MutableLiveData<ArrayList<Tag>> tagsLiveData) {
        service.getTags().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray tagsJson = new JSONArray(response.body().string());
                        ArrayList<Tag> allTags = new ArrayList<>();
                        for (int i = 0; i < tagsJson.length(); i++) {
                            JSONObject t = tagsJson.getJSONObject(i);
                            allTags.add(new Tag(t.getInt("id"), t.getString("name")));
                        }
                        tagsLiveData.postValue(allTags);
                    } catch (IOException | JSONException e) {
                        Log.e("fetchTags", "Error", e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("fetchTags", "onFailure", t);
            }
        });
    }

    public void addAuthor(final String firstname, final String lastname, final MutableLiveData<ArrayList<Author>> authorsLiveData) {
        JSONObject json = new JSONObject();
        try {
            json.put("firstname", firstname);
            json.put("lastname", lastname);
        } catch (JSONException e) { return; }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
        service.addAuthor(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject resJson = new JSONObject(response.body().string());
                        Author newAuthor = new Author(resJson.getInt("id"), firstname, lastname, new ArrayList<>());
                        ArrayList<Author> current = authorsLiveData.getValue();
                        if (current != null) {
                            current.add(newAuthor);
                            authorsLiveData.postValue(current);
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("addAuthor", "Error", e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("addAuthor", "onFailure", t);
            }
        });
    }

    public void deleteAuthor(final int authorId, final MutableLiveData<ArrayList<Author>> authorsLiveData, final MutableLiveData<ArrayList<Book>> booksLiveData) {
        service.deleteAuthor(authorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                    if (currentAuthors != null) {
                        currentAuthors.removeIf(a -> a.getId() == authorId);
                        authorsLiveData.postValue(currentAuthors);
                    }
                    if (booksLiveData != null) {
                        ArrayList<Book> currentBooks = booksLiveData.getValue();
                        if (currentBooks != null) {
                            currentBooks.removeIf(b -> b.getAuthorId() == authorId);
                            booksLiveData.postValue(currentBooks);
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("deleteAuthor", "onFailure", t);
            }
        });
    }

    public void addBook(final String title, final int publicationYear, final int authorId, final ArrayList<Integer> tagIds, final MutableLiveData<ArrayList<Book>> booksLiveData, final MutableLiveData<ArrayList<Author>> authorsLiveData) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("publication_year", publicationYear);
            if (tagIds != null && !tagIds.isEmpty()) {
                json.put("tagIds", new JSONArray(tagIds));
            }
        } catch (JSONException e) { return; }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
        service.addBook(authorId, body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject resJson = new JSONObject(response.body().string());
                        Book newBook = new Book(resJson.getInt("id"), title, publicationYear, authorId, new ArrayList<>());
                        
                        ArrayList<Book> currentBooks = booksLiveData.getValue();
                        if (currentBooks != null) {
                            currentBooks.add(newBook);
                            booksLiveData.postValue(currentBooks);
                        }
                        if (authorsLiveData != null) {
                            ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                            if (currentAuthors != null) {
                                for (Author a : currentAuthors) {
                                    if (a.getId() == authorId) {
                                        a.getBooks().add(newBook);
                                        authorsLiveData.postValue(currentAuthors);
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("addBook", "Error", e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("addBook", "onFailure", t);
            }
        });
    }

    public void deleteBook(final int bookId, final MutableLiveData<ArrayList<Book>> booksLiveData, final MutableLiveData<ArrayList<Author>> authorsLiveData) {
        service.deleteBook(bookId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ArrayList<Book> currentBooks = booksLiveData.getValue();
                    if (currentBooks != null) {
                        Book removed = null;
                        for (Book b : currentBooks) { if (b.getId() == bookId) { removed = b; break; } }
                        if (removed != null) {
                            currentBooks.remove(removed);
                            booksLiveData.postValue(currentBooks);
                            if (authorsLiveData != null) {
                                ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                                if (currentAuthors != null) {
                                    for (Author a : currentAuthors) {
                                        if (a.getId() == removed.getAuthorId()) {
                                            a.getBooks().removeIf(b -> b.getId() == bookId);
                                            authorsLiveData.postValue(currentAuthors);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("deleteBook", "onFailure", t);
            }
        });
    }
}
