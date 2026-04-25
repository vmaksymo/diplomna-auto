package com.example.auto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LogIn extends AppCompatActivity  {

    Button LogIn;
    CheckBox RememberMe;
    EditText UsernameLogin, PasswordLogin;
    TextView SignUp;

    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String LOCAL_HOST = "http://192.168.1.2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        UsernameLogin = (EditText) findViewById(R.id.editTextLoginUsername);
        PasswordLogin = (EditText) findViewById(R.id.editTextLoginPassword);

        LogIn = (Button) findViewById(R.id.buttonLoginLogIn);
        SignUp = (TextView) findViewById(R.id.textViewLoginSignUp);

        RememberMe = (CheckBox) findViewById(R.id.checkBoxLogin);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        boolean check = sharedPreferences.getBoolean("name", false);
        String usernameRemember = sharedPreferences.getString("username", "");
        final boolean[] remember = {false};
        final String[] usernameRemembered = new String[1];
        if (check){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("username", usernameRemember);
            startActivity(intent);
            finish();
        }



        RememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (RememberMe.isChecked())
                    remember[0] = true;
                else
                    remember[0] = false;

            }
        });


        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = UsernameLogin.getText().toString();
                String password = PasswordLogin.getText().toString();

                if (username.isEmpty() || password.isEmpty())
                    Toast.makeText(LogIn.this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
                else {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = password;
                            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/log_in.php"), "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    if (result.equals("Login Success")) {
                                        if(remember[0]) {
                                            editor.putBoolean("name", true);
                                            editor.putString("username", username);
                                            editor.apply();
                                            usernameRemembered[0] = username;
                                        }
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Unsuccessful Log In!", Toast.LENGTH_SHORT).show();
                                        editor.putBoolean("name", false);
                                    }
                                }
                            }
                            //End Write and Read data with URL
                        }

                    });
                }
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(LogIn.this, SignUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

}
