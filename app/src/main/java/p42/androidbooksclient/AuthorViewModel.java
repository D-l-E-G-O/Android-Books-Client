package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class AuthorViewModel extends ViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<ArrayList<Author>> authors = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Author> selectedAuthor = new MutableLiveData<>();

    public AuthorViewModel() {
        this.repository = new LibraryRepository();
    }

    public LiveData<ArrayList<Author>> getAuthors() {
        repository.fetchBooks(null,authors);
        return authors;
    }

    public void selectAuthor(Author author) {
        selectedAuthor.setValue(author);
    }

    public LiveData<Author> getSelectedAuthor() {
        return selectedAuthor;
    }

    public void addAuthor(String firstname, String lastname) {
        repository.addAuthor(firstname, lastname, authors);
    }

    public void deleteAuthor(int authorId, MutableLiveData<ArrayList<Book>> booksLiveData) {
        repository.deleteAuthor(authorId, authors, booksLiveData);
    }
}
