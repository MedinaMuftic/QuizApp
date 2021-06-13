package com.example.quizzz.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quizzz.R;

public class ResultActivity extends AppCompatActivity {

    // we are declaring xml items and variables here
    TextView resultTitle;
    TextView categoryTitleText;
    TextView cateText;
    TextView scoreResult;
    TextView scoreResultNumber;
    Button newGame;
    TextView pointTitle;
    TextView pointNumber;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // we are initialize variables here with find view by id
        resultTitle = findViewById(R.id.resultTitle);
        categoryTitleText = findViewById(R.id.categoryTitleText);
        cateText = findViewById(R.id.cateText);
        scoreResult = findViewById(R.id.scoreResult);
        scoreResultNumber = findViewById(R.id.scoreResultNumber);
        newGame = findViewById(R.id.playAgainButton);
        pointTitle = findViewById(R.id.pointResult);
        pointNumber = findViewById(R.id.pointResultNumber);

        // we are getting category, score, name and point with intent for showing user.
        Intent intent = getIntent();
        String cate = intent.getStringExtra("cate");
        String score = intent.getStringExtra("score");
        name = intent.getStringExtra("name");
        String point = intent.getStringExtra("point");

        // we are settin result text with user's name. like 'User Result'
        resultTitle.setText(name + " Result");

        // setting score, category and point to the textviews
        scoreResultNumber.setText(score);
        cateText.setText(cate);
        pointNumber.setText(point);

        // if user clicks new game button, we are taking user's name and go to the first screen,
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }
}