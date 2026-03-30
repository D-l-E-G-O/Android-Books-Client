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

    @GET("authors")
    Call<ResponseBody> getAuthors();

    @GET("tags")
    Call<ResponseBody> getTags();

    @POST("authors/{author_id}/books")
    Call<ResponseBody> addBook(@Path("author_id") int authorId, @Body RequestBody body);

    @DELETE("books/{book_id}")
    Call<ResponseBody> deleteBook(@Path("book_id") int bookId);

    @POST("authors")
    Call<ResponseBody> addAuthor(@Body RequestBody body);

    @DELETE("authors/{author_id}")
    Call<ResponseBody> deleteAuthor(@Path("author_id") int authorId);

    @POST("books/{book_id}/tags/{tag_id}")
    Call<ResponseBody> addTagToBook(@Path("book_id") int bookId, @Path("tag_id") int tagId);
}
