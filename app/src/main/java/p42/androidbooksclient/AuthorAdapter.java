package p42.androidbooksclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorViewHolder> {

    private final ArrayList<Author> authors = new ArrayList<>();
    private final OnAuthorClickListener clickListener;

    public AuthorAdapter(final OnAuthorClickListener listener) {
        this.clickListener = listener;
    }

    public void updateAuthors(final ArrayList<Author> newAuthors) {
        this.authors.clear();
        if (newAuthors != null) {
            this.authors.addAll(newAuthors);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_view_holder, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AuthorViewHolder holder, final int position) {
        final Author currentAuthor = authors.get(position);
        holder.bindData(currentAuthor, clickListener);
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }
}
