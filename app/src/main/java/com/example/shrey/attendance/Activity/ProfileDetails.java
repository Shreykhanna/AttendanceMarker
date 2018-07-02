package com.example.shrey.attendance.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Shrey on 6/21/2018.
 */

public class ProfileDetails extends Fragment implements View.OnClickListener{
    DetailPojo obj;
    InputMethodManager inputmethodmanager;
    String emailId;
    String fullName;
    TextView firstname;
    EditText designation,age,phonenumber,emailid;
    Button editEmail,editDesignation,editAge,editmobilenumber,updatedetails,changeprofilepic;
    int flag=0;
    Uri filepath;
    private final int PICK_PICTURE_REQUEST=71;
    ImageView profilePicture;
    StorageReference storageReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View rootview=inflater.inflate(R.layout.layout_profile,container,false);
          profilePicture=rootview.findViewById(R.id.profile_picture);
          firstname=rootview.findViewById(R.id.tv_firstname);
          designation=rootview.findViewById(R.id.tv_designation);
          age=rootview.findViewById(R.id.tv_age);
          phonenumber=rootview.findViewById(R.id.tv_mobilenumber);
          emailid=rootview.findViewById(R.id.tv_emailid);
          editEmail=rootview.findViewById(R.id.button_editemailid);
          editDesignation=rootview.findViewById(R.id.button_editdesignation);
          editAge=rootview.findViewById(R.id.button_editage);
          editmobilenumber=rootview.findViewById(R.id.button_editmobilenumber);
          updatedetails=rootview.findViewById(R.id.button_updatedetails);
          changeprofilepic=rootview.findViewById(R.id.button_changepicture);
          storageReference=FirebaseStorage.getInstance().getReference();
          getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
          emailId=StartPageActivity.getDefaults("emailid",getContext());
          changeprofilepic.setOnClickListener(this);
          editEmail.setOnClickListener(this);
          editDesignation.setOnClickListener(this);
          editmobilenumber.setOnClickListener(this);
          editAge.setOnClickListener(this);
          updatedetails.setOnClickListener(this);
         getfullName();
          return rootview;
     }

    @Override
    public void onStart() {
        super.onStart();
      //  getfullName();

    }

    public void getfullName()
    {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        obj = child.getValue(DetailPojo.class);
                        if (emailId.matches(obj.getEmail().toString())) {
                            fullName = obj.getName();
                            fetchData();
                        }
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            }
                    @Override
                    public void onCancelled (DatabaseError databaseError){
                }
        });
    }
public void fetchData(){
      downloadImage();
      FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child: dataSnapshot.getChildren()){
                            try {
                                   obj = child.getValue(DetailPojo.class);
                                if (emailId.matches(obj.getEmail().toString())) {
                                        firstname.setText(obj.getName());
                                        designation.setText(obj.getDesignation().toString());
                                        emailid.setText(obj.getEmail().toString());
                                        phonenumber.setText(obj.getMobilenumber().toString());
                                        age.setText(obj.getAge().toString());
                                    }
                                }catch(Exception ex){
                                    Log.v("TAG", "EXCEPTION : " + ex.getMessage());
                                }
                        }
                 }
                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                    }
                });
      }
    public void downloadImage(){
        final long ONE_MEGABYTE = 1024 * 1024;
        try {
            StorageReference reference = storageReference.child("Employees").child("ProfilePicture").child(fullName).child("images/"+fullName);
            reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    //Log.v("TAG","SUCCESS");
                    Glide.with(getActivity()).load(bytes).apply(RequestOptions.circleCropTransform()).into(profilePicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                   // Log.v("TAG","ERROR WHILE DOWNLOADING PIC");
                    Glide.with(getActivity()).load(R.drawable.placeholder).apply(RequestOptions.circleCropTransform()).into(profilePicture);
                    // Handle any errors
                }
            });
        }catch(Exception ex)
        {
            Log.v("TAG","EXCEPION : "+ex.getMessage());
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_changepicture:
                chooseImage();
                updatedetails.setVisibility(View.VISIBLE);
            case R.id.button_editemailid:
                emailid.setEnabled(true);
                emailid.requestFocus();
                emailid.setFocusableInTouchMode(true);
                inputmethodmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmethodmanager.showSoftInput(emailid, InputMethodManager.SHOW_FORCED);
                updatedetails.setCursorVisible(true);
                updatedetails.setVisibility(View.VISIBLE);
                break;
            case R.id.button_editdesignation:
                designation.setEnabled(true);
                designation.requestFocus();
                designation.setFocusableInTouchMode(true);
                inputmethodmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmethodmanager.showSoftInput(designation, InputMethodManager.SHOW_FORCED);
                updatedetails.setCursorVisible(true);
                updatedetails.setVisibility(View.VISIBLE);
                break;
            case R.id.button_editmobilenumber:
                phonenumber.setEnabled(true);
                phonenumber.requestFocus();
                phonenumber.setFocusableInTouchMode(true);
                inputmethodmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmethodmanager.showSoftInput(phonenumber, InputMethodManager.SHOW_FORCED);
                updatedetails.setCursorVisible(true);
                updatedetails.setVisibility(View.VISIBLE);
                break;
            case R.id.button_editage:
                age.setEnabled(true);
                age.requestFocus();
                age.setFocusableInTouchMode(true);
                inputmethodmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmethodmanager.showSoftInput(age, InputMethodManager.SHOW_FORCED);
                updatedetails.setVisibility(View.VISIBLE);
                break;
            case R.id.button_updatedetails:
                updateDetails();
         }
    }
    public void chooseImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_PICTURE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_PICTURE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),filepath);
                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void uploadImage(){
        if(getActivity()!=null) {
            if (filepath != null) {
                StorageReference reference = storageReference.child("Employees").child("ProfilePicture").child(fullName)
                        .child("images/" + fullName);
                reference.putFile(filepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Profile Picture Changed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else{
            return;
        }
}
    public void updateDetails()
    {
        String email_id=emailid.getText().toString();
        String mobile_number=phonenumber.getText().toString();
        String Designation=designation.getText().toString();
        String Age=age.getText().toString();
        flag=1;
        uploadImage();
        try {
            FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullName)
                    .child("email").setValue(email_id);
            FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullName)
                    .child("mobilenumber").setValue(mobile_number);
            FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullName)
                    .child("designation").setValue(Designation);
            FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullName)
                    .child("age").setValue(Age);
            Toast.makeText(getActivity()," Details Updated",Toast.LENGTH_SHORT).show();
            if(flag==1){
                emailid.setEnabled(false);
                age.setEnabled(false);
                phonenumber.setEnabled(false);
                designation.setEnabled(false);
            }
            updatedetails.setVisibility(View.INVISIBLE);
        }catch (Exception ex)
        {
            Log.v("tag","ERROR"+ex.getMessage());
        }
    }
 }

