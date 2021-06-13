package com.example.quizzz.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzz.R;
import com.example.quizzz.model.Questions;
import com.example.quizzz.service.JsonPlaceHolderApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    // we are declare xml items here. also other variables here.
    TextView scoreNumber;
    TextView timeText;
    TextView scoreTitleText;
    TextView timeCounter;
    TextView questionTitleText;
    TextView questionBlankText;
    Button answerA;
    Button answerB;
    Button answerC;
    Button answerD;
    int score=0;
    TextView welcomeUser;
    Button startQuiz;
    Button[] allButtons;
    CountDownTimer countDownTimer;
    int total = 0;
    String category = "";
    String difficulty = "";
    String name = "";
    TextView pointTitleText;
    TextView pointNumberText;
    String point = "0";
    int pointInt = 0;

    ArrayList<Questions> questions;

    // for taking data from api, we are declare base url here. also we declare retrofit here.
    private String BASE_URL = "https://trivia.willfry.co.uk/api/";
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // we initialize variables here with find view by id.
        timeText = findViewById(R.id.timeText);
        scoreNumber = findViewById(R.id.scoreNumber);
        scoreTitleText = findViewById(R.id.scoreTitleText);
        timeCounter = findViewById(R.id.timeCounter);
        questionBlankText = findViewById(R.id.questionBlankText);
        questionTitleText = findViewById(R.id.questionTitleText);
        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);
        welcomeUser = findViewById(R.id.welcomeUser);
        startQuiz = findViewById(R.id.startQuiz);
        pointTitleText = findViewById(R.id.pointTitleText);
        pointNumberText = findViewById(R.id.pointNumberText);

        // we are getting intent here. user's name, category here. we are showing user's name.
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        category = intent.getStringExtra("category");
        //difficulty = intent.getStringExtra("difficulty");
        welcomeUser.setText("Welcome " + name);

        // these gson for initialize retrofit.
        Gson gson = new GsonBuilder().setLenient().create();

        // we are initialize retrofit here. we are adding base url, gson and we are just build.
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // when user click start quiz, this methods will work.
        // also we are setting start quiz clickable false, then user cannot click again and again
        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                startQuiz.setClickable(false);
                timerStart();
            }
        });
    }

    // this method for choosing category. we already get category name in the first screen.
    // if user choose general, we are adding general knowledge as query in the api call method.
    // this method returns call with list of questions.
    public Call<List<Questions>> chooseGame(){
        // we are declare api with using retrofit. also using interface that we created already.
        JsonPlaceHolderApi jsonApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Questions>> call;

        if(category.equals("General")){
            call = jsonApi.getData("general_knowledge");
        }
        else if(category.equals("History")){
            call = jsonApi.getData("history");
        }
        else if(category.equals("Food and Drink")){
            call = jsonApi.getData("food_and_drink");
        }
        else if(category.equals("Geography")){
            call = jsonApi.getData("geography");
        }
        else if(category.equals("Art and Literature")){
            call = jsonApi.getData("literature");
        }
        else if(category.equals("Movies")){
            call = jsonApi.getData("movies");
        }
        else if(category.equals("Music")){
            call = jsonApi.getData("music");
        }
        else if(category.equals("Science")){
            call = jsonApi.getData("science");
        }
        else{
            call = jsonApi.getData("general_knowledge");
        }
        return call;
    }

    // this method loading questions from api.
    public void loadData(){

        // if total number is greater 15, quiz must be over. so we are showing toast message, reset the total number, calculate point,
        // also we are using handler for stop counter. we are waiting 1,5 second and showing the result.
        // also we are closing buttons listeners. then user cannot click buttons when quiz is over.
        if(total >= 15){
            Toast.makeText(this, "Quiz over!", Toast.LENGTH_SHORT).show();
            total = 0;
            calculatePoint();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDownTimer.cancel();
                    seeResults();
                }
            },1500);
            closeButtonsListeners();
        }
        else{

            // if total number is less than 15, then we are getting call method.
            // we are making enqueue for getting data.

            Call<List<Questions>> call = chooseGame();

            call.enqueue(new Callback<List<Questions>>() {
                @Override
                public void onResponse(Call<List<Questions>> call, Response<List<Questions>> response) {

                    // we are checking if our response is successful, because maybe we didn't get data.
                    // if is okey, we are using response to get data. we are storing in response list.
                    // because we declare call method with list of questions in api, we are using here with list of questions.
                    // we are storing this response list in questions list which we created already.

                    if(response.isSuccessful()){
                        List<Questions> responseList = response.body();
                        questions = new ArrayList<>(responseList);

                        // here we are getting just one questions which is total number's index.
                        // and we store that question in que variable.
                        Questions que = questions.get(total);

                        // we have set values to button method. we are using that method and we are using that que variable to add questions
                        // correct answer and incorrect answers.
                        setValuesToButton(que.question,que.correctAnswer,que.geti1(),que.geti2(),que.geti3());
                        // this button listeners method is getting one parameter which is correct answer. we are giving correct answer here.
                        buttonListeners(que.correctAnswer);

                        // we are calculate the point and clear the buttons colors for next question.
                        calculatePoint();
                        cleanButtonColors();

                    }
                }

                @Override
                public void onFailure(Call<List<Questions>> call, Throwable t) {
                    // if we have any error, we are writing in logs.
                    t.printStackTrace();
                }
            });
        }
        // then we are increase total variable for getting next question in list.
        total++;
    }

    // in this method, we are changing int to string. because we are showing that in textview.
    // if user got the question right, we are showing point 1 and showing score 10.
    // and setting these score and point in textviews.
    public void calculatePoint(){
        point = "0";
        pointInt = 0;
        pointInt = score * 10;
        point = String.valueOf(pointInt);
        pointNumberText.setText(point);
    }

    // for seeing result, we are getting score, category, name and point.
    // then we are using intent to go result screen.
    public void seeResults(){
        Intent intent = new Intent(MainActivity2.this, ResultActivity.class);
        String score = scoreNumber.getText().toString();
        intent.putExtra("score",score);
        intent.putExtra("cate",category);
        intent.putExtra("name",name);
        intent.putExtra("point",pointNumberText.getText().toString());
        startActivity(intent);
    }

    // in this method, we are getting question, correct answer and incorrect answers.
    // then we are setting to buttons that variables.
    public void setValuesToButton(String question, String correctAns , String ia1, String ia2, String ia3){

        // we have that buttons array. we have A,B,C,D buttons.
        allButtons = new Button[]{answerA, answerB, answerC, answerD};

        // in here we are getting random number between 0 to 3
        Random random = new Random();
        int index = random.nextInt(3 - 0);

        // in here we are setting question to question's textview.
        questionBlankText.setText(question);

        // we are getting randomly button in button array. if that button's id equals to answer A button's id,
        // we are set correct answer to answer A button. and incorrect answers to another buttons.
        // with that, we are setting correct answer different buttons for every question.
        if(allButtons[index].getId() == answerA.getId()){
            answerA.setText("A : " + correctAns);
            answerB.setText("B : " + ia1);
            answerC.setText("C : " + ia2);
            answerD.setText("D : " + ia3);
        }
        else if(allButtons[index].getId() == answerB.getId()){
            answerA.setText("A : " + ia1);
            answerB.setText("B : " + correctAns);
            answerC.setText("C : " + ia2);
            answerD.setText("D : " + ia3);
        }
        else if(allButtons[index].getId() == answerC.getId()){
            answerA.setText("A : " + ia1);
            answerB.setText("B : " + ia2);
            answerC.setText("C : " + correctAns);
            answerD.setText("D : " + ia3);
        }
        else if(allButtons[index].getId() == answerD.getId()){
            answerA.setText("A : " + ia1);
            answerB.setText("B : " + ia2);
            answerC.setText("C : " + ia3);
            answerD.setText("D : " + correctAns);
        }
    }

    // this method for checking which button clicked.
    public void buttonListeners(String correctAns){

        // if button A is clicked, we are getting answer which in A button. then check for if this answer is correct.
        answerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ans1 = answerA.getText().toString();

                // if answer is correct, increase score, change score to string and set score textview,
                // showing toast message 'Correct!', then stop count down timer,
                // close button listeners and with that user cannot click more than one for one question,
                // then we are loading next question and starting time with delay 1 second.
                // because sometimes questions loading slowly because of internet connection.
                if(ans1.equals("A : " + correctAns)){
                    score++;
                    String s = String.valueOf(score);
                    scoreNumber.setText(s);
                    Toast.makeText(MainActivity2.this, "Correct!", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    closeButtonsListeners();
                    answerA.setBackground(getDrawable(R.color.greenNew));
                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
                else{
                    // if answer is not correct show toast message 'Wrong!'
                    Toast.makeText(MainActivity2.this, "Wrong!", Toast.LENGTH_SHORT).show();

                    // set wrong answer button's color as RED
                    answerA.setBackground(getDrawable(R.color.redNew));

                    // stop time and close button listeners
                    countDownTimer.cancel();
                    closeButtonsListeners();

                    // in here we are showing correct answer to user.
                    // correct answer's button will be green.
                    if(answerB.getText().toString().equals("B : " + correctAns)){
                        answerB.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerC.getText().toString().equals("C : " + correctAns)){
                        answerC.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerD.getText().toString().equals("D : " + correctAns)){
                        answerD.setBackground(getDrawable(R.color.greenNew));
                    }


                    // then load next question with 1 second delay.
                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
            }
        });

        // same progress for answer B,C and D.
        answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ans2 = answerB.getText().toString();
                if(ans2.equals("B : " + correctAns)){
                    score++;
                    String s = String.valueOf(score);
                    scoreNumber.setText(s);
                    Toast.makeText(MainActivity2.this, "Correct!", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    closeButtonsListeners();
                    answerB.setBackground(getDrawable(R.color.greenNew));
                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
                else{
                    Toast.makeText(MainActivity2.this, "Wrong!", Toast.LENGTH_SHORT).show();
                    answerB.setBackground(getDrawable(R.color.redNew));
                    countDownTimer.cancel();
                    closeButtonsListeners();

                    if(answerA.getText().toString().equals("A : " + correctAns)){
                        answerA.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerC.getText().toString().equals("C : " + correctAns)){
                        answerC.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerD.getText().toString().equals("D : " + correctAns)){
                        answerD.setBackground(getDrawable(R.color.greenNew));
                    }

                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
            }
        });

        answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ans3 = answerC.getText().toString();
                if(ans3.equals("C : " + correctAns)){
                    score++;
                    String s = String.valueOf(score);
                    scoreNumber.setText(s);
                    Toast.makeText(MainActivity2.this, "Correct!", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    closeButtonsListeners();
                    answerC.setBackground(getDrawable(R.color.greenNew));
                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
                else{
                    Toast.makeText(MainActivity2.this, "Wrong!", Toast.LENGTH_SHORT).show();
                    answerC.setBackground(getDrawable(R.color.redNew));
                    countDownTimer.cancel();
                    closeButtonsListeners();

                    if(answerB.getText().toString().equals("B : " + correctAns)){
                        answerB.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerA.getText().toString().equals("A : " + correctAns)){
                        answerA.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerD.getText().toString().equals("D : " + correctAns)){
                        answerD.setBackground(getDrawable(R.color.greenNew));
                    }

                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
            }
        });

        answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ans4 = answerD.getText().toString();
                if(ans4.equals("D : " + correctAns)){
                    score++;
                    String s = String.valueOf(score);
                    scoreNumber.setText(s);
                    Toast.makeText(MainActivity2.this, "Correct!", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    closeButtonsListeners();
                    answerD.setBackground(getDrawable(R.color.greenNew));
                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
                else{
                    Toast.makeText(MainActivity2.this, "Wrong!", Toast.LENGTH_SHORT).show();
                    answerD.setBackground(getDrawable(R.color.redNew));
                    countDownTimer.cancel();
                    closeButtonsListeners();

                    if(answerB.getText().toString().equals("B : " + correctAns)){
                        answerB.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerC.getText().toString().equals("C : " + correctAns)){
                        answerC.setBackground(getDrawable(R.color.greenNew));
                    }
                    else if(answerA.getText().toString().equals("A : " + correctAns)){
                        answerA.setBackground(getDrawable(R.color.greenNew));
                    }

                    loadData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timerStart();
                        }
                    },1000);
                }
            }
        });
    }

    // in this method, we are closing buttons listeners and users cannot click the buttons.
    public void closeButtonsListeners(){
        answerA.setOnClickListener(null);
        answerB.setOnClickListener(null);
        answerC.setOnClickListener(null);
        answerD.setOnClickListener(null);
    }

    // in this method we are setting same color for every button.
    public void cleanButtonColors(){
        answerA.setBackground(this.getDrawable(R.drawable.custom_button2));
        answerB.setBackground(this.getDrawable(R.drawable.custom_button2));
        answerC.setBackground(this.getDrawable(R.drawable.custom_button2));
        answerD.setBackground(this.getDrawable(R.drawable.custom_button2));
    }

    // this method starting time.
    public void timerStart(){

        // we are using count down timer. user will have 30 second, and this time will decrease with 1 second.
        countDownTimer = new CountDownTimer(30000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

                // for every 1 second, we are showing new time in textview.
                TextView time = findViewById(R.id.timeCounter);
                time.setText( "" + millisUntilFinished / 1000);

                // if our time less then 10 second, time will be RED for warning user.
                if(millisUntilFinished < 10000){
                    time.setTextColor(Color.RED);
                }
                else{
                    time.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onFinish() {

                // if our time is over, we are stopping count down timer and load the next question,
                // and starting time again for new question.
                countDownTimer.cancel();
                loadData();
                timerStart();
            }
        }.start();
    }
}