package com.example.julio.photogo;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB extends AppCompatActivity {


Connection connection;
    private static final String ip ="77.125.73.25";//"192.168.1.4";
    private static  final String port = "1433";
    private static String classs = "net.sourceforge.jtds.jdbc.Driver";
    private static String db = "PHOTOGO";
    private static final String user = "mizrahijulio12 ";
    private static final String password = "julio1207";
    Context context=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    //Connection method
    public Connection connectBD(Context c){
         connection=null;
         context=c;
        try {
            //Acces Permition to DB
            StrictMode.ThreadPolicy policicy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policicy);

            Class.forName(classs).newInstance();
            connection= DriverManager.getConnection("jdbc:jtds:sqlserver://"+ip+":"+port+";databaseName="+db+";user="+user+";password="+password+";instance=SQLEXPRESS;");


        }catch(SQLException sq)
        {
            Toast.makeText(context,"Ops, We have problems with the connections.\nPlease try to connect later  ",Toast.LENGTH_LONG).show();
        }
        catch (ClassNotFoundException cfe)
        {
            Toast.makeText(context,cfe.getMessage().toString(),Toast.LENGTH_SHORT).show();

        } catch (NullPointerException np)
        {
            Toast.makeText(context,np.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }

        return connection;
    }

    public Connection CloseConnection(){
    try{
        if(connection!=null){
             connection.close();}
        }
        catch(SQLException sq){
            Toast.makeText(context,sq.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
        return connection;
    }
}

