package com.example.auto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_USERNAME = "sharedUsername";
    public static final String SHARED_CAR_DATA_ = "sharedCarData_";
    private static final String LOCAL_HOST = "http://192.168.1.2";
    private ScheduledExecutorService executor;

    Button Menu;
    Notifications notification;
    ImageButton AddCar, Profile, Home, Logo, Notifications;
    TextView Debug;
    RecyclerView Cars;
    ScrollView layout;
    String username;
    List<String> plate_list;
    CarViewModel car_view_model;
    CardAdapter adapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request_permissions();
        defines();
        get_and_share_username();
        Cars.setAdapter(adapter);
        get_and_display_cars();

        run_notifications();


        AddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCar.class);
                intent.putExtra("username", username);
                //launcher.launch(intent);
                startActivity(intent);
            }
        });
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.show_notification(getApplicationContext(), "Test 1","message");
            }
        });
    }

    protected void onResume() {
        super.onResume();
        get_and_display_cars();

    }


    //FUNCTION SECTION BEGIN
    private void request_permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myChannelID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void defines() {
        car_view_model = new ViewModelProvider(this).get(CarViewModel.class);
        layout = (ScrollView) findViewById(R.id.scrollViewMain);
        AddCar = (ImageButton) findViewById(R.id.imageButtonAddNewCar);
        Profile = (ImageButton) findViewById(R.id.imageButtonMainProfile);
        Home = (ImageButton) findViewById(R.id.imageButtonMainHome);
        Logo = (ImageButton) findViewById(R.id.imageButtonMainLogo);
        Notifications = (ImageButton) findViewById(R.id.imageButtonMainNotification);
        Cars = (RecyclerView) findViewById(R.id.recyclerViewListCars);
        Debug = (TextView) findViewById(R.id.textViewDebug);
        Cars.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter(getApplicationContext(), new ArrayList<>());
        notification = new Notifications();
        context = getApplicationContext();
    }

    private void get_and_share_username() {
        username = getIntent().getStringExtra("username");
        SharedPreferences sharedPreferences_username = getSharedPreferences(SHARED_USERNAME, MODE_PRIVATE);
        sharedPreferences_username.edit().putString("username", username).apply();
        car_view_model.set_username(username);
    }

    private void get_and_display_cars() {
        car_view_model.get_cars_for_user(username);
        car_view_model.get_car_list().observe(this, car_list -> {
            Log.d("LiveDataCheck", "Items changed: " + car_list.size());
            adapter.setItems(car_list);
        });
    }

    private void run_notifications() {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Data input_username = new Data.Builder()
                .putString("username",username)
                        .build();

        /*
        mainHandler.post(() -> {
            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInputData(input_username)
                    .build();

            WorkManager.getInstance(this).enqueue(uploadWorkRequest);
            Log.d("OneTimeRequest", "Notificaion");
        });
        //*/
        ///*
        mainHandler.post(()->{
            Log.i("TEST","Starting PeriodicWorkRequest");
            PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class, 15, TimeUnit.MINUTES)
                    .setInputData(input_username)
                    .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "NotificationWork",
                    ExistingPeriodicWorkPolicy.KEEP, // or REPLACE
                    periodicRequest
            );
        });
        //*/


        /*
        car_view_model.get_car_list().observe(this, car_list -> {
            String today = LocalDate.now().toString();
            String three_days_from_today = LocalDate.now().plusDays(3).toString();
            String one_week_from_today = LocalDate.now().plusWeeks(1).toString();
            String title;
            String message;
            for(Car car : car_list){
                //Insurance
                if(car.get_insurance().equals(today)){
                    title = String.format("Insurance is ending today");
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_insurance().equals(three_days_from_today)){
                    title = String.format("Insurance will end on %s", three_days_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_insurance().equals(one_week_from_today)){
                    title = String.format("Insurance will end on %s", one_week_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                }
                //Vignette
                if(car.get_vignette().equals(today)){
                    title = String.format("Vignette is ending today");
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_vignette().equals(three_days_from_today)){
                    title = String.format("Vignette will end on %s", three_days_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_vignette().equals(one_week_from_today)){
                    title = String.format("Vignette will end on %s", one_week_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                }
                //Inspection
                if(car.get_inspection().equals(today)){
                    title = String.format("Inspection is ending today");
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_inspection().equals(three_days_from_today)){
                    title = String.format("Inspection will end on %s", three_days_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                } else if(car.get_inspection().equals(one_week_from_today)){
                    title = String.format("Inspection will end on %s", one_week_from_today);
                    message = String.format("[%s]",car.get_model_plus_platenumber());
                    notification.show_notification(context,title, message);
                }
            }

        });


        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {

            Log.d("ScheduledExecutor", "Notificaion 1");
            mainHandler.post(() -> {
                car_view_model.get_cars_for_user(username);
            });
        }, 5, 60, TimeUnit.SECONDS);

         */

    }

}