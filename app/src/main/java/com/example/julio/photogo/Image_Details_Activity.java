package com.example.julio.photogo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Image_Details_Activity extends AppCompatActivity {
    Toolbar toolbar;
    Button back, botonmaps, botonwaze,trytoconnect;
    ImageView imageView;
    private String image, meuser,route,ip;
    TextView nameoflocation, User, coordinates, address, category, zone,details,date;
    ImageData imagedata = null;
    User user=new User();
    ProgressBar progressBarphoto,progressBartoUser;
    ImageButton delete;
    private ConnectionHTTP connectionHTTP;
    RequestQueue request;
    StringRequest stringRequest;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details);
        connectionHTTP=new ConnectionHTTP(this);
        ip=connectionHTTP.getIp();
        request=Volley.newRequestQueue(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        imageView = (ImageView) findViewById(R.id.setimage);


        nameoflocation = (TextView) findViewById(R.id.textView2);
        date=(TextView)findViewById(R.id.date);
        back = (Button) findViewById(R.id.backto);
        trytoconnect=(Button)findViewById(R.id.trytoconnectbtn);
        trytoconnect.setVisibility(View.GONE);
        progressBarphoto = (ProgressBar) findViewById(R.id.trytoconnect);
        progressBartoUser= (ProgressBar) findViewById(R.id.progresbartoUser);
        progressBartoUser.setVisibility(View.GONE);
        User = (TextView) findViewById(R.id.setby);
        coordinates = (TextView) findViewById(R.id.setcoortxt);
        address = (TextView) findViewById(R.id.setaddresstxt);
        category = (TextView) findViewById(R.id.setcategory);
        zone = (TextView) findViewById(R.id.setzone);
        details=(TextView)findViewById(R.id.coment);
        botonmaps = (Button) findViewById(R.id.send_map);
        botonwaze = (Button) findViewById(R.id.send_waze);
        delete = (ImageButton) findViewById(R.id.deleteButton);
        date=(TextView)findViewById(R.id.date);
        //delete.setEnabled(false);

        details.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        SharedPreferences sh = this.getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        meuser = sh.getString("name", "default");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        setSupportActionBar(toolbar);
        Bundle sendingObject = getIntent().getExtras();


        if (sendingObject != null) {
            imagedata = (ImageData) sendingObject.getSerializable("imagedata");
            //Her i inicialite the data from Imagedata to this Activity
            route=imagedata.getRouteImg();
            coordinates.setText(imagedata.getLatitude() + "," + imagedata.getLongitude());
            address.setText(imagedata.getAddress());
            category.setText("   "+imagedata.getCategory());
            nameoflocation.setText(imagedata.getLocalname());
            zone.setText("   "+imagedata.getZone());
            User.setText(imagedata.getUser());
            date.setText(imagedata.getDate());
           // ImgID = imagedata.getImgId();
            getPhotoWS();

        }
        if(imagedata.getComent()!="")
        {
            details.setText(imagedata.getComent());
            details.setVisibility(View.VISIBLE);
        }
        if ( meuser.equals(imagedata.getUser())) {
            delete.setVisibility(View.VISIBLE);
        }

        trytoconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trytoconnect.setVisibility(View.GONE);
                getPhotoWS();
            }
        });

    //Delete the Picture
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogToDelete();

            }
        });
        //Select App to Navigate
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoprofile(User.getText().toString());
            }
        });
        //Botton to App Waze
        botonwaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "waze://?q=" + imagedata.getLatitude() + "," + imagedata.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.waze"));
                    startActivity(intent);
                }
            }
        });
        //Botto to App Maps
        botonmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri intentUri = Uri.parse("geo:" + imagedata.getLatitude() + "," + imagedata.getLongitude() + "?z=16&q=" + imagedata.getLatitude() + "," + imagedata.getLongitude() + "(" + imagedata.getLocalname() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
                    startActivity(intent);
                }
            }
        });
    }

    public void   gotoprofile( String user) {
        progressBartoUser.setVisibility(View.VISIBLE);
        connectionHTTP.getUser(user,progressBartoUser);

            }

//This Method is make the action deleted by the user was shot the photo
    private void deleteimage() {
        String file="/delete&update/deletepict.php";
        String url=ip+file+"?root="+route;

        progressBarphoto.setVisibility(View.VISIBLE);
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBarphoto.setVisibility(View.GONE);

                if (response.trim().equalsIgnoreCase("deleted")){

                    Toast.makeText(getApplicationContext(),"המיקום נמחק בהצלחה",Toast.LENGTH_SHORT).show();

                    Intent i =new Intent(Image_Details_Activity.this,Principal_Activity.class);
                    startActivity(i);
                    finish();

                }else{
                    if (response.trim().equalsIgnoreCase("noExist")){
                        Toast.makeText(getApplicationContext(),"לא נמצא המיקום בבסיס הנתונים ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPONSE: ",""+response);
                    }else{
                        Toast.makeText(getApplicationContext(),"לא הצליח להתבצע המחיקה ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPONSE: : ",""+response);
                    }

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarphoto.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();
            }
        });

        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void DialogToDelete() {
        final CharSequence[] opciones = {getString(R.string.yes), getString(R.string.no)};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
        alertOpciones.setTitle(getString(R.string.removepicture));
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(getString(R.string.yes))) {

                    deleteimage();

                }  else {
                        dialogInterface.dismiss();
                    }
                }

        });
        AlertDialog alertdialog = alertOpciones.create();
        alertdialog.show();

    }


//Get the Picture from DataBase
    private void getPhotoWS() {
        progressBarphoto.setVisibility(View.VISIBLE);


        String url=ip+route;
        url=url.replace(" ","%20");

        com.android.volley.toolbox.ImageRequest imageRequest=new com.android.volley.toolbox.ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                progressBarphoto.setVisibility(View.GONE);
                trytoconnect.setVisibility(View.GONE);imageView.setImageBitmap(response);



            }

        },0,0,ImageView.ScaleType.CENTER,null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBarphoto.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();
                progressBarphoto.setVisibility(View.GONE);
                trytoconnect.setVisibility(View.VISIBLE);


            }
        });
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(imageRequest);


    }


    public void exit() {
        SharedPreferences sharedPreferences = getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "");
        editor.putString("pass", "");
        editor.apply();
        Intent i = new Intent(this, SingIn_Activity.class);
        startActivity(i);
        finish();
    }


    //CREATE THE MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem options_menu) {
        int id = options_menu.getItemId();
        if (id == R.id.exit) {
            exit();
            return true;
        }if(id==R.id.perfil){

            gotoprofile(meuser);
            return true;
        }
        if(id==R.id.gohome){
            Intent intent = new Intent(this ,Principal_Activity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(options_menu);
    }


}
