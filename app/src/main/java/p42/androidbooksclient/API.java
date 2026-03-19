package p42.androidbooksclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("books")
    Call<ResponseBody> getData(@Query("include") String include);
}
