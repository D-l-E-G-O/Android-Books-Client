package p42.androidbooksclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private final ArrayList<Book> books = new ArrayList<>();
    private final OnBookClickListener clickListener;

    public BookAdapter(final OnBookClickListener listener) {
        this.clickListener = listener;
    }

    public void updateBooks(final ArrayList<Book> newBooks) {
        this.books.clear();
        if (newBooks != null) {
            this.books.addAll(newBooks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_view_holder, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, final int position) {
        final Book currentBook = books.get(position);
        holder.bindData(currentBook, clickListener);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
