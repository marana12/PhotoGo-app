package com.example.julio.photogo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register_Activity extends SingIn_Activity {
    private EditText user,pass,confirm_p,email,name,lname;
    private String User,Pass,confirm,f_name,l_name,Email,txt ;

    private ConnectionHTTP register;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_activity);

        user=(EditText)findViewById(R.id.Usertext);
        name=(EditText)findViewById(R.id.nametext);
        lname=(EditText)findViewById(R.id.lname_text);
        email=(EditText)findViewById(R.id.emailtext);
        pass=(EditText)findViewById(R.id.passwordtext);
        confirm_p=(EditText)findViewById(R.id.confirm_pass);
        register=new ConnectionHTTP(this);
        txt="";
    }

    private int nameCheck(int cnt) {
        int i;

        for (i = 0; i < f_name.trim().length(); i++) {
            if (!Character.isLetter(f_name.charAt(i)))
                cnt++;
        }

        for (i = 0; i < l_name.trim().length(); i++) {
            if (!Character.isLetter(l_name.charAt(i)))
                cnt++;
            }
       return cnt;
    }


    private boolean checkPass(){
        if(!Pass.equals(confirm)){
            //If the password ar the same with the confirm password
            pass.setError(getString(R.string.not_confirm));


            return false;
        }
        else {//Check the length of Password
            if (Pass.length()<8 || Pass.length()>16)
            {
                pass.setError(getString(R.string.pass_length));


                return false;
            }
            else{//Witch String have the password
                String letters = "abcdefghijklmnoupqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String num = "0123456789";
                String espChar ="!@#$%^&*()_-{}:?¿></~|[]";
                int cnt=0;
                int cntLeter=0;
                int cntNum=0;
                int cntChar=0;

                for(int i=0;i<Pass.length();i++){

                   if(cntLeter==0){
                   for(int k=0;k<letters.length();k++) {
                       if (Pass.charAt(i) == letters.charAt(k)) {
                           cntLeter++;
                           cnt++;
                           continue;
                                }
                            }
                        }

                       if(cntChar==0) {
                           for (int k = 0; k < espChar.length(); k++) {
                               if (Pass.charAt(i) == espChar.charAt(k)) {
                                   cntChar++;
                                   cnt++;
                                   continue;
                               }
                           }
                       }
                           if (cntNum == 0) {
                               for (int k = 0; k < num.length(); k++) {
                                   if (Pass.charAt(i) == num.charAt(k)) {
                                       cntNum++;
                                       cnt++;
                                       continue;
                                   }
                               }
                           }
                }
                if(cnt<2)
                {
                    pass.setError(getString(R.string.pass_char));

                    return false;
                }
            }
        }
        return true;
    }


//Check the  all te parameters to LogIn
   private boolean check(){
            int cnt=0;
        try{
            //If the Labels are Empty
        if ((!User.equals(txt)) || (!Pass.equals(txt)) || (!confirm.equals(txt)) || (!f_name.equals(txt)) || (!l_name.equals(txt))|| (!Email.equals(txt)))
        {

                //Check the Char of First Name and Last Name
                if(nameCheck(cnt)!=0){
                     name.setError(getString(R.string.aChar));
                    name.setText(txt);
                    lname.setText(txt);
                    confirm_p.setText(txt);
                    pass.setText(txt);
                    return false;

                }


            //Check the password
            if(!checkPass()){
                confirm_p.setText(txt);
                pass.setText(txt);
                return false;
            }
                    else

                        return true;
        }
        else{

            Toast.makeText(getApplicationContext(), getString(R.string.empty_label), Toast.LENGTH_LONG).show();

        return false;
        }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
        return false;
    }


    //Create a new acount
    public void create(View v) {

        User = user.getText().toString();
        Pass = pass.getText().toString();
        confirm = confirm_p.getText().toString();
        f_name = name.getText().toString();
        l_name = lname.getText().toString();
        Email = email.getText().toString();

                    if(check()) {
                        register.register(user,name,lname,email,pass,confirm_p);

                    }

    }
}
