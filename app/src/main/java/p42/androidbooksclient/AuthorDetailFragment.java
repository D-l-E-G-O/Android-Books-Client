package p42.androidbooksclient;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AuthorDetailFragment extends Fragment {

    private LibraryViewModel viewModel;
    private Author currentAuthor;
    private BookAdapter adapter;

    public AuthorDetailFragment() {}

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_author_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView txtName = view.findViewById(R.id.txtDetailAuthorName);
        final Button btnDelete = view.findViewById(R.id.btnDeleteAuthor);
        final RecyclerView recyclerBooks = view.findViewById(R.id.recyclerAuthorBooks);

        recyclerBooks.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        adapter = new BookAdapter(book -> {
            viewModel.selectBook(book);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new BookDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        recyclerBooks.setAdapter(adapter);

        viewModel.getSelectedAuthor().observe(getViewLifecycleOwner(), author -> {
            if (author != null) {
                currentAuthor = author;
                txtName.setText(String.format("%s %s", author.getFirstname(), author.getLastname()));
                adapter.updateBooks(author.getBooks());
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentAuthor == null) return;

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer cet auteur ? Cela supprimera également tous ses livres.")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        viewModel.deleteAuthor(currentAuthor.getId());
                        Toast.makeText(getContext(), "Auteur supprimé", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
}
