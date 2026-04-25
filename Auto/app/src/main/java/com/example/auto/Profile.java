package com.example.auto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Base64;

public class Profile extends AppCompatActivity {

    ImageView Avatar;
    Bitmap bitmap, decodedBitmap;
    Button LogOut;
    ImageButton Logo, AddPhoto, Home;
    TextView Fullname, Username, Email;
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String LOCAL_HOST = "http://192.168.8.2";

    ActivityResultLauncher<Intent> activityResultLauncher;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        Avatar = (ImageView) findViewById(R.id.imageViewProfileAvatar);
        Fullname = (TextView) findViewById(R.id.textViewProfileFullname);
        Username = (TextView) findViewById(R.id.textViewProfileUsername);
        Email = (TextView) findViewById(R.id.textViewProfileEmail);
        LogOut = (Button) findViewById(R.id.buttonProfileLogOut);
        Logo = (ImageButton) findViewById(R.id.imageButtonMainLogo);
        AddPhoto = (ImageButton) findViewById(R.id.imageButtonProfileAddPhoto);
        Home = (ImageButton) findViewById(R.id.imageButtonProfileHome);

        String username = getIntent().getStringExtra("username");

        registerImage();


        //Get User Data begin
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "username";
                String[] data = new String[1];
                data[0] = username;
                PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/get_user_data.php"), "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();

                        try {
                            JSONObject object = new JSONObject(result);
                            String name = object.getString("fullname");
                            String email = object.getString("email");
                            String image = object.getString("image");


                            if (!image.isEmpty()){
                                //Base64.Decoder decoder = Base64.getUrlDecoder();

                                byte[] decodedByteArray = Base64.getDecoder().decode(image);
                                decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                                Avatar.setImageBitmap(decodedBitmap);
                            }
                            Fullname.setText(name);
                            Username.setText(username);
                            Email.setText(email);

                            editor.putString("fullname", name);
                            editor.putString("email", email);
                            editor.putString("image", image);

                            editor.apply();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Data Retrieve Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        //Get User Data end


        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        //Logo
        Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //Avatar
        Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // LogOut
        LogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("name", false);
                editor.apply();

                Intent intent = new Intent(Profile.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


    }



    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        activityResultLauncher.launch(intent);
    }


    //Bitmap begin
    private void registerImage() {
        activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            String username = getIntent().getStringExtra("username");

                            Intent data = result.getData();
                            Uri uri = data.getData();
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                Avatar.setImageBitmap(bitmap);

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                byte[] bytes = byteArrayOutputStream.toByteArray();
                                final String base64Image = Base64.getEncoder().encodeToString(bytes);

                                Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String[] field = new String[2];
                                        field[0] = "username";
                                        field[1] = "image";
                                        String[] data = new String[2];
                                        data[0] = username;
                                        data[1] = base64Image;
                                        PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/insert_profile_image.php"), "POST", field, data);
                                        if (putData.startPut()) {
                                            if (putData.onComplete()) {
                                                String result = putData.getResult();
                                                if (result.equals("Image Import Success")){
                                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(), "Unsuccessful Image Import!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
    //Bitmap end

}
