package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<Book> selectedBook = new MutableLiveData<>();
    private final MutableLiveData<Author> selectedAuthor = new MutableLiveData<>();

    public LibraryViewModel() {
        this.repository = new LibraryRepository();
    }

    public LiveData<ArrayList<Book>> observeBooks() {
        return repository.fetchBooks();
    }

    public LiveData<ArrayList<Author>> observeAuthors() {
        return repository.fetchAuthors();
    }

    public void loadDataFromAPI() {
        repository.fetchDataFromAPI();
    }

    public void selectBook(final Book book) {
        selectedBook.setValue(book);
    }

    public LiveData<Book> getSelectedBook() {
        return selectedBook;
    }

    public void selectAuthor(final Author author) {
        selectedAuthor.setValue(author);
    }

    public LiveData<Author> getSelectedAuthor() {
        return selectedAuthor;
    }

    public void addAuthor(final String firstname, final String lastname) {
        repository.addAuthor(firstname, lastname);
    }

    public void addBook(final String title, final int publicationYear, final int authorId) {
        repository.addBook(title, publicationYear, authorId);
    }
}
