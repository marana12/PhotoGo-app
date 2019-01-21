package com.example.julio.photogo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class MapsFragment extends Fragment {
   private MapView mapView;
   private GoogleMap map;
   private ConnectionHTTP connectionHTTP;
   private String ip;
   private ArrayList<ImageData> imagedata=new ArrayList<ImageData> ();
  // RequestQueue request;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.maps_fragment, container, false);
       // refillImgData();
        // request= Volley.newRequestQueue(getActivity());

         connectionHTTP=new ConnectionHTTP();
         ip=connectionHTTP.getIp();
        mapView = (MapView) view.findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
       getFrormWeb();
        return view;
    }

    private void getFrormWeb(){

        final String file = "/getAllData/getAllimg.php";
        final String url=ip+file;

       JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        getResponse(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        getErrorResponse(error);
                    }
                }
        );
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(getRequest);
    }

    private void getErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(),"יש בעיה עם התקשורת", Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
    }

    private void getResponse(JSONArray response) {
        Log.d("Response", response.toString());

        // display response
        try {
            for (int i=0;i<response.length();i++){
                ImageData imgdata=new ImageData();
                imgdata.setImgId(response.getJSONObject(i).getInt("Img_ID"));
                imgdata.setUser(response.getJSONObject(i).getString("U_Name"));
                imgdata.setLocalname(response.getJSONObject(i).getString("localname"));
                imgdata.setZone(response.getJSONObject(i).getString("zone"));
                imgdata.setCategory(response.getJSONObject(i).getString("category"));
                imgdata.setComent(response.getJSONObject(i).getString("comment"));
                imgdata.setLatitude(response.getJSONObject(i).getDouble("latitude"));
                imgdata.setLongitude(response.getJSONObject(i).getDouble("longitude"));
                imgdata.setAddress(response.getJSONObject(i).getString("Address"));
                imgdata.setRouteImg(response.getJSONObject(i).getString("Img_root"));
                imgdata.setDate(response.getJSONObject(i).getString("thisDate"));


                imagedata.add(imgdata);
            }}
        catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "אין תקשורת עם השרת" +
                    " "+response, Toast.LENGTH_LONG).show();

        }
        getMap();
    }
    private void getMap(){

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setMyLocationButtonEnabled(false);
                LatLng jerusalem = new LatLng(32.1105435, 34.8683683);
                CameraUpdate miLocation = CameraUpdateFactory.newLatLngZoom(jerusalem, 8);
                map.moveCamera(CameraUpdateFactory.newLatLng(jerusalem));
                googleMap.animateCamera(miLocation);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(jerusalem);
                for(int i=0;i<imagedata.size();i++){

                    LatLng latLng=new LatLng(imagedata.get(i).getLatitude(),imagedata.get(i).getLongitude());
                    //imagedata.get(i).setAddress(getAddress(latLng));
                    MarkerOptions markerOptions1 = new MarkerOptions();
                    markerOptions1.title(imagedata.get(i).getLocalname()).snippet("למידע נוסף תלחץ כאן...");
                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_log_map));
                    markerOptions1.position(latLng);
                    // map.clear();
                    Marker marker=map.addMarker(markerOptions1);
                    imagedata.get(i).setMarkerId(marker.getId());
                }

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        //searching marker id in locationDetailses and getting all the information of a particular marker
                        for (int i = 0; i < imagedata.size(); i++) {
                            if (marker.getId().equals(imagedata.get(i).getMarkerId())) {
                                Intent intent = new Intent(getActivity(),Image_Details_Activity.class);
                                Bundle bundle =new Bundle();
                                bundle.putSerializable("imagedata",imagedata.get(i));
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                });
            }});


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


}