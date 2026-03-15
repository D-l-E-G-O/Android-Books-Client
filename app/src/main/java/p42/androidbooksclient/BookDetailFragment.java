package p42.androidbooksclient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailFragment extends Fragment {

    private LibraryViewModel viewModel;

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

        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        viewModel.getSelectedBook().observe(getViewLifecycleOwner(), book -> {
            if (book != null) {
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
            Toast.makeText(getContext(), "Livre supprimé", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}