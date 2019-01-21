package com.example.julio.photogo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Camera_Activity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final String ROOT_FILE = "PhotogoIMG/",ROUTE_IMAGE = ROOT_FILE + "images";
    private final int COD_SELECT= 10,COD_PHOTO = 20;
    private  final  String[] selecteZone = new String[1],selectedCategory = new String[1];
    private String encodedImage=null,path,getaddress,user;
    private double latitude=0, longitude=0;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private float zoomLevel=  16.0f;
    private Button butonimage, myplacebtn,searchbtn,addlocation,back;
    private ImageView image;
    private  TextView setCoordinates,title;
    private EditText searchtxt,locationname,coment;
    private  Spinner zonespinner,categoryspinner;
    private String[] zones,category;
    private List<String> zonesList,categoryList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ConnectionHTTP connectionHTTP;
    MapView mapView;
    GoogleMap map;
    private  ProgressBar progressBar;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        Bundle sendingObject = getIntent().getExtras();
        title=(TextView)findViewById(R.id.textView2);
        //TOLLBAR MENU
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        title.setText("הוספת מיקום");
        back = (Button) findViewById(R.id.backto);
        connectionHTTP=new ConnectionHTTP(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        }
        );
     /*   if (sendingObject != null) {
           user= (String) sendingObject.getSerializable("usertext");

        }
        });*/





        // Get reference of widgets from XML layout
        progressBar = (ProgressBar)findViewById(R.id.progressBarCam);
// Get reference of widgets from XML layout
        zonespinner = (Spinner) findViewById(R.id.spinnerzone);
        categoryspinner = (Spinner) findViewById(R.id.spinnercategory);
        image = (ImageView) findViewById(R.id.imagemId);
        butonimage = (Button) findViewById(R.id.btnCargarImg);//Button --take a Picture
        myplacebtn = (Button) findViewById(R.id.myplacebtn);//Button --get my location
        searchbtn=(Button)findViewById(R.id.searchbutton);//Button to search location
        addlocation=(Button)findViewById(R.id.addlocalbtn); //Button to add location
        progressBar = (ProgressBar)findViewById(R.id.progressBarCam);
        progressBar.setVisibility(View.GONE);
        setCoordinates = (TextView) findViewById(R.id.coordinator);
        locationname=(EditText)findViewById(R.id.locationnametxt);
        coment=(EditText)findViewById(R.id.comentText);
        searchtxt=(EditText)findViewById(R.id.searchText);

        //set a search editext to Keyboard Search
        searchtxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);
                onSearch();
                return false;
            }
        });

        final ScrollView ScrollView = (ScrollView) findViewById(R.id.scrollview);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setBackgroundColor(Color.TRANSPARENT);

        //set a button search
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);
            }
        });

        //set a Button to Locate the device
        myplacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePermissionsLocation()) {//Validate Permission
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MINIMUM_TIME_BETWEEN_UPDATES,
                            MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                            new MyLocationListener()
                    );

                    showCurrentLocation();//Show the Location
                }
            }
        });


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get a MapView
        mapView.getMapAsync(new OnMapReadyCallback() {


            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                // Setting a click event handler for the map
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {

                        //Make the Address

                        getAddress(latLng);
                    }
                });
                map.getUiSettings().setMyLocationButtonEnabled(false);

                LatLng jerusalem = new LatLng(32.1105435, 34.8683683);
                CameraUpdate miLocation = CameraUpdateFactory.newLatLngZoom(jerusalem, 8);
                map.moveCamera(CameraUpdateFactory.newLatLng(jerusalem));
                googleMap.animateCamera(miLocation);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(jerusalem);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(true);

                googleMap.getUiSettings().setZoomControlsEnabled(true);

            }
        });


        //Static MapView into the ScrollView
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        ScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        ScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        ScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });


        // Initializing a String Array zones
        zones = new String[]{
                getString(R.string.select_zone),
                getString(R.string.Jerusalem),
                getString(R.string.north),
                getString(R.string.Haifa),
                getString(R.string.center),
                getString(R.string.Tel_aviv),
                getString(R.string.south)
        };


        // Initializing a String Array categories
        category = new String[]{
                getString(R.string.select_category),
                getString(R.string.Natural),
                getString(R.string.Portrait),
                getString(R.string.Macro),
                getString(R.string.Urban),
                getString(R.string.Beach),
                getString(R.string.Stars)
        };
        zonesList = new ArrayList<>(Arrays.asList(zones));
        categoryList = new ArrayList<>(Arrays.asList(category));
        selecteZone[0]=null;
        selectedCategory[0]=null;
        spinnerhint(zonesList, zonespinner,selecteZone);
        spinnerhint(categoryList, categoryspinner,selectedCategory);




        //Make an action to the Button
        butonimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoadImage();
            }
        });

        if (validatePermissions()) {
            butonimage.setEnabled(true);
        } else {
            butonimage.setEnabled(false);
        }

        addlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addLocal();


            }
        });

    }

    @SuppressLint("ResourceType")
    private void addLocal()  {
        String namelocal=locationname.getText().toString();
        String coment=this.coment.getText().toString();




        SharedPreferences sh = getApplicationContext().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        String user= sh.getString("name","default");
        if(checbeforeadd(namelocal)){
            connectionHTTP.upLoadToWebService(progressBar,user,namelocal,selecteZone[0],selectedCategory[0],latitude,longitude,coment,encodedImage,getaddress);
        }
        else
            Toast.makeText(getApplicationContext(),"צריך למלא את כל התווים", Toast.LENGTH_LONG).show();

    }


    private boolean checbeforeadd(String namelocal){

        if(longitude==0 && latitude==0){
            setCoordinates.setError(getString(R.string.nothing_select));
            return false;

        }else if(namelocal.equals(""))
        {
            locationname.setError("נא לרשום שם של המיקום");
            return false;
        }else if(selecteZone[0]==null){
            Toast.makeText(getApplicationContext(),"לא נבחר איזור",Toast.LENGTH_LONG).show();
            return false;
        }else if(selectedCategory[0]==null){
            Toast.makeText(getApplicationContext(),"לא נבחר קטגוריה",Toast.LENGTH_LONG).show();
            return false;
        }else if(encodedImage==null){
            Toast.makeText(getApplicationContext(),"לא נבחר תמונה למיקום",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {

        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {


            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            handler.sendMessage(msg);

        }
        public void onProviderEnabled(String s) {


        }


        private final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.arg1 == 1){
                    if (!isFinishing()) { // Without this in certain cases application will show ANR
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage(getString(R.string.gps_disable)).setCancelable(false).setPositiveButton(getString(R.string.enable_GPS), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsOptionsIntent);
                            }
                        });
                        builder.setNegativeButton(getString(R.string.do_nothing), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }

            }
        };


    }


    private void onSearch(){
        String location=searchtxt.getText().toString();
        List<android.location.Address> addressList=null;
        LatLng latLng=null;
        if(location!=null|| location.equals(""))
        {
            Geocoder geocoder=new Geocoder(this);
            try {

                addressList=geocoder.getFromLocationName(location,1);
                android.location.Address address=addressList.get(0);
                latLng=new LatLng(address.getLatitude(),address.getLongitude());
                if(getAddress(latLng)!=true){
                    Toast.makeText(getApplicationContext(),"המיקום לא נמצא",Toast.LENGTH_LONG).show();
                }
                CameraUpdate miLocation = CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel);

                map.animateCamera(miLocation);


            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"המיקום לא נמצא",Toast.LENGTH_LONG).show();
            }

            searchtxt.getText().clear();

        }

    }

    protected void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {

            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

            //Make Address
            getAddress(latLng);

            CameraUpdate miLocation = CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel);

            map.animateCamera(miLocation);

        }


    }

    private boolean getAddress(LatLng latLng){
        Geocoder geocoder;
        boolean flag=true;
        List<android.location.Address> direccion = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_log_map));
        try {
            direccion = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // 1 representa la cantidad de resultados a obtener

        } catch (IOException e) {

            markerOptions.title(latLng.latitude + " , " + latLng.longitude);
        }
        catch (Exception e){
            //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
            markerOptions.title(latLng.latitude + " , " + latLng.longitude);
        }
        if (direccion!= null && !direccion.isEmpty()){

            String city=direccion.get(0).getLocality();
            if(city==null)
                getaddress = direccion.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            else
                getaddress = direccion.get(0).getAddressLine(0)+"\n"+city;
            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(getaddress);
        }
        else {

            flag=false;
            markerOptions.title(latLng.latitude + " , " + latLng.longitude);
            getaddress="לא נמצאה כתובת של המיקום";
        }
        // Creating a marker

        // Setting the position for the marker
        markerOptions.position(latLng);

        setCoordinates.setText(latLng.latitude + " , " + latLng.longitude);
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        // Clears the previously touched position
        map.clear();

        // Animating to the touched position
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        map.addMarker(markerOptions);

        return flag;
    }


    private void spinnerhint(List<String> arrayList, Spinner spinner, final String[] selectedItemText) {

        spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, arrayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selectedItemText[0] = parent.getItemAtPosition(position).toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void TakePhoto() {
        File fileImagen = new File(Environment.getExternalStorageDirectory(), ROUTE_IMAGE);
        boolean isCreada = fileImagen.exists();
        String nombreImagen = "";
        if (isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada == true) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }


        path = Environment.getExternalStorageDirectory() +
                File.separator + ROUTE_IMAGE + File.separator + nombreImagen;

        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getApplicationContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent, COD_PHOTO);
        ////
    }

    //Get the paht of the file
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                case COD_SELECT:

                    final Uri miPath = data.getData();
                    this.path = getRealPathFromURI( miPath);

                    image.setImageURI(miPath);

                    CreateBitmap(this.path);

                    break;

                case COD_PHOTO:

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
        Bitmap image = ((BitmapDrawable) this.image.getDrawable()).getBitmap();

        int width=image.getWidth();
        int height=image.getHeight();
        if(width==height){
            width=1080;height=1080;
        }if(width>height){
            width=1080;height=720;
        }else{
            width=1080;height=1350;
        }
       Bitmap imageScaled=resizeImage(image,width,height);
        encodedImage=convertImgString(imageScaled);
    }


    public static Bitmap resizeImage(Bitmap BitmapOrg, int w, int h) {

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;


        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;


        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);


        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        return resizedBitmap;

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
        this.image.setImageBitmap(rotateBipmap);

    }


    private boolean validatePermissionsLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
        if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
            DialogLoaderlocal();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }

        return false;
    }

    private void DialogLoaderlocal() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getApplicationContext());
        dialogo.setTitle(getString(R.string.disabled_permissions));
        dialogo.setMessage(getString(R.string.alert_permissions));

        dialogo.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION}, 10);
            }
        });
        dialogo.show();

    }

    private boolean validatePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
        if ((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
            DialogLoader();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
        }

        return false;
    }

    private void DialogLoader() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getApplicationContext());
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

        switch (requestCode){
            case 100:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    butonimage.setEnabled(true);
                } else {
                    requestManualPerrmission();
                }
                return;
            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myplacebtn.setEnabled(true);

                }else{
                    requestManualPerrmission();
                }
                return;
        }

    }

    private void requestManualPerrmission() {
        final CharSequence[] opciones = {getString(R.string.yes), getString(R.string.no)};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getApplicationContext());
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
                    Toast.makeText(getApplicationContext(), getString(R.string.permissionotaccepted), Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertdialog = alertOpciones.create();
        alertdialog.show();
    }


    private void LoadImage() {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.load_image), getString(R.string.cancel)};
        final AlertDialog.Builder alertOptions = new AlertDialog.Builder(this);
        alertOptions.setTitle(getString(R.string.select_options));
        alertOptions.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals(getString(R.string.take_photo))) {
                    TakePhoto();
                } else {
                    if (options[i].equals(getString(R.string.load_image))) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent, getString(R.string.selectapp)), COD_SELECT);
                    } else {
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        AlertDialog alertdialog = alertOptions.create();
        alertdialog.show();

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




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
        if (id==R.id.perfil)
        {

               finish();
            }
        if(id==R.id.gohome){
            Intent intent = new Intent(Camera_Activity.this ,Principal_Activity.class);
            startActivity(intent);

        }
        if(id==R.id.exit)
        {
            exit();
            return true;
        }
        return super.onOptionsItemSelected(options_menu);
    }
}
