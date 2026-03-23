package p42.androidbooksclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class BookAddFragment extends Fragment {

    private BookViewModel bookViewModel;
    private AuthorViewModel authorViewModel;
    private Spinner spinnerAuthors;
    private ArrayList<Author> authorsList = new ArrayList<>();

    public BookAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);

        final TextInputEditText editTitle = view.findViewById(R.id.editBookTitle);
        final TextInputEditText editYear = view.findViewById(R.id.editPubYear);
        spinnerAuthors = view.findViewById(R.id.spinnerAuthors);
        final Button btnSave = view.findViewById(R.id.btnSaveBook);

        authorViewModel.getAuthors().observe(getViewLifecycleOwner(), authors -> {
            this.authorsList = authors;
            ArrayList<String> authorNames = new ArrayList<>();
            for (Author a : authors) {
                authorNames.add(a.getFirstname() + " " + a.getLastname());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, authorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAuthors.setAdapter(adapter);
        });

        btnSave.setOnClickListener(v -> {
            String title = Objects.requireNonNull(editTitle.getText()).toString().trim();
            String yearStr = Objects.requireNonNull(editYear.getText()).toString().trim();
            int selectedAuthorIndex = spinnerAuthors.getSelectedItemPosition();

            if (title.isEmpty() || yearStr.isEmpty() || selectedAuthorIndex == -1) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int year = Integer.parseInt(yearStr);
            int authorId = authorsList.get(selectedAuthorIndex).getId();

            bookViewModel.addBook(title, year, authorId, (MutableLiveData<ArrayList<Author>>) authorViewModel.getAuthors());
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
