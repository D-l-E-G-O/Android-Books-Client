package p42.androidbooksclient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookListFragment extends Fragment {

    private LibraryViewModel viewModel;
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

        adapter = new BookAdapter(book -> Toast.makeText(getContext(), "Clicked: " + book.getTitle(), Toast.LENGTH_SHORT).show());
        recyclerView.setAdapter(adapter);

        final FloatingActionButton fab = view.findViewById(R.id.fabBookAdd);
        fab.setOnClickListener(v -> Toast.makeText(getContext(), "Add Book Clicked", Toast.LENGTH_SHORT).show());
    }

    private void observeData() {
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        viewModel.observeBooks().observe(getViewLifecycleOwner(),
                books -> adapter.updateBooks(new ArrayList<>(books)));
    }
}