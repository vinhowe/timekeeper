package com.base512.accountant.data.source.days;

import com.base512.accountant.data.Day;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Thomas on 10/21/2016.
 */

public interface DaysService {
    @GET("days/today")
    Call<ResponseBody> getToday();
}
