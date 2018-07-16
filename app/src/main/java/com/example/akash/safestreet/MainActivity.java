package com.example.akash.safestreet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener,LocationListener{
    TextView vehicle1,vehicle2,vehicle3,vehicle4,position1,position2,position3,update1,update2,update3,update4,update5,update6;
    Button button,settings;
    LinearLayout vehicleL1,vehicleL2,vehicleL3,vehicleL4;

    SensorManager sm;
    LocationManager lm;

    boolean updating=false;
    long prev=0,prev2=0,prev3=0;
    long traveltime=0;
    int level,scale;
    int adjustment;
    float battery;
    double bearing;
    double latitude,longitude,lastlatitude,lastlongitude,accuracy;
    double distance=0;
    double bump_thres=10.6,pot_thres=4,lowlimit=5,scaling=5,baselimit=5,finb,finp,speed;

    int events;

    private StorageReference storageReference;
    public static SharedPreferences shpref;
    public final static String ss="fname";
    public final static String k2="b";

    RandomAccessFile raf;
    File root;
    Calendar calendar ;
    SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy-hh_mm_ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vehicleL1=(LinearLayout)findViewById(R.id.twoL);
        vehicleL2=(LinearLayout)findViewById(R.id.threeL);
        vehicleL3=(LinearLayout)findViewById(R.id.fourL);
        vehicleL4=(LinearLayout)findViewById(R.id.sixL);
        vehicle1=(TextView)findViewById(R.id.two);
        vehicle2=(TextView)findViewById(R.id.three);
        vehicle3=(TextView)findViewById(R.id.four);
        vehicle4=(TextView)findViewById(R.id.six);
        position1=(TextView)findViewById(R.id.pocket);
        position2=(TextView)findViewById(R.id.mounter);
        position3=(TextView)findViewById(R.id.dashboard);
        button=(Button)findViewById(R.id.button);
        update1=(TextView)findViewById(R.id.text1);
        update2=(TextView)findViewById(R.id.text2);
        update3=(TextView)findViewById(R.id.text3);
        update4=(TextView)findViewById(R.id.text4);
        update5=(TextView)findViewById(R.id.text5);
        update6=(TextView)findViewById(R.id.text6);
        settings=(Button)findViewById(R.id.setting);
        Constants.vehiclestoreL=vehicleL1;
        Constants.vehiclestore=vehicle1;
        Constants.vehicle="TWO_WHEELER";
        vehicle1.setClickable(false);
        vehicle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.vehiclestoreL.setBackgroundResource(R.drawable.border_blue);
                Constants.vehiclestore.setClickable(true);
                vehicleL1.setBackgroundResource(R.drawable.border_green);
                vehicle1.setClickable(false);
                Constants.vehiclestoreL=vehicleL1;
                Constants.vehiclestore=vehicle1;
                Constants.vehicle="TWO_WHEELER";

            }
        });
        vehicle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.vehiclestoreL.setBackgroundResource(R.drawable.border_blue);
                Constants.vehiclestore.setClickable(true);
                vehicleL2.setBackgroundResource(R.drawable.border_green);
                vehicle2.setClickable(false);
                Constants.vehiclestoreL=vehicleL2;
                Constants.vehiclestore=vehicle2;
                Constants.vehicle="THREE_WHEELER";
            }
        });
        vehicle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.vehiclestoreL.setBackgroundResource(R.drawable.border_blue);
                Constants.vehiclestore.setClickable(true);
                vehicleL3.setBackgroundResource(R.drawable.border_green);
                vehicle3.setClickable(false);
                Constants.vehiclestoreL=vehicleL3;
                Constants.vehiclestore=vehicle3;
                Constants.vehicle="FOUR_WHEELER";
            }
        });
        vehicle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.vehiclestoreL.setBackgroundResource(R.drawable.border_blue);
                Constants.vehiclestore.setClickable(true);
                vehicleL4.setBackgroundResource(R.drawable.border_green);
                vehicle4.setClickable(false);
                Constants.vehiclestoreL=vehicleL4;
                Constants.vehiclestore=vehicle4;
                Constants.vehicle="SIX_WHEELER";
            }
        });
        position1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position1.setBackgroundResource(R.drawable.button_green);
                position1.setClickable(false);
                position2.setBackgroundResource(R.drawable.border_blue);
                position2.setClickable(true);
                position3.setBackgroundResource(R.drawable.border_blue);
                position3.setClickable(true);
                Constants.position="POCKET";
            }
        });
        position2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position2.setBackgroundResource(R.drawable.button_green);
                position2.setClickable(false);
                position1.setBackgroundResource(R.drawable.border_blue);
                position1.setClickable(true);
                position3.setBackgroundResource(R.drawable.border_blue);
                position3.setClickable(true);
                Constants.position="MOUNTER";
            }
        });
        position3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position3.setBackgroundResource(R.drawable.button_green);
                position3.setClickable(false);
                position1.setBackgroundResource(R.drawable.border_blue);
                position1.setClickable(true);
                position2.setBackgroundResource(R.drawable.border_blue);
                position2.setClickable(true);
                Constants.position="DASHBOARD";
            }
        });
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        storageReference = FirebaseStorage.getInstance().getReference();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,0x1);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,0x1);
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (!(Constants.position.equals(""))) {
                            updating = !updating;
                            if (updating) {
                                Intent batteryStatus = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                                level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                                scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                                battery = level / (float) scale;
                                sm.registerListener((SensorEventListener) MainActivity.this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                calendar=Calendar.getInstance();
                                Constants.starttime=String.valueOf(formatter.format(calendar.getTime()));
                                String path = Environment.getExternalStorageDirectory() + "/SafeStreet/" + Constants.vehicle + "_" + Constants.position + "_" + Constants.starttime + "_" + Constants.model+ "/";
                                root = new File(path);
                                if (!root.exists()) {
                                    root.mkdirs();
                                }
                                try {
                                    raf = new RandomAccessFile(root + "/All_Details.txt", "rw");
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                String whole = "bump:"+String.valueOf((float)bump_thres) + "," +"pothole:"+ String.valueOf((float)pot_thres) + "," +"basespeed:"+ String.valueOf((float)baselimit) + "," +"lowerspeed:"+ String.valueOf((float)lowlimit) + "," +"scaling:"+ String.valueOf((float)scaling)+"\n"+"x,y,z,latitude,longitude,bearing,aaccuracy,timestamp"+"\n";
                                try {
                                    raf.seek(raf.length());
                                    raf.write(whole.getBytes());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                traveltime = SystemClock.elapsedRealtime();
                                vehicle1.setClickable(false);vehicle2.setClickable(false);vehicle3.setClickable(false);vehicle4.setClickable(false);
                                position1.setClickable(false);position2.setClickable(false);position3.setClickable(false);
                                settings.setClickable(false);
                                settings.setVisibility(View.INVISIBLE);
                                button.setText("STOP");
                                button.setTextColor(Color.parseColor("#FFFFFFFF"));
                                button.setBackgroundResource(R.drawable.button_red);

                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setTitle("Alert!!");
                                alert.setMessage("Do you really want to stop?");
                                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sm.unregisterListener((SensorEventListener) MainActivity.this);
                                        try {
                                            raf.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        adjustment=0;
                                        prev = -900000;prev2 = -900000;prev3 = -900000;
                                        latitude=0;longitude=0;
                                        lastlatitude=0;lastlongitude=0;
                                        distance=0;accuracy=10000;speed=0;
                                        events=0;
                                        update3.setText(String.valueOf(events));
                                        vehicle1.setClickable(true);vehicle2.setClickable(true);vehicle3.setClickable(true);vehicle4.setClickable(true);
                                        position1.setClickable(true);position2.setClickable(true);position3.setClickable(true);
                                        settings.setClickable(true);
                                        settings.setVisibility(View.VISIBLE);
                                        button.setText("START");
                                        button.setTextColor(Color.parseColor("#FF000000"));
                                        button.setBackgroundResource(R.drawable.button_green);
                                        uploadFile();
                                    }
                                });
                                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updating = true;
                                    }
                                });
                                alert.show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Position of the phone required", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Storage permission required. Go to Settings->Apps->SafeStreet->Permissions->Storage->On", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Location should be turned On.",Toast.LENGTH_SHORT).show();
                }

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shpref=MainActivity.this.getSharedPreferences(ss,Context.MODE_PRIVATE);
                final Dialog thres=new Dialog(MainActivity.this);
                thres.setContentView(R.layout.threshold);
                String mount[]=(shpref.getString(k2," # # # # ")).split("#");
                final EditText bump=(EditText)thres.findViewById(R.id.bump_thres);if(!mount[0].equals(" ")){bump.setText(mount[0]);}
                final EditText pot=(EditText)thres.findViewById(R.id.pot_thres);if(!mount[1].equals(" ")){pot.setText(mount[1]);}
                final EditText low1=(EditText)thres.findViewById(R.id.low);if(!mount[2].equals(" ")){low1.setText(mount[2]);}
                final EditText scale1=(EditText)thres.findViewById(R.id.scale);if(!mount[3].equals(" ")){scale1.setText(mount[3]);}
                final EditText base1=(EditText)thres.findViewById(R.id.base);if(!mount[4].equals(" ")){base1.setText(mount[4]);}
                final Button confirmm=(Button)thres.findViewById(R.id.okk);
                final Button done=(Button)thres.findViewById(R.id.done);
                confirmm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!(bump.getText().toString().trim().equals("")) && !(pot.getText().toString().trim().equals("")) && !(low1.getText().toString().trim().equals("")) && !(scale1.getText().toString().trim().equals("")) && !(base1.getText().toString().trim().equals(""))) {
                            bump_thres = Double.parseDouble(bump.getText().toString().trim());
                            pot_thres = Double.parseDouble(pot.getText().toString().trim());
                            lowlimit = Double.parseDouble(low1.getText().toString().trim());
                            scaling = Double.parseDouble(scale1.getText().toString().trim());
                            baselimit = Double.parseDouble(base1.getText().toString().trim());
                            Toast.makeText(MainActivity.this, "bumper: " + String.valueOf(bump_thres) + "/pothole: " + String.valueOf(pot_thres), Toast.LENGTH_SHORT).show();
                            thres.dismiss();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Fields cannot be null",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!(bump.getText().toString().trim().equals("")) && !(pot.getText().toString().trim().equals("")) && !(low1.getText().toString().trim().equals("")) && !(scale1.getText().toString().trim().equals("")) && !(base1.getText().toString().trim().equals(""))) {
                            SharedPreferences.Editor editor = shpref.edit();
                            bump_thres = Double.parseDouble(bump.getText().toString().trim());
                            pot_thres = Double.parseDouble(pot.getText().toString().trim());
                            lowlimit = Double.parseDouble(low1.getText().toString().trim());
                            scaling = Double.parseDouble(scale1.getText().toString().trim());
                            baselimit = Double.parseDouble(base1.getText().toString().trim());
                            editor.putString(k2, String.valueOf(bump_thres) + "#" + String.valueOf(pot_thres) + "#" + String.valueOf(lowlimit) + "#" + String.valueOf(scaling) + "#" + String.valueOf(baselimit));
                            editor.commit();
                            Toast.makeText(MainActivity.this, "bumper: " + String.valueOf(bump_thres) + "/pothole: " + String.valueOf(pot_thres), Toast.LENGTH_SHORT).show();
                            thres.dismiss();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Fields cannot be null",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                thres.show();
            }
        });
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        }
    }
    float[] acceleration = new float[3];
    double[] ori = new double[2];
    double acc_final[]=new double[3];
    double cosa,sina,cosb,sinb;
    String filesize;
    char flag;
    @Override
    public void onSensorChanged(SensorEvent event) {
            flag='0';
            switch (event.sensor.getType()) {

                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(event.values, 0, acceleration,
                            0, acceleration.length);
                    break;
            }
            long timestamp=SystemClock.elapsedRealtime();
            float sum=(float)Math.sqrt((acceleration[0]*acceleration[0])+(acceleration[1]*acceleration[1])+(acceleration[2]*acceleration[2]));
            if((timestamp-prev)>850000)
            {
                adjustment=0;
                prev=timestamp;
            }
            if(Math.abs(sum-9.80)<=0.4 && adjustment<=10 && (timestamp-prev)<=1000) {
                adjustment++;
                ori[0] = Math.atan2(acceleration[1],acceleration[2]);
                ori[1] = Math.atan2(-1*acceleration[0],Math.sqrt((acceleration[1]*acceleration[1])+(acceleration[2]*acceleration[2])));
                cosa=Math.cos(ori[0]);sina= Math.sin(ori[0]);cosb=Math.cos(ori[1]);sinb=Math.sin(ori[1]);
            }
            acc_final[0]=(cosb*acceleration[0])+(sinb*sina*acceleration[1])+(cosa*sinb*acceleration[2]);
            acc_final[1]=(cosa*acceleration[1])-(sina*acceleration[2]);
            acc_final[2]=(-1*sinb*acceleration[0])+(cosb*sina*acceleration[1])+(cosb*cosa*acceleration[2]);

            if(lastlatitude!=0 && lastlongitude!=0 && latitude!=0 && longitude!=0)
            {
                Location location1=new Location(""),location2=new Location("");
                location1.setLatitude(latitude);location1.setLongitude(longitude);
                location2.setLatitude(lastlatitude);location2.setLongitude(lastlongitude);
                if(location2.distanceTo(location1)>1) {
                    bearing = location2.bearingTo(location1);
                    if (latitude != lastlatitude || longitude != lastlongitude) {
                        distance = distance + location2.distanceTo(location1);
                    }
                    lastlatitude = latitude;
                    lastlongitude = longitude;
                }

            }
            else
            {
                lastlatitude=latitude;
                lastlongitude=longitude;
            }
            if((speed*3.6)<baselimit)
            {
                finb=(bump_thres);
                finp=-1*(pot_thres);
            }
            else {
                finb = bump_thres + (((speed * 3.6) - lowlimit) * (scaling / 10));
                finp = -1 * (pot_thres  + (((speed * 3.6) - lowlimit) * (scaling / 10)));
            }
            if ((acc_final[2] > finb || acc_final[2]<finp) && (timestamp-prev3)>2000)
            {
                events++;
                update3.setText(String.valueOf(events));
                prev3=timestamp;
                flag='1';
            }
            String whole = String.valueOf((float)acc_final[0]) + "," + String.valueOf((float)acc_final[1]) + "," + String.valueOf((float)acc_final[2]) +","+ String.valueOf(latitude)+","+String.valueOf(longitude)+","+String.valueOf(bearing)+","+String.valueOf((float)accuracy)+","+String.valueOf(timestamp)+","+String.valueOf(flag)+"\n";
            try {
                raf.seek(raf.length());
                raf.write(whole.getBytes());
                filesize=getfilesize(raf.length());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if((timestamp-prev2)>10) {
                update1.setText(getDistance(distance)+" km.");
                update2.setText(getTraveltime(timestamp));
                update4.setText(filesize+" Mb");
                update5.setText(getBatteryconsumed(battery)+" %");
                update6.setText(getSpeed(speed)+" km/s");
                prev2=timestamp;
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        accuracy=location.getAccuracy();
        speed=location.getSpeed();
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

    String getTraveltime(long timestamp)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        if(((timestamp-traveltime)/(1000.0*60.0))>100)
        {
            return df.format((timestamp-traveltime)/(1000.0*60.0*60)) + " hours";
        }
        return df.format((timestamp-traveltime)/(1000.0*60.0)) + " minutes";
    }
    String getfilesize(long size)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format((double)size/(1024.0*1024.0));
    }
    String getBatteryconsumed(double battery)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        scale=batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        return df.format((battery-(level/(float)scale))*100);
    }
    String getDistance(double distance)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(distance/1000.0);
    }
    String getSpeed(double speed)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(speed*3.6);
    }
    private void uploadFile() {
        String path=Constants.vehicle + "_" + Constants.position + "_" + Constants.starttime + "_" + Constants.model+ "/" + "All_Details.txt";
        Uri filePath=Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/SafeStreet/" + path));
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading first file...");
            progressDialog.show();

            StorageReference riversRef = storageReference.child(path);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "File uploaded to the server successfully", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            String linebar="";
                            for(int i=0;i<(int)progress/10;i++)
                                linebar=linebar+"-";
                            progressDialog.setMessage("Uploaded" + linebar + ((int) progress) + "%");
                        }
                    });
        }
        else {
        }
    }
}
