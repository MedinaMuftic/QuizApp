package com.example.quizzz.model;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

// This class for modeling data. When we are taking data from api, variables in this class help us to get data.
public class Questions {
    // we need to write variable names, exactly same in the api. Or we can write serialized name and write inside it.
    @SerializedName("correctAnswer")
    public String correctAnswer;

    public String question;

    @SerializedName("incorrectAnswers")
    public JsonArray incorrectAnswers;

    // when we get incorrect answers, we are checking if we get data or null.
    public String geti1(){
        String i1 = "";
        if(incorrectAnswers != null){
            i1 = incorrectAnswers.get(0).getAsString();
        }
        return i1;
    }

    public String geti2(){
        String i2 = "";
        if(incorrectAnswers != null){
            i2 = incorrectAnswers.get(1).getAsString();
        }
        return i2;
    }

    public String geti3(){
        String i3 = "";
        if(incorrectAnswers != null){
            i3 = incorrectAnswers.get(2).getAsString();
        }
        return i3;
    }
}
