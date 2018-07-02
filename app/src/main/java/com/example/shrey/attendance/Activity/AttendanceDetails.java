package com.example.shrey.attendance.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Shrey on 6/18/2018.
 */

public class AttendanceDetails extends Fragment {
    ArrayList<DetailPojo> attendanceList = new ArrayList<>();
    DetailPojo obj;
    ListView listView;
    TextView leaves;
    String emailId,totalleaves,fullName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_attendancedetails, container, false);
        listView = rootview.findViewById(R.id.listView_attendancedetails);
        leaves=rootview.findViewById(R.id.tvleaves);
        emailId = StartPageActivity.getDefaults("emailid", getContext());
        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        getfullName();

    }

    public void getfullName() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren())
                            try {
                                obj = child.getValue(DetailPojo.class);
                                if (emailId.matches(obj.getEmail())) {
                                    fullName= obj.getName();
                                    fetchattendancedetails();
                                    fetchLeaves();
                                }
                            } catch (Exception ex) {
                                Log.v("TAG", "EXCEPTION : " + ex.getMessage());
                            }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
}
    public void fetchLeaves() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        obj = child.getValue(DetailPojo.class);
                        if (emailId.matches(obj.getEmail())) {
                            totalleaves = obj.getLeaves();
                            leaves.setText(totalleaves);
                            Log.v("TAG", "TOTAL LEAVES : " + totalleaves);
                        }
                    }catch (Exception ex){
                        ex.getMessage();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void fetchattendancedetails() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").limitToLast(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot template : child.getChildren()) {
                        Log.v("TAG","CHILD KEY : "+child.getKey());
                        try {
                            if(fullName.matches(child.getKey())) {
                                obj = template.getValue(DetailPojo.class);
                                attendanceList.add(obj);
                                AttendanceDetailsAdapter adapter = new AttendanceDetailsAdapter(getContext(), R.layout.adapter_attendancedetails, attendanceList);
                                listView.setAdapter(adapter);
                            }
                        } catch (Exception ex) {
                            Log.v("tag", "EXCEPTION " + ex.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}