package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class BookViewModel extends ViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<ArrayList<Book>> books = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Book> selectedBook = new MutableLiveData<>();

    public BookViewModel() {
        this.repository = new LibraryRepository();
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

    public void addBook(String title, int year, int authorId, MutableLiveData<ArrayList<Author>> authorsLiveData) {
        repository.addBook(title, year, authorId, books, authorsLiveData);
    }

    public void deleteBook(int bookId, MutableLiveData<ArrayList<Author>> authorsLiveData) {
        repository.deleteBook(bookId, books, authorsLiveData);
    }
}
