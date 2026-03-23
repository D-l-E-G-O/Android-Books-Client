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
    private final MutableLiveData<ArrayList<Book>> booksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<Author>> authorsLiveData = new MutableLiveData<>(new ArrayList<>());

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .build();

    private final API service = retrofit.create(API.class);

    private void loadDataFromJsons(final JSONArray authorsArray, final JSONArray booksArray) {
        final ArrayList<Author> allAuthors = new ArrayList<>();
        final ArrayList<Book> allBooks = new ArrayList<>();
        final Map<Integer, Author> authorMap = new HashMap<>();

        try {
            // 1. Parser d'abord tous les auteurs du JSON "authors"
            for (int i = 0; i < authorsArray.length(); i++) {
                JSONObject authorJson = authorsArray.getJSONObject(i);
                int id = authorJson.getInt("id");
                String firstname = authorJson.getString("firstname");
                String lastname = authorJson.getString("lastname");
                
                // On initialise l'auteur avec une liste de livres vide
                Author author = new Author(id, firstname, lastname, new ArrayList<>());
                allAuthors.add(author);
                authorMap.put(id, author);
            }

            // 2. Parser les livres du JSON "books" et les lier aux auteurs
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

                // Liaison avec l'auteur
                Author author = authorMap.get(authorId);
                if (author != null) {
                    author.getBooks().add(book);
                }
            }

            // Mise à jour des LiveData
            booksLiveData.postValue(allBooks);
            authorsLiveData.postValue(allAuthors);

        } catch (final JSONException e) {
            Log.e("LibraryRepository", "loadDataFromJsons Error", e);
        }
    }

    public MutableLiveData<ArrayList<Book>> fetchBooks() {
        return booksLiveData;
    }

    public MutableLiveData<ArrayList<Author>> fetchAuthors() {
        return authorsLiveData;
    }


    public void fetchDataFromAPI() {
        service.getAuthors().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> responseAuthors) {
                if (responseAuthors.isSuccessful() && responseAuthors.body() != null) {
                    try {
                        String authorsStr = responseAuthors.body().string();
                        final JSONArray authorsJson = new JSONArray(authorsStr);

                        // Une fois les auteurs récupérés, on récupère les livres
                        service.getData("author").enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> responseBooks) {
                                if (responseBooks.isSuccessful() && responseBooks.body() != null) {
                                    try {
                                        JSONArray booksJson = new JSONArray(responseBooks.body().string());
                                        loadDataFromJsons(authorsJson, booksJson);
                                    } catch (IOException | JSONException e) {
                                        Log.e("fetchDataFromAPI", "Parsing books error", e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                                Log.e("fetchDataFromAPI", "Books onFailure", throwable);
                            }
                        });
                    } catch (IOException | JSONException e) {
                        Log.e("fetchDataFromAPI", "Parsing authors error", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("fetchDataFromAPI", "Authors onFailure", throwable);
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

        service.addBook(authorId, body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject resJson = new JSONObject(response.body().string());
                        int id = resJson.getInt("id");
                        Book newBook = new Book(id, title, publicationYear, authorId, new ArrayList<>());
                        
                        ArrayList<Book> currentBooks = booksLiveData.getValue();
                        if (currentBooks != null) {
                            currentBooks.add(newBook);
                            booksLiveData.postValue(currentBooks);
                        }

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
                    } catch (IOException | JSONException e) {
                        Log.e("addBook", "Parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("addBook", "onFailure", throwable);
            }
        });
    }

    public void deleteBook(final int bookId) {
        service.deleteBook(bookId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ArrayList<Book> currentBooks = booksLiveData.getValue();
                    if (currentBooks != null) {
                        Book bookToRemove = null;
                        for (Book b : currentBooks) {
                            if (b.getId() == bookId) {
                                bookToRemove = b;
                                break;
                            }
                        }
                        if (bookToRemove != null) {
                            currentBooks.remove(bookToRemove);
                            booksLiveData.postValue(currentBooks);

                            ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                            if (currentAuthors != null) {
                                for (Author a : currentAuthors) {
                                    if (a.getId() == bookToRemove.getAuthorId()) {
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

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("deleteBook", "onFailure", throwable);
            }
        });
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

        service.addAuthor(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject resJson = new JSONObject(response.body().string());
                        int id = resJson.getInt("id");
                        Author newAuthor = new Author(id, firstname, lastname, new ArrayList<>());
                        
                        ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                        if (currentAuthors != null) {
                            currentAuthors.add(newAuthor);
                            authorsLiveData.postValue(currentAuthors);
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("addAuthor", "Parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("addAuthor", "onFailure", throwable);
            }
        });
    }

    public void deleteAuthor(final int authorId) {
        service.deleteAuthor(authorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ArrayList<Author> currentAuthors = authorsLiveData.getValue();
                    if (currentAuthors != null) {
                        currentAuthors.removeIf(a -> a.getId() == authorId);
                        authorsLiveData.postValue(currentAuthors);
                    }
                    ArrayList<Book> currentBooks = booksLiveData.getValue();
                    if (currentBooks != null) {
                        currentBooks.removeIf(b -> b.getAuthorId() == authorId);
                        booksLiveData.postValue(currentBooks);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("deleteAuthor", "onFailure", throwable);
            }
        });
    }
}
