package p42.androidbooksclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class BookDetailFragment extends Fragment {

    private BookViewModel bookViewModel;
    private AuthorViewModel authorViewModel;
    private Book currentBook;
    private ImageView imgCover;
    private SharedPreferences prefs;

    public BookDetailFragment() {}

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && currentBook != null) {
                        try {
                            requireActivity().getContentResolver().takePersistableUriPermission(
                                    selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            
                            prefs.edit().putString("cover_" + currentBook.getId(), selectedImageUri.toString()).apply();

                            currentBook.setCoverUri(selectedImageUri.toString());
                            imgCover.setImageURI(selectedImageUri);
                            
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Erreur lors de la sélection de l'image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity().getSharedPreferences("book_covers", Context.MODE_PRIVATE);

        final TextView txtTitle = view.findViewById(R.id.txtDetailTitle);
        final TextView txtAuthor = view.findViewById(R.id.txtDetailAuthor);
        final TextView txtYear = view.findViewById(R.id.txtDetailYear);
        final TextView txtTags = view.findViewById(R.id.txtDetailTags);
        final Button btnDelete = view.findViewById(R.id.btnDeleteBook);
        final Button btnSetCover = view.findViewById(R.id.btnSetCover);
        imgCover = view.findViewById(R.id.imgBookCover);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);

        bookViewModel.getSelectedBook().observe(getViewLifecycleOwner(), book -> {
            if (book != null) {
                currentBook = book;
                txtTitle.setText(book.getTitle());
                txtYear.setText(book.getPublicationYear() != null ? "Publié en : " + book.getPublicationYear() : "Année inconnue");

                String uriStr = book.getCoverUri();
                if (uriStr == null) {
                    uriStr = prefs.getString("cover_" + book.getId(), null);
                }

                if (uriStr != null) {
                    imgCover.setImageURI(Uri.parse(uriStr));
                } else {
                    imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                authorViewModel.getAuthors().observe(getViewLifecycleOwner(), authors -> {
                    for (Author a : authors) {
                        if (a.getId() == book.getAuthorId()) {
                            txtAuthor.setText(String.format("Par %s %s", a.getFirstname(), a.getLastname()));
                            break;
                        }
                    }
                });

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

        btnSetCover.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImageLauncher.launch(intent);
        });

        btnDelete.setOnClickListener(v -> {
            if (currentBook == null) return;

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer ce livre ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        prefs.edit().remove("cover_" + currentBook.getId()).apply();

                        bookViewModel.deleteBook(currentBook.getId(), (MutableLiveData<ArrayList<Author>>) authorViewModel.getAuthors());
                        Toast.makeText(getContext(), "Livre supprimé", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
}
