package p42.androidbooksclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TagViewModel extends ViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<ArrayList<Tag>> tags = new MutableLiveData<>(new ArrayList<>());

    public TagViewModel() {
        this.repository = new LibraryRepository();
    }

    public LiveData<ArrayList<Tag>> getTags() {
        repository.fetchTags(tags);
        return tags;
    }
}
