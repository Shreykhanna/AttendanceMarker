package com.example.shrey.attendance.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.Calendar;

/**
 * Created by Shrey on 6/17/2018.
 */
public class AttendanceMarkerActivity extends Fragment implements View.OnClickListener, LocationListener {
    final int ACCESS_FINE_LOCATION = 1;
    final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    String address = "119, Shahpur Jat, Siri Fort, New Delhi, Delhi 110049, India";
    Location mlastlocation;
    LocationManager locationManager;
    LocationCallback locationCallback;
    Toolbar toolbar;
    Button button;
    Boolean mRequestingLocationUpdates = false;
    TextView firstname, designation;
    AddressResultReceiver mResultReceiver;
    ImageView userPicture;
    String mAddressOutput;
    View rootview;
    DetailPojo obj;
    String systemDate;
    String fullName, emailId, date;
    static String time;
    int leaves, counter, defaulthour = 10, defaultMinutes = 30, tracker = -1;
    StorageReference storageReference;
    int Hr24, min;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.layout_atttendancemark, container, false);
        Log.v("TAG","TRACKER VALUE : "+tracker);
        userPicture = rootview.findViewById(R.id.picture_profile);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        toolbar = rootview.findViewById(R.id.toolbar);
        button = rootview.findViewById(R.id.button_attendance_mark);
        storageReference = FirebaseStorage.getInstance().getReference();
        mResultReceiver = new AddressResultReceiver(new Handler());
        firstname = rootview.findViewById(R.id.tv_name);
        designation = rootview.findViewById(R.id.tv_designation);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        emailId = StartPageActivity.getDefaults("emailid", getContext());
        button.setOnClickListener(this);
        systemDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Calendar c = Calendar.getInstance();
        Hr24 = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        date = systemDate.substring(0, 11);
        checkLeaves();
        return rootview;
    }

    public static AttendanceMarkerActivity newInstance() {
        return new AttendanceMarkerActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission();
        getfullName();
        downloadImage();
    }

    public void checkdate() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").limitToLast(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot template : child.getChildren()) {
                        try {
                            if (fullName.matches(child.getKey())) {
                                obj = template.getValue(DetailPojo.class);
                                if (date.matches(obj.getDate())) {
                                    button.setEnabled(false);
                                    button.setText("Attendance marked");
                                    fetchData();
                                } else {
                                    button.setEnabled(true);
                                    button.setText("Mark attendance");
                                    fetchData();
                                }
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

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);

            }
        } else {
            Toast.makeText(getActivity(), "Access location permission already granted", Toast.LENGTH_SHORT).show();
            currentLocationSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentLocationSettings();
                } else {
                    Toast.makeText(getActivity(), "Permisison denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    protected void currentLocationSettings() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
    public void getLastlocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        try {
                            mlastlocation = location;
                            // In some rare cases the location returned can be null
                            if (mlastlocation == null) {
                                Log.v("TAG", "LAST LOCATION " + mlastlocation.getLongitude() + "," + mlastlocation.getLatitude());
                                return;
                            }
                            if (!Geocoder.isPresent()) {
                                Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Start service and update UI to reflect new location
                            startIntentService();
                            //   updateUI();
                        } catch (Exception ex) {
                            Log.v("TAG", "EXCEPTION : " + ex.getMessage());
                        }
                    }
                });
    }
    protected void startIntentService() {
        Log.v("TAG", "INTENT STARTED");
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mlastlocation);
        getActivity().startService(intent);
    }

    @Override
    public void onClick(View v) {
        getLastlocation();
    }

    public void pushAttendance() {
        tracker = tracker + 1;
        String temp = Integer.toString(tracker);
        Log.v("TAG", "TRACKER VALUE  : " + tracker);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("date").setValue(date);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("value").setValue("Present");
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("time").setValue(Hr24+":"+min);
    }

    public void pushAttendanceAutomatic() {
        tracker = tracker + 1;
        String temp = Integer.toString(tracker);
        Log.v("TAG", "TRACKER VALUE  : " + tracker);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("date").setValue(date);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("value").setValue("Late");
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("time").setValue(Hr24 + ":" + min);
        getLeaves();
    }

    public void pushAttendanceAutomatic(int flag) {
        Log.v("TAG", "TRACKER INITIAL VALUE  : " + tracker);
        tracker = tracker + 1;
        leaves = leaves - 1;
        Log.v("TAG","TRACKER VALUE  "+tracker);
        String newleaves = Integer.toString(leaves);
        String temp = Integer.toString(tracker);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("date").setValue(date);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("value").setValue("Late");
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).child(date).child("time").setValue(Hr24 + ":" + min);
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").child(fullName).child("leaves").setValue(newleaves);
    }
    public void getLeaves() {
        counter = 0;
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Attendance").child(fullName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try{
                    obj=child.getValue(DetailPojo.class);
                     for (DataSnapshot template : child.getChildren()) {
                            String value = template.getValue().toString();
                            Log.v("TAG","INSDIDE IF CONDITION");
                            if (value.matches("Late")) {
                                counter++;
                            }
                        }
                    }catch (Exception ex){
                        Log.v("TAG","EXCEPTION : "+ex.getMessage());
                    }
                        if (counter >= 2) {
                            try {
                                if (leaves > 0) {
                                    leaves = leaves - 1;
                                    String newleaves = Integer.toString(leaves);
                                    FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details")
                                            .child(fullName).child("leaves").setValue(newleaves);
                                }
                            } catch (Exception ex) {
                                ex.getMessage();
                            }
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void checkLeaves() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    obj = child.getValue(DetailPojo.class);
                    leaves = Integer.valueOf(obj.getLeaves());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            return;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
                pushAttendanceAutomatic();
            } else {
                if ((mAddressOutput.matches(address) || mAddressOutput.contains("Siri Fort")) && (Hr24 <= defaulthour && min >= 0 && min <= 30)) {
                    pushAttendance();
                }else{
                        pushAttendanceAutomatic();
                     }
                    if(Hr24>=14){
                        pushAttendanceAutomatic(1);
                    }
                }

            }
        }


    public void getfullName() {
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        obj = child.getValue(DetailPojo.class);
                        if (emailId.matches(obj.getEmail())) {
                            fullName = obj.getName();
                            checkdate();
                            fetchData();
                        }
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void fetchData() {
        downloadImage();
        FirebaseDatabase.getInstance().getReference().child("Employees").child("Profile").child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        obj = child.getValue(DetailPojo.class);
                        if (emailId.matches(obj.getEmail())) {
                            fullName = obj.getName();
                            firstname.setText(obj.getName().toString());
                            designation.setText(obj.getDesignation().toString());
                        }
                    } catch (Exception ex) {
                        Log.v("TAG", "EXCEPTION : " + ex.getMessage());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void downloadImage() {
        if (getActivity() != null) {
            try {
                StorageReference reference = storageReference.child("Employees").child("ProfilePicture").child(fullName).child("images/" + fullName);
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        Picasso.with(getContext()).load(uri).placeholder(R.drawable.placeholder).resize(220,220).into(userPicture, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) userPicture.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.min(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                userPicture.setImageDrawable(imageDrawable);
                            }
                            @Override
                            public void onError() {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(getContext()).load(R.drawable.placeholder).into(userPicture);
                    }
                });
            } catch (Exception ex) {
                ex.getMessage();
            }
        }   else {
            return;
        }
    }
}