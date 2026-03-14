package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {
    private final LibraryRepository repository;

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
}
