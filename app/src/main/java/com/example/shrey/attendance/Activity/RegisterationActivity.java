package com.example.shrey.attendance.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shrey on 6/8/2018.
 */
public class RegisterationActivity extends AppCompatActivity implements View.OnClickListener{
    private final int PICK_PICTURE_REQUEST=71;
    EditText name,age,designation,mobilenumber,email,password,confirmpassword;
    StorageReference storageReference;
    Uri filepath;
    Button submit;
    CircleImageView profilePicture;
    DetailPojo detailPojo=new DetailPojo();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        profilePicture=findViewById(R.id.profile_picture);
        storageReference=FirebaseStorage.getInstance().getReference();
        name=findViewById(R.id.name);
        password=findViewById(R.id.password);
        confirmpassword=findViewById(R.id.confirmpassword);
        designation=findViewById(R.id.designation);
        mobilenumber=findViewById(R.id.mobile_number);
        email=findViewById(R.id.email_id);
        age=findViewById(R.id.age);
        submit=findViewById(R.id.button_submit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().contains("stickyindia"))
                {
                    if (password.getText().toString().matches(confirmpassword.getText().toString()) && password.getText().toString().length()>0 && confirmpassword.getText().toString().length()>0)
                    {
                        if(email.getText().toString().matches("") || password.getText().toString().matches("") || confirmpassword.getText().toString().matches("") || mobilenumber.getText().toString().matches("") ||
                                designation.getText().toString().matches("") || age.getText().toString().matches(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter all details",Toast.LENGTH_SHORT).show();
                        }else
                            {
                        setProfileValues();
                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();}
                    }else{
                        name.setText(name.getText().toString());
                        designation.setText(name.getText().toString());
                        email.setText(email.getText().toString());
                        age.setText(age.getText().toString());
                        password.setText("");
                        confirmpassword.setText("");
                        mobilenumber.setText(mobilenumber.getText().toString());
                        Toast.makeText(RegisterationActivity.this, "Password mismatch", Toast.LENGTH_SHORT).show();
                    }
                }else
                    {
                    Toast.makeText(getApplicationContext(),"Unauthorised",Toast.LENGTH_SHORT).show();
                }

            }
        });
       profilePicture.setOnClickListener(this);
       profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));

    }
    public void setProfileValues(){
    detailPojo.setName(name.getText().toString());
    detailPojo.setAge(age.getText().toString());
    detailPojo.setDesignation(designation.getText().toString());
    detailPojo.setMobilenumber(mobilenumber.getText().toString());
    detailPojo.setEmail(email.getText().toString());
    detailPojo.setPassword(password.getText().toString());
    detailPojo.setConfirmpasssword(confirmpassword.getText().toString());
    onSubmitProfile();
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
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(RegisterationActivity.this.getContentResolver(),filepath);
                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        public void uploadImage(){
            String fullname=name.getText().toString();
        if(filepath!=null){
           StorageReference reference=storageReference.child("Employees").child("ProfilePicture").child(fullname)
                    .child("images/"+fullname);
                    reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterationActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterationActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
          }
        }
    public void onSubmitProfile()
    {
        uploadImage();
        String fullname=name.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullname).setValue(detailPojo);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullname).child("leaves").setValue("2");
        Intent startpageintent=new Intent(RegisterationActivity.this,StartPageActivity.class);
        startActivity(startpageintent);
    }
    @Override
    public void onClick(View v) {
        chooseImage();
    }
}


