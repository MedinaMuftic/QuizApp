package com.example.quizzz.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.quizzz.R;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // we are declare xml items here. for choosing category, we are using scroll choise. also we declare array list for categories.
    ImageView imageView;
    Button button;
    EditText editTextTextPersonName;
    TextView categoryTitle;
    String chosedCategory = "";
    List<String> categories = new ArrayList<>();
    ScrollChoice scrollChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // here we are initializing items and variables with find view by id.
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button3);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        categoryTitle = findViewById(R.id.categoryTitle);
        scrollChoice = findViewById(R.id.scrollchoise);

        // if user already finished one game and click 'new game' button which in result screen, we are getting user's name with intent
        // then we are adding user's name in edittext. so this is for if user finished one game and clicks the 'new game' button.
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        editTextTextPersonName.setText(name);

        // for scroll choise item, we need to add list for showing categories. we are adding categories list here.
        // when user open app, scroll will be first index. we can change that in add items method.
        loadCategories();
        scrollChoice.addItems(categories,1);

        // in this method we are getting which category user selected. we store that category in chosedCategory.
        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                chosedCategory = name;
            }
        });

        // when user click button, it will start 'start' method.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(v);
            }
        });
    }

    // in this method, we are taking user name in edittext and we are getting category.
    // we created intent for go to second screen with user's name and chosen category.
    public void start(View view){
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        String name = editTextTextPersonName.getText().toString();
        intent.putExtra("name",name);
        intent.putExtra("category",chosedCategory);
        startActivity(intent);
    }

    // in this method we are adding categories in list.
    public void loadCategories(){
        categories.add("General");
        categories.add("History");
        categories.add("Food and Drink");
        categories.add("Geography");
        categories.add("Art and Literature");
        categories.add("Movies");
        categories.add("Music");
        categories.add("Science");
    }
}