package com.example.quizzz.service;

import com.example.quizzz.model.Questions;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
// this interface for declare method for taking data from api.
public interface JsonPlaceHolderApi {

    // with get method we are writing api link here. we already have base url. so we write here except base url.
    // this method returns list of questions. so we can use this list for showing questions.
    // also there is query part in this method. we will write as query when we want to another category.
    @GET("questions?limit=16")
    Call<List<Questions>> getData(@Query("categories") String categories);
}