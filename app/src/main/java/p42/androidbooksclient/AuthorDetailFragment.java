package p42.androidbooksclient;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AuthorDetailFragment extends Fragment {

    private AuthorViewModel authorViewModel;
    private BookViewModel bookViewModel;
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

        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        adapter = new BookAdapter(book -> {
            bookViewModel.selectBook(book);
            Navigation.findNavController(view).navigate(R.id.action_authorDetailFragment_to_bookDetailFragment);
        });
        recyclerBooks.setAdapter(adapter);

        authorViewModel.getSelectedAuthor().observe(getViewLifecycleOwner(), author -> {
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
                        authorViewModel.deleteAuthor(currentAuthor.getId(), (MutableLiveData<ArrayList<Book>>) bookViewModel.getBooks(null));
                        Toast.makeText(getContext(), "Auteur supprimé", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
}
