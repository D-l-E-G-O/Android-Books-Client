package p42.androidbooksclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class BookAddFragment extends Fragment {

    private BookViewModel bookViewModel;
    private AuthorViewModel authorViewModel;
    private TagViewModel tagViewModel;
    private Spinner spinnerAuthors;
    private LinearLayout containerTags;
    private ArrayList<Author> authorsList = new ArrayList<>();
    private ArrayList<Tag> tagsList = new ArrayList<>();
    private final ArrayList<CheckBox> tagCheckBoxes = new ArrayList<>();

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
        tagViewModel = new ViewModelProvider(requireActivity()).get(TagViewModel.class);

        final TextInputEditText editTitle = view.findViewById(R.id.editBookTitle);
        final TextInputEditText editYear = view.findViewById(R.id.editPubYear);
        spinnerAuthors = view.findViewById(R.id.spinnerAuthors);
        containerTags = view.findViewById(R.id.containerTags);
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

        tagViewModel.getTags().observe(getViewLifecycleOwner(), tags -> {
            this.tagsList = tags;
            containerTags.removeAllViews();
            tagCheckBoxes.clear();
            for (Tag t : tags) {
                CheckBox cb = new CheckBox(requireContext());
                cb.setText(t.getName());
                containerTags.addView(cb);
                tagCheckBoxes.add(cb);
            }
        });

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String yearStr = editYear.getText().toString().trim();
            int selectedAuthorIndex = spinnerAuthors.getSelectedItemPosition();

            if (title.isEmpty() || yearStr.isEmpty() || selectedAuthorIndex == -1) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int year = Integer.parseInt(yearStr);
            int authorId = authorsList.get(selectedAuthorIndex).getId();

            ArrayList<Integer> selectedTagIds = new ArrayList<>();
            for (int i = 0; i < tagCheckBoxes.size(); i++) {
                if (tagCheckBoxes.get(i).isChecked()) {
                    selectedTagIds.add(tagsList.get(i).getId());
                }
            }

            bookViewModel.addBook(title, year, authorId, selectedTagIds, (MutableLiveData<ArrayList<Author>>) authorViewModel.getAuthors());
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
