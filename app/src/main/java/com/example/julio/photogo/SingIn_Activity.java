package com.example.julio.photogo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SingIn_Activity extends AppCompatActivity  {
   private EditText user,pass;
   // Button login,reg;
    private String User,Pass;
    ProgressBar circular;
    private ConnectionHTTP tologin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        user=(EditText)findViewById(R.id.usertext);
        pass=(EditText) findViewById(R.id.passtext);
        SharedPreferences sh = this.getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        user.setText(sh.getString("name",""));
        pass.setText(sh.getString("pass",""));
        circular=(ProgressBar)findViewById(R.id.progressBar3);
        circular.setVisibility(View.GONE);
    }
    private void tologin()  {

        circular.setVisibility(View.VISIBLE);
        tologin = new ConnectionHTTP(this);
               tologin.startSession(circular,User,Pass,"Singin");
    }
    //Botton Login
    public void log(View v) {

       User = user.getText().toString();
       Pass = pass.getText().toString();

        if (User.equals("") || Pass.equals(""))
            Toast.makeText(getApplicationContext(),getString(R.string.empty_label) , Toast.LENGTH_LONG).show();
        else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            tologin();
        }
    }

    public void register(View v){
        Intent i =new Intent(this,Register_Activity.class);
        startActivity(i);
    }


}
