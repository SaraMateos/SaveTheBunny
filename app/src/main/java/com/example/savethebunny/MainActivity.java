package com.example.savethebunny;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

// Tutorial del juego  --> https://www.youtube.com/watch?v=gfX8UHTpq3o&ab_channel=SandipBhattacharya

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void startGame(View view) {
       GameView gameView = new GameView(this);
       setContentView(gameView);
    }
}