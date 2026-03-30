package p42.androidbooksclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class BookViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<ArrayList<Book>> books = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Book> selectedBook = new MutableLiveData<>();
    private final SharedPreferences prefs;

    public BookViewModel(@NonNull Application application) {
        super(application);
        this.repository = new LibraryRepository();
        this.prefs = application.getSharedPreferences("book_covers", Context.MODE_PRIVATE);
        
        // On observe les changements de la liste de livres pour réappliquer les couvertures locales
        books.observeForever(bookList -> {
            if (bookList != null) {
                for (Book b : bookList) {
                    String uri = prefs.getString("cover_" + b.getId(), null);
                    if (uri != null) {
                        b.setCoverUri(uri);
                    }
                }
            }
        });
    }

    public LiveData<ArrayList<Book>> getBooks(MutableLiveData<ArrayList<Author>> authorsLiveData) {
        repository.fetchBooks(books, authorsLiveData);
        return books;
    }

    public void selectBook(Book book) {
        selectedBook.setValue(book);
    }

    public LiveData<Book> getSelectedBook() {
        return selectedBook;
    }

    public void updateCover(int bookId, String uri) {
        prefs.edit().putString("cover_" + bookId, uri).apply();
        // Mettre à jour l'objet sélectionné si c'est le même livre
        Book selected = selectedBook.getValue();
        if (selected != null && selected.getId() == bookId) {
            selected.setCoverUri(uri);
            selectedBook.setValue(selected);
        }
    }

    public void addBook(String title, int year, int authorId, ArrayList<Integer> tagIds, MutableLiveData<ArrayList<Author>> authorsLiveData) {
        repository.addBook(title, year, authorId, tagIds, books, authorsLiveData);
    }

    public void deleteBook(int bookId, MutableLiveData<ArrayList<Author>> authorsLiveData) {
        prefs.edit().remove("cover_" + bookId).apply();
        repository.deleteBook(bookId, books, authorsLiveData);
    }
}
