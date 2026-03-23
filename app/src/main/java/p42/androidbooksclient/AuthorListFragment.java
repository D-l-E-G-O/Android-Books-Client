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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AuthorListFragment extends Fragment {

    private AuthorViewModel authorViewModel;
    private AuthorAdapter adapter;

    public AuthorListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_author_list, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeData();
    }

    private void initUI(final View view) {
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerAuthors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AuthorAdapter(author -> {
            authorViewModel.selectAuthor(author);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new AuthorDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        final FloatingActionButton fab = view.findViewById(R.id.fabAuthorAdd);
        fab.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new AuthorAddFragment())
                .addToBackStack(null)
                .commit());
    }

    private void observeData() {
        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);
        authorViewModel.getAuthors().observe(getViewLifecycleOwner(), authors -> adapter.updateAuthors(authors));
    }
}
