package com.example.shrey.attendance.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Shrey on 6/16/2018.
 */

public class ForgotpasswordActivity extends AppCompatActivity {
    EditText username,newpassword;
    DetailPojo obj;
    Button changePassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgotpassword);
        username=findViewById(R.id.userName);
        newpassword=findViewById(R.id.new_password);
        changePassword=findViewById(R.id.button_changepassword);
       }

    public void changePassword(View view)
    {
        final String userName=username.getText().toString();
        final String password=newpassword.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Registration Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren())
                try{
                   obj=child.getValue(DetailPojo.class);
                   if(obj.getUsername().toString().matches(userName))
                   {
                           FirebaseDatabase.getInstance().getReference().child("Employees").child("Registration Details").child(userName).child("password").setValue(password);
                           Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                           Intent loginpageintent=new Intent(ForgotpasswordActivity.this,StartPageActivity.class);
                           loginpageintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(loginpageintent);
                       }else if(!obj.getUsername().toString().matches(userName)){
                       Toast.makeText(getApplicationContext(),"No such username exists",Toast.LENGTH_SHORT).show();
                   }
                }catch (Exception ex)
                {
                    Log.v("TAG" ,"EXCEPTION : "+ex.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
