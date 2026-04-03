package p42.androidbooksclient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookListFragment extends Fragment {

    private BookViewModel bookViewModel;
    private AuthorViewModel authorViewModel;
    private BookAdapter adapter;

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeData();
    }

    private void initUI(final View view) {
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookAdapter(book -> {
            bookViewModel.selectBook(book);
            Navigation.findNavController(view).navigate(R.id.action_bookListFragment_to_bookDetailFragment);
        });
        recyclerView.setAdapter(adapter);

        final FloatingActionButton fab = view.findViewById(R.id.fabBookAdd);
        fab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_bookListFragment_to_bookAddFragment));
    }

    private void observeData() {
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);

        // On a besoin du LiveData des auteurs pour que le repo puisse faire la liaison lors du fetchBooks
        // On récupère donc d'abord le LiveData de AuthorViewModel (qui peut être vide au début)
        bookViewModel.getBooks((androidx.lifecycle.MutableLiveData<ArrayList<Author>>) authorViewModel.getAuthors()).observe(getViewLifecycleOwner(),
                books -> adapter.updateBooks(new ArrayList<>(books)));
    }
}
