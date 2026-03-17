package p42.androidbooksclient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private LibraryViewModel viewModel;

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //initData();
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        viewModel.loadDataFromAPI();
        setupNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            replaceFragment(new BookListFragment());
        }
    }

//    private void initData() {
//        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
//
//        String jsonString;
//
//        InputStream inputStream = this.getResources().openRawResource(R.raw.library);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while(true) {
//            try {
//                if ((line = reader.readLine()) == null) break;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            sb.append( line );
//            sb.append( '\n' );
//        }
//        jsonString = sb.toString();
//
//        viewModel.loadJson(jsonString);
//    }

    private void setupNavigation() {
        final BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            final int itemId = item.getItemId();

            if (itemId == R.id.navBooks) {
                replaceFragment(new BookListFragment());
                return true;
            } else if (itemId == R.id.navAuthors) {
                replaceFragment(new AuthorListFragment());
                return true;
            }

            return false;
        });
    }

    private void replaceFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
}