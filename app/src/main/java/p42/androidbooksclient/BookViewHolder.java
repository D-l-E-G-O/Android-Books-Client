package p42.androidbooksclient;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleTextView;
    private final TextView yearTextView;

    public BookViewHolder(@NonNull final View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.txtBookTitle);
        yearTextView = itemView.findViewById(R.id.txtBookYear);
    }

    public void bindData(final Book book, final OnBookClickListener listener) {
        titleTextView.setText(book.getTitle());

        final Integer year = book.getPublicationYear();
        if (year != null) {
            yearTextView.setText(String.valueOf(year));
        } else {
            yearTextView.setText("N/A");
        }

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.handleClick(book);
            }
        });
    }
}
