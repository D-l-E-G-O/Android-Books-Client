package p42.androidbooksclient;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookDetailFragment extends Fragment {

    private BookViewModel bookViewModel;
    private AuthorViewModel authorViewModel;
    private Book currentBook;

    public BookDetailFragment() {}

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView txtTitle = view.findViewById(R.id.txtDetailTitle);
        final TextView txtYear = view.findViewById(R.id.txtDetailYear);
        final TextView txtTags = view.findViewById(R.id.txtDetailTags);
        final Button btnDelete = view.findViewById(R.id.btnDeleteBook);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);

        bookViewModel.getSelectedBook().observe(getViewLifecycleOwner(), book -> {
            if (book != null) {
                currentBook = book;
                txtTitle.setText(book.getTitle());
                txtYear.setText(book.getPublicationYear() != null ? "Publié en : " + book.getPublicationYear() : "Année inconnue");

                StringBuilder tagsString = new StringBuilder("Tags : ");
                if (book.getTags() != null && !book.getTags().isEmpty()) {
                    for (int i = 0; i < book.getTags().size(); i++) {
                        tagsString.append(book.getTags().get(i).getName());
                        if (i < book.getTags().size() - 1) {
                            tagsString.append(", ");
                        }
                    }
                } else {
                    tagsString.append("Aucun");
                }
                txtTags.setText(tagsString.toString());
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentBook == null) return;

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer ce livre ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        bookViewModel.deleteBook(currentBook.getId(), (MutableLiveData<ArrayList<Author>>) authorViewModel.getAuthors());
                        Toast.makeText(getContext(), "Livre supprimé", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
}
