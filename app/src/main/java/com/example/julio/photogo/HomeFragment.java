package com.example.julio.photogo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    Context context;
    private ArrayList<ImageData> imagedata=new ArrayList<>();
    private ConnectionHTTP connectionHTTP;
    private String ip;
    Image_Home_Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.home_fragment, container, false);
        adapter=null;
        connectionHTTP=new ConnectionHTTP();
        ip=connectionHTTP.getIp();
        recyclerView = (RecyclerView) v.findViewById(R.id.RecyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getFrormWeb();
        context=getContext();
        return v;
    }

    private   void getFrormWeb(){

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
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(getRequest);
    }

    private void getErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת", Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
    }

    private void getResponse(JSONArray response) {
        Log.d("Response", response.toString());
        int i=response.length()-1;
        // display response
        try {
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

         adapter=new Image_Home_Adapter(getActivity(),imagedata);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(),Image_Details_Activity.class);
                    Bundle bundle =new Bundle();

                    bundle.putSerializable("imagedata",imagedata.get(recyclerView.getChildAdapterPosition(v)));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "אין תקשורת עם השרת" +
                    " "+response, Toast.LENGTH_LONG).show();

        }

    }

}