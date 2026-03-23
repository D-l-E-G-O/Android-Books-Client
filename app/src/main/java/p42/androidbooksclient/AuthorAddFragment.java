package p42.androidbooksclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AuthorAddFragment extends Fragment {

    private AuthorViewModel authorViewModel;

    public AuthorAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_author_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authorViewModel = new ViewModelProvider(requireActivity()).get(AuthorViewModel.class);

        final TextInputEditText editFirstname = view.findViewById(R.id.editFirstname);
        final TextInputEditText editLastname = view.findViewById(R.id.editLastname);
        final Button btnSave = view.findViewById(R.id.btnSaveAuthor);

        btnSave.setOnClickListener(v -> {
            String firstname = Objects.requireNonNull(editFirstname.getText()).toString().trim();
            String lastname = Objects.requireNonNull(editLastname.getText()).toString().trim();

            if (firstname.isEmpty() || lastname.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authorViewModel.addAuthor(firstname, lastname);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
