package com.example.julio.photogo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class NoConnect_Activity extends AppCompatActivity {
    User user;
    String getuser;
    ProgressBar progresbar;
    Button tryagain;
    ConnectionHTTP connectionHTTP;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection);
        user=new User();
        connectionHTTP=new ConnectionHTTP(this);
        tryagain=(Button) findViewById(R.id.trybtn);
        progresbar=(ProgressBar)findViewById(R.id.progressBartrytoconnect);
        progresbar.setVisibility(View.GONE);
        Bundle sendingObject = getIntent().getExtras();
        if (sendingObject != null) {
            getuser = (String) sendingObject.getSerializable("user");

        }
        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progresbar.setVisibility(View.VISIBLE);
                connectionHTTP.getUser(getuser,progresbar);

            }
        });


    }
}
