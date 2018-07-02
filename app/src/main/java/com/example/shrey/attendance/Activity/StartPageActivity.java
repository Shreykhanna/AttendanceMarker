package com.example.shrey.attendance.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Shrey on 6/13/2018.
 */
public class StartPageActivity extends AppCompatActivity {
    Button registerButton,loginButton,forgotpasswordButton;
    CheckBox rememberMe;
    EditText emailid,password;
    DetailPojo obj;
    Toolbar toolbar;
    public SharedPreferences loginDetails;
    public SharedPreferences.Editor editor;
    Boolean loggedIn=false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_startpage);
        registerButton=findViewById(R.id.button_signup);
        loginButton=findViewById(R.id.button_login);
        forgotpasswordButton=findViewById(R.id.button_forgotpassword);
        emailid=findViewById(R.id.emailId);
        password=findViewById(R.id.password);
        toolbar=findViewById(R.id.toolbar);
        rememberMe=findViewById(R.id.remember_me);
        loginDetails= PreferenceManager.getDefaultSharedPreferences(StartPageActivity.this);
        editor=loginDetails.edit();
        setSupportActionBar(toolbar);

     }
    public void startRegisterActivity(View view)
    {
        Intent registerIntent=new Intent(this,RegisterationActivity.class);
        startActivity(registerIntent);
    }
    public void startLogin(View view) {
        loginDetails=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                    try {
                        obj = child.getValue(DetailPojo.class);
                        Log.v("TAG", "EMAIL : " + obj.getEmail());
                        Log.v("TAG", "PASSWORD : " + obj.getPassword());
                        if (obj.getEmail().matches(emailid.getText().toString()) &&
                                obj.getPassword().matches(password.getText().toString())) {
                            if(rememberMe.isChecked()) {
                                setSharedPreferenceDefault("emailid", emailid.getText().toString(), getBaseContext());
                            }
                            Intent homepageactivityintent = new Intent(StartPageActivity.this, HomePageActivity.class);
                            startActivity(homepageactivityintent);
                            loggedIn=true;
                        }else if(!obj.getEmail().equals(emailid.getText().toString()) || !obj.getPassword().equals(password.getText().toString())){
                            Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        System.out.println("Exception" + ex.getMessage());
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
public void forgotpassword(View view)
    {
        Intent forgetpasswordActivity=new Intent(this,ForgotpasswordActivity.class);
        startActivity(forgetpasswordActivity);
    }
public static void setSharedPreferenceDefault(String key,String value,Context context)
{
    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor=sharedPreferences.edit();
    editor.putBoolean("saveLogin",true);
    editor.putString(key,value);
    editor.commit();
}
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}

