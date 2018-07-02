package com.example.shrey.attendance.Database;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**
 * Created by Shrey on 5/27/2018.
 */
public class DatabaseConn {
    NetworkInfo newtworkInfo;
    DatabaseReference reference;
    DetailPojo detailPojo=new DetailPojo();
    public void savevalues(String userName, String password,String mobilenumber) {
        try {
             detailPojo.setUsername(userName);
             detailPojo.setPassword(password);
             detailPojo.setMobilenumber(mobilenumber);
        }catch(Exception ex)
        {
            System.out.println("EXCEPTION INSIDE SAVE VALUES METHOD : "+ex.getMessage());
        }
        setvalues();
    }
    public void setvalues() {
        try {
            /*String firstname = detailPojo.getFirstname();
            String lastname = detailPojo.getLastname();*/
            String username=detailPojo.getUsername();
            reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Employees").child("Registration Details").child(username).setValue(detailPojo);
        } catch (Exception ex) {
            System.out.println("EXCEPTION INSIDE SET VALUES METHOD : " + ex.getMessage());
        }
    }
        void getvalues(){
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Employees").orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot template : dataSnapshot.getChildren()) {
                        for (DataSnapshot child : template.getChildren()) {

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception ex){
            System.out.println("EXCEPTION INSIDE GETVALUES METHOD : "+ex.getMessage());
        }
    }

}
