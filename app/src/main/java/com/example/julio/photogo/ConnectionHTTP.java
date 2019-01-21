package com.example.julio.photogo;

/*
* THIS CLASS MAKE A CONNECTION WITH THE SERVER
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class ConnectionHTTP extends AppCompatActivity
{
    private String ip;

    private StringRequest strq;
    private Context c;

    public ConnectionHTTP(Context c){
        this.c=c;
        ip="http://ip";


    }
   public ConnectionHTTP(){
       ip="http://ip";//
   }
    public String getIp(){
       return this.ip;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void nexActivty(){
        Intent i =new Intent(this.c,Principal_Activity.class);
        c.startActivity(i);
        ((Activity)c).finish();
    }
//This Method is for Login
    public void startSession(final ProgressBar progressBar, String u_name, String pass, final String root){

   final String user=u_name.trim();
   final String password=pass.trim();
    final String file="/login/loginuser.php";
    final String url=ip+file;

    strq = new StringRequest(
            Request.Method.POST,
            url,

            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE: ",""+response);
                    if(response.contains("success")){
                        if (root == "Main") {
                            progressBar.setVisibility(View.GONE);
                            //Continue to the next Activity
                           nexActivty();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            //Save the Password and the User
                        SharedPreferences sharedPreferences = c.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("name",user);
                        editor.putString("pass",password);
                        editor.apply();

                        //Continue to the next Activity
                        nexActivty();
                        }
                    }
                    else
                    {
                        if(root == "Main"){
                            progressBar.setVisibility(View.GONE);
                            SharedPreferences sharedPreferences = c.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("name","");
                            editor.putString("pass","");
                            editor.apply();
                        Intent i =new Intent(c,SingIn_Activity.class);
                        c.startActivity(i);
                            ((Activity)c).finish();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(c,c.getString(R.string.not_login) , Toast.LENGTH_LONG).show();
                        }
                    }
                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(c,"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();
                    Log.i("ERROR",""+error);
                    if(root=="Main"){

                    Intent i =new Intent(c,SingIn_Activity.class);
                    c.startActivity(i);
                    ((Activity)c).finish();
                    }

                }
            }
    ){
        @Override
        protected  Map<String,String>getParams() throws AuthFailureError{
            Map<String,String>params=new HashMap<>();
            params.put("loginuser","true");
            params.put("username",user);
            params.put("password",password);
            return  params;
        }
    };

   // Volley.newRequestQueue(c).add(strq);
        VolleySingleton.getIntanciaVolley(c).addToRequestQueue(strq);

    }

    //This Method is for Register
    public void register(final EditText uName, EditText fName, EditText lName, final EditText mail, final EditText pass,final  EditText confirmpass){
        final String username=uName.getText().toString().trim();
        final String f_name=fName.getText().toString().trim();
        final String l_name=lName.getText().toString().trim();
        final String email=mail.getText().toString().trim();
        final String password=pass.getText().toString().trim();
        final  String file="/login/registeruser.php";
         String url=ip+file;

       strq=new StringRequest
               (Request.Method.POST, url, new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       Log.i("RESPONSE: ",""+response);
                       if(response.contains("success")){
                           Intent i =new Intent(c,SingIn_Activity.class);
                           c.startActivity(i);
                           ((Activity)c).finish();
                       }
                       else if(response.contains("username")){

                           pass.setText("");
                           confirmpass.setText("");
                           uName.setError(c.getString(R.string.user_exist));

                       }else if(response.contains("email")){

                           pass.setText("");
                           confirmpass.setText("");
                           mail.setError(c.getString(R.string.user_exist));
                       }
                       else{

                       }

                   }
               }
                       , new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Toast.makeText(c,"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();
                       Log.i("ERROR",""+error);

                   }
               }){
           @Override
            protected  Map<String,String>getParams() throws AuthFailureError {
               Map<String, String> params = new HashMap<>();
                    params.put("registeruser","true");
                    params.put("username",username);
                    params.put("f_name",f_name);
                    params.put("l_name",l_name);
                    params.put("password",password);
                    params.put("email",email);
                 return params;
           }
       };

        VolleySingleton.getIntanciaVolley(c).addToRequestQueue(strq);

    }

    public void upLoadToWebService(final ProgressBar progressBar, final String username, String Lname, String Zone, String Category,
                                   final Double Latitude, final  Double Longitude, String Comment, String Image,String Address) {
        progressBar.setVisibility(View.VISIBLE);

        final Double latitude=Latitude;
        final Double longitude=Longitude;
        final String u_name =username.trim();
        final String localname=Lname.trim();
        final String zone=Zone.trim();
        final String category=Category.trim();
        final String comment=Comment.trim();
        final String image=Image.trim();
        final String address=Address.trim();

        final  String file="/uploadimg/uploadimage.php";
        String url=ip+file;

        strq=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE: ",""+response);
                progressBar.setVisibility(View.GONE);

                if (response.trim().equalsIgnoreCase("register")){
                    Toast.makeText(c,"המיקום התווסף בהצלחה", Toast.LENGTH_LONG).show();
                    Intent i =new Intent(c,Principal_Activity.class);
                    ((Activity)c).startActivity(i);
                    ((Activity)c).finish();
                }else{
                    Toast.makeText(c,"המיקום לא התווסף", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Log.i("ERROR",""+error);
                Toast.makeText(c,"יש בעיה עם התקשורת אנא נסה שנית", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("u_name",u_name);
                params.put("localname",localname);
                params.put("zone",zone);
                params.put("category",category);
                params.put("latitude", String.valueOf(latitude).trim());
                params.put("longitude", String.valueOf(longitude).trim());
                params.put("comment",comment);
                params.put("image",image);
                params.put("address",address);
                return params;
            }
        };
        strq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley((Activity)c).addToRequestQueue(strq);
    }




    public void getUser(final String user, final ProgressBar pb){
        final User getuser=new User();
        String file="/users/getDataUsers.php?u_name='"+user+"'";
        String url=ip+file;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        pb.setVisibility(View.GONE);
                        // display response
                        try {

                            getuser.setUserId(response.getJSONObject(0).getInt("U_ID"));
                            getuser.setUser(response.getJSONObject(0).getString("U_Name"));
                            getuser.setName(response.getJSONObject(0).getString("F_Name"));
                            getuser.setLname(response.getJSONObject(0).getString("L_Name"));
                            getuser.setPhoto(response.getJSONObject(0).getString("Photo"));

                            if(!getuser.equals(null)){
                                pb.setVisibility(View.GONE);
                                Intent intent = new Intent(c ,Profile_Activity.class);
                                Bundle bundle =new Bundle();
                                bundle.putSerializable("usertext",getuser);
                                intent.putExtras(bundle);
                                c.startActivity(intent);
                                if(((Activity)c) instanceof NoConnect_Activity){
                                    ((Activity)c).finish();
                                }
                            }
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
                        pb.setVisibility(View.GONE);
                        Log.d("Error.Response", error.toString());
                        pb.setVisibility(View.GONE);
                        Intent intent = new Intent(c,NoConnect_Activity.class);
                        Bundle bundle =new Bundle();
                        bundle.putSerializable("user",user);
                        intent.putExtras(bundle);
                       c.startActivity(intent);
                        if(((Activity)c) instanceof NoConnect_Activity){
                            ((Activity)c).finish();
                        }

                       // Toast.makeText(getApplicationContext(),"יש בעיה עם התקשורת", Toast.LENGTH_LONG).show();
                        System.out.println();

                    }
                }
        );
        VolleySingleton.getIntanciaVolley(c).addToRequestQueue(getRequest);

    }

}
