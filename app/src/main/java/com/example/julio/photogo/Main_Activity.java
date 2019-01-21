package com.example.julio.photogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class Main_Activity extends AppCompatActivity {
    private String tempuser,tempass;

    ProgressBar progressBar;
    ConnectionHTTP login;
    ArrayList<ImageData> imagedata=new ArrayList<> ();
    private static final String PREFS_TAG = "SharedPrefs";
    private static final String IMAGE_TAG = "MyImageData";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        session();

    }

    //Check if the user Was Loggin
    private void session(){
        SharedPreferences sh = this.getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        tempuser = sh.getString("name","default");
        tempass=sh.getString("pass","default");
        login = new ConnectionHTTP(this);
        login.startSession(progressBar,tempuser,tempass,"Main");

    }


}
