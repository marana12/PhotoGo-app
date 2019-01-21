package com.example.julio.photogo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.String.valueOf;

public class Profile_Activity extends AppCompatActivity {
    private String path;
    private final String CARPETA_RAIZ="PhotogoIMG/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"images";
    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;
    private ArrayList<ImageData> imagedata=new ArrayList<ImageData> ();
    ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView User,FullName,coutphotos;
    Button addlocal;
    String getuser,meuser,Name,LastName,IP,encodedImage=null;
    ImageView imgperfil;
    Button back;
    ConnectionHTTP connectionHTTP;
   // ConnectionDB con;
    User newUser;
    //the recyclerview
    RecyclerView recyclerView;
    Context context=this;
    private  int ImgID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        connectionHTTP=new ConnectionHTTP(this);
        IP=connectionHTTP.getIp();
        back = (Button) findViewById(R.id.backto);
        toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        progressBar=(ProgressBar)findViewById(R.id.progressBar) ;
        progressBar.setVisibility(View.GONE);
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        coutphotos=(TextView)findViewById(R.id.setPhotos);
        User=(TextView)findViewById(R.id.textView2);
        FullName=(TextView)findViewById(R.id.fullname_txt);
        addlocal=(Button) findViewById(R.id.addlocation);
        imgperfil=(ImageView)findViewById(R.id.imagePerfil);
        setSupportActionBar(toolbar);

       // con=new ConnectionDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });



        SharedPreferences sh = this.getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        meuser = sh.getString("name", "default");

        //Get the UserName from another Activity
        Bundle sendingObject = getIntent().getExtras();
        if (sendingObject != null) {

            newUser =  (User)  sendingObject.getSerializable("usertext");
            //Here i inicialite the data from Imagedata to this Activity
            getuser=newUser.getUser();
            User.setText(getuser);
            Name=newUser.getName();
            LastName=newUser.getLname();
            FullName.setText(Name+" "+LastName);//SEt the Name of the user in GUI
            //If the User Have a Perfil Pic The app download the pic

            if(!newUser.getPhoto().equals("null")){
                getPhotoPerfil(newUser.getPhoto());
            }
            else{
                imgperfil.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

            }

            getAllImagefromUser();
        }

        //Create a new Class to Upload Photo of User
        //And to get Data User Data



        if(getuser.equals(meuser)){
            imgperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadImage();

                }});

        }  else{
            addlocal.setVisibility(View.GONE);

        }

        //Test the permision to take photo
       if(validatePermissions()){
           imgperfil.setEnabled(true);
       }else{
           imgperfil.setEnabled(false);
        }

        //Add a new location
        addlocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile_Activity.this,Camera_Activity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("usertext",meuser);
                intent.putExtras(bundle);
                startActivity(intent);

            }


        });


    }


    private void getPhotoPerfil(String file) {

        String url=IP+file;
        url=url.replace(" ","%20");

        com.android.volley.toolbox.ImageRequest imageRequest=new com.android.volley.toolbox.ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //progressBar.setVisibility(View.GONE);
                imgperfil.setImageBitmap(response);
            }

        },0,0,ImageView.ScaleType.CENTER,null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();

            }
        });
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(imageRequest);

    }
    private void uploadPicProfile(){
        final  String file="/users/uploadImgPerfil.php";
        String url=IP+file;
        final int userId=newUser.getUserId();
        final String rootfile=encodedImage;

        StringRequest strq=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE: ",""+response);

                if (response.trim().equalsIgnoreCase("Update")){
                    Toast.makeText(getApplicationContext(),"התמונה שונתה בהצלחה", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(),"התמונה לא שונתה", Toast.LENGTH_LONG).show();
                    imgperfil.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Log.i("ERROR",""+error);
                imgperfil.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
                Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("u_id", valueOf(userId));
                params.put("photo",rootfile);

                return params;
            }
        };
        strq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(strq);

    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1){
                if (!isFinishing()) { // Without this in certain cases application will show ANR
                    AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
                    builder.setMessage(getString(R.string.changeprofilepic)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int id) {

                            uploadPicProfile();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int id) {
                            if(!newUser.getPhoto().equals("null")){
                                getPhotoPerfil(newUser.getPhoto());}
                            else{


                                imgperfil.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));}
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        }
    };


    private void  getAllImagefromUser(){
    String route="/users/getImgsFroUsers.php?u_name='"+getuser+"'";
    String url=IP+route;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        try {

                        int i=response.length()-1;
                            while (i>=0){
                                ImageData imgdata=new ImageData();
                                imgdata.setImgId(response.getJSONObject(i).getInt("Img_ID"));
                                imgdata.setUser(response.getJSONObject(i).getString("U_Name"));
                                imgdata.setLocalname(response.getJSONObject(i).getString("localname"));
                                imgdata.setZone(response.getJSONObject(i).getString("zone"));
                                imgdata.setCategory(response.getJSONObject(i).getString("category"));
                                imgdata.setComent(response.getJSONObject(i).getString("comment"));
                                imgdata.setLatitude(response.getJSONObject(i).getDouble("latitude"));
                                imgdata.setLongitude(response.getJSONObject(i).getDouble("longitude"));
                                imgdata.setRouteImg(response.getJSONObject(i).getString("Img_root"));
                                imgdata.setDate(response.getJSONObject(i).getString("thisDate"));
                                imgdata.setAddress(response.getJSONObject(i).getString("Address"));
                                imagedata.add(imgdata);
                                i--;
                            }
                            coutphotos.setText(valueOf(response.length()));
                            Image_Adpter adapter = new Image_Adpter(context, imagedata);
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(Profile_Activity.this,Image_Details_Activity.class);
                                    Bundle bundle =new Bundle();
                                    bundle.putSerializable("imagedata",imagedata.get(recyclerView.getChildAdapterPosition(v)));
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                            });
                            //setting adapter to recyclerview
                            recyclerView.setAdapter(adapter);
                } catch(JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "אין תקשורת עם השרת" +
                                    " "+response, Toast.LENGTH_LONG).show();
                        }
                    }
                        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת", Toast.LENGTH_LONG).show();
                        System.out.println();
                        Log.d("ERROR: ", error.toString());

                    }
                }
        );
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
    }


    //------SET A NEW PICTURE--------
    public boolean validatePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            DialogLoader();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }
    private void DialogLoader() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Profile_Activity.this);
        dialogo.setTitle(getString(R.string.disabled_permissions));
        dialogo.setMessage(getString(R.string.alert_permissions));

        dialogo.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
            }
        });
        dialogo.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                imgperfil.setEnabled(true);
            }else{
                requestManualPerrmission();
            }
        }

    }

    private void requestManualPerrmission() {
        final CharSequence[] opciones = {getString(R.string.yes), getString(R.string.no)};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(Profile_Activity.this);
        alertOpciones.setTitle(getString(R.string.requestmanualperrmission));
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(getString(R.string.yes))) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText( Profile_Activity.this.getApplicationContext(), getString(R.string.permissionotaccepted), Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertdialog = alertOpciones.create();
        alertdialog.show();
    }

    private void LoadImage() {
        final CharSequence[] opciones={getString(R.string.take_photo) ,getString(R.string.load_image) ,getString(R.string.cancel) };
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(Profile_Activity.this);
        alertOpciones.setTitle(getString(R.string.select_options));
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(getString(R.string.take_photo))){
                    TakePhoto();
                }else{
                    if (opciones[i].equals(getString(R.string.load_image))){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,getString(R.string.selectapp)),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();

    }
    private void TakePhoto(){
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(Profile_Activity.this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);

        ////
    }


 //Get the paht of the file
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getRealPathFromURI(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        if (cursor != null) {
            cursor.close();
        }
        return filePath;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case COD_SELECCIONA:

                    final Uri miPath = data.getData();
                    this.path = getRealPathFromURI(getApplicationContext(), miPath);

                    imgperfil.setImageURI(miPath);

                    CreateBitmap(this.path);

                    break;

                case COD_FOTO:

                    MediaScannerConnection.scanFile(this, new String[]{this.path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                    Log.i("Root of Storage", "Path: " + path);
                                }
                            });

                    CreateBitmap(this.path);

                    break;
            }
        }
    }
    private void CreateBitmap(String path){
        Bitmap originBitmap = BitmapFactory.decodeFile(path);

        if (originBitmap != null) {

            rotateImage(originBitmap,path);
        }
        Bitmap image = ((BitmapDrawable) this.imgperfil.getDrawable()).getBitmap();
        Bitmap imageScaled=Camera_Activity.resizeImage(image,250,250);
        encodedImage=convertImgString(imageScaled);
        Message msg = handler.obtainMessage();
        msg.arg1 = 1;
        handler.sendMessage(msg);


    }

    private String convertImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imageByte=array.toByteArray();
        String imageString= Base64.encodeToString(imageByte,Base64.DEFAULT);
        return imageString;
    }

    private void rotateImage(Bitmap bitmap, String miPath ){
        ExifInterface exifInterface=null;
        try {
            exifInterface=new ExifInterface(miPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix=new Matrix();
        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:}

        Bitmap rotateBipmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        this.imgperfil.setImageBitmap(rotateBipmap);

    }
//------------------------------------------------------

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
    public boolean onOptionsItemSelected(MenuItem options_menu){
        int id=options_menu.getItemId();
        if(id==R.id.exit)
        {

                //con.CloseConnection();

            exit();
            return true;
        }
        if (id==R.id.perfil)
        {

            if(!getuser.equals(meuser)){


                progressBar.setVisibility(View.VISIBLE);
                connectionHTTP.getUser(getuser,progressBar);

            }/*else
            {
                Intent intent = new Intent(this ,Profile_Activity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("usertext",meuser);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }*/
            if(id==R.id.gohome){
                Intent intent = new Intent(Profile_Activity.this ,Principal_Activity.class);
                startActivity(intent);

            }

        }
        return super.onOptionsItemSelected(options_menu);    }


}
