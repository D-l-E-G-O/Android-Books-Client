package p42.androidbooksclient;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    @GET("books")
    Call<ResponseBody> getData(@Query("include") String include);

    @POST("book")
    Call<ResponseBody> addBook(@Body RequestBody body);

    @DELETE("book/{book_id}")
    Call<ResponseBody> deleteBook(@Path("book_id") int bookId);

    @POST("author")
    Call<ResponseBody> addAuthor(@Body RequestBody body);

    @DELETE("author/{author_id}")
    Call<ResponseBody> deleteAuthor(@Path("author_id") int authorId);
}
