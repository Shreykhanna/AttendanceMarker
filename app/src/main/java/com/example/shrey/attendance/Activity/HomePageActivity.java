package com.example.shrey.attendance.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Shrey on 6/14/2018.
 */
public class HomePageActivity extends AppCompatActivity {
    DrawerLayout drawerlayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    StorageReference storageReference;
    ImageView userPicture;
    Button logout;
    View header;
    DetailPojo obj;
    String emailId,fullName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        navigationView = findViewById(R.id.view_navigation);
        header=navigationView.getHeaderView(0);
        userPicture=header.findViewById(R.id.imageview_header);
        emailId=StartPageActivity.getDefaults("emailid",getApplicationContext());
        logout=findViewById(R.id.button_logout);
        storageReference= FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerlayout = findViewById(R.id.layout_drawer);
        actionBarDrawerToggle=settupdrawerToggle();
        drawerlayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        AttendanceMarkerActivity attendanceMarkerActivity=AttendanceMarkerActivity.newInstance();
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.layout_startpage,attendanceMarkerActivity).commit();
        checkuser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartPageActivity.setSharedPreferenceDefault("emailid",null,getApplicationContext());
                Intent startpage=new Intent(HomePageActivity.this,StartPageActivity.class);
                startpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startpage);
            }
        });
        setupDrawerContent(navigationView);
    }
    public void checkuser() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            try {
                                obj = child.getValue(DetailPojo.class);
                                if (emailId.matches(obj.getEmail())) {
                                    fullName=obj.getName();
                                    downloadImage(fullName);
                                }else{
                                    downloadImage();
                                }
                            }catch (Exception ex)
                            {
                                Log.v("TAG","EXCEPTION INSIDE HOMEPAGEACTIVITY :"+ex.getMessage());

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public ActionBarDrawerToggle settupdrawerToggle()
    {
        return new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }
    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                checkuser();
                selectDrawerItem(item);
                return false;
            }
        });
    }
    public void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentclass;
        Log.v("TAG","itemid"+item.getItemId());
           switch (item.getItemId()) {
                 case R.id.fragment_attendance:
                   fragmentclass = AttendanceMarkerActivity.class;
                   break;
               case R.id.fragment_attendancedetails:
                   fragmentclass=AttendanceDetails.class;
                   break;
               case R.id.fragment_profiledetails:
                   fragmentclass=ProfileDetails.class;
                   break;
               default:
                   fragmentclass = AttendanceMarkerActivity.class;
           }
        try {
            fragment = (Fragment) fragmentclass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layout_startpage, fragment).commit();
        item.setChecked(true);
        setTitle(item.getTitle());
        drawerlayout.closeDrawers();
    }
   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
           return true;
       }
       return super.onOptionsItemSelected(item);
   }
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    public  void downloadImage()
    {
        Log.v("TAG","INSIDE DEFAULT PIC IMAGE FUNCTION");
        Glide.with(HomePageActivity.this).load(R.drawable.placeholder).apply(RequestOptions.circleCropTransform()).into(userPicture);
    }
    public void downloadImage(String fullName) {
        final long ONE_MEGABYTE = 1024 * 1024;
       try {
            StorageReference reference = storageReference.child("Employees").child("ProfilePicture").child(fullName).
                    child("images/"+fullName);
            reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Glide.with(HomePageActivity.this).load(bytes).apply(RequestOptions.circleCropTransform()).into(userPicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                        Glide.with(HomePageActivity.this).load(R.drawable.placeholder).apply(RequestOptions.circleCropTransform()).into(userPicture);
                        // Handle any errors
                }
            });
        }catch(Exception ex)
        {
            Log.v("TAG","eXCEPION : "+ex.getMessage());
        }
    }
}
