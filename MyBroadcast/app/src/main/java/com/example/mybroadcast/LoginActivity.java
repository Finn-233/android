package com.example.mybroadcast;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LoginActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        int[] bg = new int[]{R.drawable.w11, R.drawable.w12, R.drawable.w13};
        int rand = (int) (Math.random()*3);
        constraintLayout = findViewById(R.id.login);
        constraintLayout.setBackgroundResource(bg[rand]);


//        NavController navController = Navigation.findNavController(this,R.id.f);
//        NavigationUI.setupActionBarWithNavController(this,navController);
    }
}
