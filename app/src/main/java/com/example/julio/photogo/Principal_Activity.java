package com.example.julio.photogo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class Principal_Activity extends AppCompatActivity  {
     Button home,exit;
    Toolbar toolbar;
    ProgressBar progressBar;
    String meuser;
    ConnectionHTTP connectionHTTP=new ConnectionHTTP(this);
    private BottomNavigationView bottomNavigationView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.  onCreate(savedInstanceState);
        setContentView(R.layout.pricipal_activity);


        toolbar=(Toolbar)findViewById(R.id.toolbar) ;
        SharedPreferences sh = this.getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        meuser = sh.getString("name", "default");
        progressBar=(ProgressBar)findViewById(R.id.progressfragment);
       progressBar.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);



        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
//            progressBar.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.inicioItem:

                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.camaraItem:

                            selectedFragment = new CameraFragment();
                            break;
                        case R.id.mapItem:

                            selectedFragment = new MapsFragment();

                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    public void exit(){
        SharedPreferences sharedPreferences = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("name","");
        editor.putString("pass","");
        editor.apply();
        Intent i=new Intent(this,SingIn_Activity.class);
        startActivity(i);
        finish();
    }
    //CREATE THE MENU
  @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem options_menu){
        int id=options_menu.getItemId();
            if(id==R.id.exit)
            {

                exit();
               return true;
           }
           if (id==R.id.perfil)
           {
               progressBar.setVisibility(View.VISIBLE);
               connectionHTTP.getUser(meuser,progressBar);

           }
           if(id==R.id.gohome){
               Intent intent = new Intent(this ,Principal_Activity.class);
               startActivity(intent);
               finish();
           }
            return super.onOptionsItemSelected(options_menu);    }

}
