package p42.androidbooksclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface API {
    @GET("books")
    Call<ResponseBody> getBooks();

    @GET("authors")
    Call<ResponseBody> getAuthors();
}
