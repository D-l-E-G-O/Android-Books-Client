package p42.androidbooksclient;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AuthorViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameTextView;

    public AuthorViewHolder(@NonNull final View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.txtAuthorName);
    }

    public void bindData(final Author author, final OnAuthorClickListener listener) {
        final String fullName = author.getFirstname() + " " + author.getLastname();
        nameTextView.setText(fullName);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.click_handle(author);
            }
        });
    }
}
