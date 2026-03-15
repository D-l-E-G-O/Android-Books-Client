package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<Book> selectedBook = new MutableLiveData<>();

    public LibraryViewModel() {
        this.repository = new LibraryRepository();
    }

    public LiveData<ArrayList<Book>> observeBooks() {
        return repository.fetchBooks();
    }

    public LiveData<ArrayList<Author>> observeAuthors() {
        return repository.fetchAuthors();
    }

    public void loadJson(final String jsonString) {
        repository.loadDataFromJson(jsonString);
    }

    public void selectBook(final Book book) {
        selectedBook.setValue(book);
    }

    public LiveData<Book> getSelectedBook() {
        return selectedBook;
    }
}
